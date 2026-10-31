/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2026 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.blocks;

import static net.imglib2.blocks.PrimitiveBlocksUtils.extractOobValue;

import java.util.function.Supplier;

import net.imglib2.Interval;
import net.imglib2.Volatile;
import net.imglib2.img.basictypeaccess.nio.BufferAccess;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveType;
import net.imglib2.util.CloseableThreadLocal;
import net.imglib2.util.Intervals;

class VolatileViewPrimitiveBlocks< T extends Volatile< ? > & NativeType< T >, R extends Volatile< ? > & NativeType< R > > implements VolatilePrimitiveBlocks< T >
{
	private final ViewProperties< T, R > props;

	// copies from view root. root type primitive equivalent
	private final VolatileRangeCopier copier;

	// root primitive type
	private final TempArray< R > tempArrayPermute;

	// root primitive type
	private final TempArray< R > tempArrayConvert;

	private final PermuteInvert permuteInvert;

	private final Convert convert;


	private final PermuteInvert permuteInvertValid;

	private Supplier< VolatilePrimitiveBlocks< T > > threadSafeSupplier;

	public VolatileViewPrimitiveBlocks( final ViewProperties< T, R > props )
	{
		this.props = props;
		final PrimitiveType primitiveType = props.getRootType().getNativeTypeFactory().getPrimitiveType();
		final MemCopy memCopy = MemCopy.forPrimitiveType( primitiveType, props.getRoot().getAccessType() instanceof BufferAccess, false );
		// The oob value is always extracted into a primitive array, never a
		// Buffer, so filling it needs a MemCopy with a primitive array source.
		final MemCopy fillCopy = MemCopy.forPrimitiveType( primitiveType, false, false );
		final MemCopy memCopyValid = MemCopy.forPrimitiveType( PrimitiveType.BYTE );
		final Extension extension = props.getExtension() != null ? props.getExtension() : Extension.border();
		final Object oob = extractOobValue( props.getRootType(), extension );
		final Ranges findRanges = Ranges.forExtension( extension );
		copier = VolatileRangeCopier.create( props.getRoot(), findRanges, memCopy, fillCopy, oob );
		tempArrayConvert = TempArray.forPrimitiveType( primitiveType );
		tempArrayPermute = TempArray.forPrimitiveType( primitiveType );
		permuteInvert = new PermuteInvert( memCopy, props.getPermuteInvertTransform() );
		permuteInvertValid = new PermuteInvert( memCopyValid, props.getPermuteInvertTransform() );
		convert = props.hasConverterSupplier()
				? Convert.create( props.getRootType(), props.getViewType(), props.getConverterSupplier() )
				: null;
	}

	@Override
	public T getType()
	{
		return props.getViewType();
	}

	@Override
	public int numDimensions()
	{
		return props.getViewNumDimensions();
	}

	private BlockInterval getTransformedInterval( final BlockInterval interval )
	{
		if ( !props.hasTransform() )
			return interval;

		final long[] srcPos = interval.min();
		final int[] srcSize = interval.size();
		final MixedTransform transform = props.getTransform();
		final int n = transform.numTargetDimensions();
		final BlockInterval destInterval = new BlockInterval( n );
		final long[] destPos = destInterval.min();
		final int[] destSize = destInterval.size();
		for ( int d = 0; d < n; d++ )
		{
			final int t = ( int ) transform.getTranslation( d );
			if ( transform.getComponentZero( d ) )
			{
				destPos[ d ] = t;
				destSize[ d ] = 1;
			}
			else
			{
				final int c = transform.getComponentMapping( d );
				destPos[ d ] = transform.getComponentInversion( d )
						? t - srcPos[ c ] - srcSize[ c ] + 1
						: t + srcPos[ c ];
				destSize[ d ] = srcSize[ c ];
			}
		}
		return destInterval;
	}

