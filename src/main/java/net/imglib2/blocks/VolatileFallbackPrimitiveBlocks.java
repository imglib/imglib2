/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.util.Cast;
import net.imglib2.view.Views;

/**
 * Fall-back {@code VolatilePrimitiveBlocks}, copying pixel
 * by pixel with {@code LoopBuilder}. The volatile counterpart of {@link
 * FallbackPrimitiveBlocks}.
 * <p>
 * The validity of {@code dest} is written
 * into {@link VolatileArray#validForWriting()}, so that the result is a {@link
 * DenseValidity}.
 * Its data is written through the non-volatile {@link Volatile#get() value} type,
 * and invalid pixels are written as zero ({@code Volatile.get().createVariable()}.
 * <p>
 * NB: This requires the {@code Volatile} type to wrap a {@code NativeType} with
 * the same primitive type. This is currently not checked, because it is true
 * for all {@code Volatile} {@code NativeType}s
 *
 * @param <V>
 * 		volatile pixel type
 * @param <T>
 * 		the wrapped non-volatile pixel type
 * 		(actually {@code V extends Volatile<T>} but to simplify callers it
 * 		is not declared).
 */
class VolatileFallbackPrimitiveBlocks< V extends Volatile< ? > & NativeType< V >, T extends NativeType< T >, A extends ArrayDataAccess< A > > implements VolatilePrimitiveBlocks< V >
{
	private final RandomAccessible< V > source;

	private final V type;
	private final T baseType;
	private final PrimitiveTypeProperties< ?, A > baseTypeProperties;
	private final NativeTypeFactory< T, A > baseTypeFactory;

	public VolatileFallbackPrimitiveBlocks( final FallbackProperties< V > props )
	{
		this.source = props.getView();
		this.type = props.getViewType();

		if ( type.getEntitiesPerPixel().getRatio() != 1 )
			throw new IllegalArgumentException( "Types with entitiesPerPixel != 1 are not supported" );

		baseType = Cast.unchecked( type.get() );
		baseTypeFactory = Cast.unchecked( baseType.getNativeTypeFactory() );
		baseTypeProperties = Cast.unchecked( PrimitiveTypeProperties.get( baseTypeFactory.getPrimitiveType() ) );
	}

	@Override
	public V getType()
	{
		return type;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public void copy( final Interval interval, final VolatileArray< ? > vdest )
	{
		final long[] dims = interval.dimensionsAsLongArray();
		final ArrayImg< T, A > img = new ArrayImg<>( baseTypeProperties.wrap( vdest.data() ), dims, baseType.getEntitiesPerPixel() );
		img.setLinkedType( baseTypeFactory.createLinkedType( img ) );
		final ArrayImg< ByteType, ByteArray > valid = ArrayImgs.bytes( vdest.validForWriting(), dims );
		final T zero = baseType.createVariable();
		LoopBuilder.setImages( Views.interval( source, interval ), img, valid ).forEachPixel( ( s, d, v ) -> {
			if(s.isValid())
			{
				d.set( ( T ) s.get() );
				v.setByte( ( byte ) 1 );
			} else {
				d.set( zero );
				v.setByte( ( byte ) 0 );
			}
		} );
	}

	/**
	 * Inspects every pixel. With {@code failFast}, stops at the first invalid
	 * one; otherwise every pixel is visited, which for a cached source requests
	 * all the data the block reads from.
	 */
	@Override
	public AllValid allValid( final Interval interval, final boolean failFast )
	{
		boolean allValid = true;
		for ( final V s : Views.flatIterable( Views.interval( source, interval ) ) )
		{
			if ( !s.isValid() )
			{
				allValid = false;
				if ( failFast )
					break;
			}
		}
		return AllValid.of( allValid );
	}

	@Override
	public VolatilePrimitiveBlocks< V > independentCopy()
	{
		// NB VolatileFallbackPrimitiveBlocks is stateless
		return this;
	}

	@Override
	public VolatilePrimitiveBlocks< V > threadSafe()
	{
		return this;
	}
}
