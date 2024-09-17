/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.img.basictypeaccess.nio.BufferAccess;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveType;
import net.imglib2.util.Cast;
import net.imglib2.util.CloseableThreadLocal;
import net.imglib2.util.Intervals;

class ViewPrimitiveBlocks< T extends NativeType< T >, R extends NativeType< R > > implements PrimitiveBlocks< T >
{
	private final ViewProperties< T, R > props;

	// copies from view root. root type primitive equivalent
	private final RangeCopier copier;

	// root primitive type
	private final TempArray< R > tempArrayPermute;

	// root primitive type
	private final TempArray< R > tempArrayConvert;

	private final PermuteInvert permuteInvert;

	private final Convert convert;

	private Supplier< PrimitiveBlocks< T > > threadSafeSupplier;

	public ViewPrimitiveBlocks( final ViewProperties< T, R > props )
	{
		this.props = props;
		final PrimitiveType primitiveType = props.getRootType().getNativeTypeFactory().getPrimitiveType();
		final MemCopy memCopy = MemCopy.forPrimitiveType( primitiveType, props.getRoot().getAccessType() instanceof BufferAccess, false );
		final Extension extension = props.getExtension() != null ? props.getExtension() : Extension.border();
		final Object oob = extractOobValue( props.getRootType(), extension );
		final Ranges findRanges = Ranges.forExtension( extension );
		copier = RangeCopier.create( props.getRoot(), findRanges, memCopy, oob );
		tempArrayConvert = Cast.unchecked( TempArray.forPrimitiveType( primitiveType ) );
		tempArrayPermute = Cast.unchecked( TempArray.forPrimitiveType( primitiveType ) );
		permuteInvert = new PermuteInvert( memCopy, props.getPermuteInvertTransform() );
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

	/**
	 * @param srcPos
	 * 		min coordinates of block to copy from src Img.
	 * @param dest
	 * 		destination array. Type is {@code byte[]}, {@code float[]},
	 * 		etc, corresponding to the src Img's native type.
	 * @param size
	 * 		dimensions of block to copy from src Img.
	 */
	public void copy( final long[] srcPos, final Object dest, final int[] size )
	{
		final long[] destPos;
		final int[] destSize;
		if ( props.hasTransform() )
		{
			final MixedTransform transform = props.getTransform();
			final int n = transform.numTargetDimensions();
			destPos = new long[ n ];
			destSize = new int[ n ];
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
							? t - srcPos[ c ] - size[ c ] + 1
							: t + srcPos[ c ];
					destSize[ d ] = size[ c ];
				}
			}
		}
		else
		{
			destPos = srcPos;
			destSize = size;
		}

		final boolean doPermute = props.hasPermuteInvertTransform();
		final boolean doConvert = props.hasConverterSupplier();
		final int length = ( int ) Intervals.numElements( size );
		if ( doPermute && doConvert )
		{
			final Object copyDest = tempArrayPermute.get( length );
			final Object permuteDest = tempArrayConvert.get( length );
			copier.copy( destPos, copyDest, destSize );
			permuteInvert.permuteAndInvert( copyDest, permuteDest, size );
			convert.convert( permuteDest, dest, length );
		}
		else if ( doPermute )
		{
			final Object copyDest = tempArrayConvert.get( length );
			copier.copy( destPos, copyDest, destSize );
			permuteInvert.permuteAndInvert( copyDest, dest, size );
		}
		else if ( doConvert )
		{
			final Object copyDest = tempArrayPermute.get( length );
			copier.copy( destPos, copyDest, destSize );
			convert.convert( copyDest, dest, length );
		}
		else
		{
			copier.copy( destPos, dest, destSize );
		}
	}

	@Override
	public PrimitiveBlocks< T > threadSafe()
	{
		if ( threadSafeSupplier == null )
			threadSafeSupplier = CloseableThreadLocal.withInitial( this::independentCopy )::get;
		return new PrimitiveBlocks< T >()
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
			public void copy( final long[] srcPos, final Object dest, final int[] size )
			{
				threadSafeSupplier.get().copy( srcPos, dest, size );
			}

			@Override
			public PrimitiveBlocks< T > independentCopy()
			{
				return ViewPrimitiveBlocks.this.independentCopy().threadSafe();
			}

			@Override
			public PrimitiveBlocks< T > threadSafe()
			{
				return this;
			}
		};
	}

	@Override
	public PrimitiveBlocks< T > independentCopy()
	{
		return new ViewPrimitiveBlocks<>( this );
	}

	private ViewPrimitiveBlocks( final ViewPrimitiveBlocks< T, R > blocks )
	{
		props = blocks.props;
		copier = blocks.copier.newInstance();
		permuteInvert = blocks.permuteInvert.newInstance();
		convert = blocks.convert == null ? null : blocks.convert.newInstance();
		tempArrayConvert = blocks.tempArrayConvert.newInstance();
		tempArrayPermute = blocks.tempArrayPermute.newInstance();
	}
}