	/**
	 * Copy a block from the ({@code T}-typed) source into a {@link
	 * VolatileArray}.
	 *
	 * @param interval
	 * 		position and size of the block to copy
	 * @param dest
	 * 		{@link VolatileArray} to copy into. Its {@link VolatileArray#data()
	 * 		data} must correspond to {@code T}, for example, if {@code T} is
	 * 		{@code VolatileUnsignedByteType} then it must be {@code byte[]}. Its
	 * 		validity ({@link VolatileArray#allValid()}, {@link
	 * 		VolatileArray#valid()}) is set to the {@link Volatile#isValid()
	 * 		validity} of the copied block.
	 * <p>
	 * Pixels that come from an invalid {@code Cell} are set to zero, instead of
	 * copying the meaningless data of the invalid {@code Cell}. Note that the
	 * zeros are written in the root type, before conversion: with a converter
	 * in the chain, such a pixel ends up as {@code converter(0)}, not
	 * necessarily {@code 0}.
	 */
	@Override
	public void copy( final Interval interval, final VolatileArray< ? > dest )
	{
		final BlockInterval blockInterval = BlockInterval.asBlockInterval( interval );
		final int[] size = blockInterval.size();
		final int length = ( int ) Intervals.numElements( size );

		final BlockInterval destInterval = getTransformedInterval( blockInterval );
		final long[] destPos = destInterval.min();
		final int[] destSize = destInterval.size();

		final boolean doPermute = props.hasPermuteInvertTransform();
		final boolean doConvert = props.hasConverterSupplier();

		// TODO: Revisit conversion. I'm not sure whether it is relevant for the Volatile case.

		if ( doPermute && doConvert )
		{
			final Object copyDest = tempArrayPermute.get( length );
			final Object permuteDest = tempArrayConvert.get( length );
			final RangeValidity validity = copier.copy( destPos, copyDest, destSize );
			permuteInvert.permuteAndInvert( copyDest, permuteDest, size );
			dest.setValidity( permuteInvertValid.permuteAndInvert( validity ) );
			convert.convert( permuteDest, dest.data(), length );
		}
		else if ( doPermute )
		{
			final Object copyDest = tempArrayConvert.get( length );
			final RangeValidity validity = copier.copy( destPos, copyDest, destSize );
			permuteInvert.permuteAndInvert( copyDest, dest.data(), size );
			dest.setValidity( permuteInvertValid.permuteAndInvert( validity ) );
		}
		else if ( doConvert )
		{
			final Object copyDest = tempArrayPermute.get( length );
			dest.setValidity( copier.copy( destPos, copyDest, destSize ) );
			convert.convert( copyDest, dest.data(), length );
		}
		else
		{
			dest.setValidity( copier.copy( destPos, dest.data(), destSize ) );
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Equivalent to {@code copy(interval, …)} followed by {@link
	 * Validity#allValid()}, but it only looks at the validity of the {@code
	 * Cell}s the block reads from, so it costs one check per cell instead of a
	 * block copy.
	 * <p>
	 * Neither the permutation nor the converter of a View affects the answer: a
	 * permutation is a bijection on pixels, and conversion is per-pixel, so it
	 * is the answer for the corresponding block of the root image.
	 *
	 * @param failFast
	 * 		if {@code true}, return as soon as an invalid {@code Cell} is found. If
	 * 		{@code false}, every {@code Cell} the block reads from is visited,
	 * 		which for a cached source has the side effect of requesting all of
	 * 		them.
	 */
	@Override
	public AllValid allValid( final Interval interval, final boolean failFast )
	{
		final BlockInterval blockInterval = BlockInterval.asBlockInterval( interval );
		final BlockInterval destInterval = getTransformedInterval( blockInterval );
		// the copier always knows: it looks at the Cells themselves
		return AllValid.of( copier.allValid( destInterval.min(), destInterval.size(), failFast ) );
	}

	@Override
	public VolatilePrimitiveBlocks< T > threadSafe()
	{
		if ( threadSafeSupplier == null )
			threadSafeSupplier = CloseableThreadLocal.withInitial( this::independentCopy )::get;
		return new VolatilePrimitiveBlocks< T >()
		{
			@Override
			public T getType()
			{
				return props.getViewType();
			}

			@Override
			public int numDimensions()
			{
				return props.getViewNumDimensions();
			}

			@Override
			public void copy( final Interval interval, final VolatileArray< ? > dest )
			{
				threadSafeSupplier.get().copy( interval, dest );
			}

			@Override
			public AllValid allValid( final Interval interval, final boolean failFast )
			{
				return threadSafeSupplier.get().allValid( interval, failFast );
			}

			@Override
			public VolatilePrimitiveBlocks< T > independentCopy()
			{
				return VolatileViewPrimitiveBlocks.this.independentCopy().threadSafe();
			}

			@Override
			public VolatilePrimitiveBlocks< T > threadSafe()
			{
				return this;
			}
		};
	}

	@Override
	public VolatilePrimitiveBlocks< T > independentCopy()
	{
		return new VolatileViewPrimitiveBlocks<>( this );
	}

	private VolatileViewPrimitiveBlocks( final VolatileViewPrimitiveBlocks< T, R > blocks )
	{
		props = blocks.props;
		copier = blocks.copier.newInstance();
		permuteInvert = blocks.permuteInvert.newInstance();
		convert = blocks.convert == null ? null : blocks.convert.newInstance();
		tempArrayConvert = blocks.tempArrayConvert.newInstance();
		tempArrayPermute = blocks.tempArrayPermute.newInstance();
		permuteInvertValid = blocks.permuteInvertValid.newInstance();
	}
}
