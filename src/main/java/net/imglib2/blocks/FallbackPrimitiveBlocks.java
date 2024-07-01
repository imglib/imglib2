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

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Cast;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

class FallbackPrimitiveBlocks< T extends NativeType< T >, A extends ArrayDataAccess< A > > implements PrimitiveBlocks< T >
{
	private final RandomAccessible< T > source;

	private final T type;

	private final PrimitiveTypeProperties< ?, A > primitiveTypeProperties;

	private final NativeTypeFactory< T, A > nativeTypeFactory;

	public FallbackPrimitiveBlocks( final FallbackProperties< T > props )
	{
		this( props.getView(), props.getViewType() );
	}

	public FallbackPrimitiveBlocks( final RandomAccessible< T > source, final T type )
	{
		this.source = source;
		this.type = type;

		if ( type.getEntitiesPerPixel().getRatio() != 1 )
			throw new IllegalArgumentException( "Types with entitiesPerPixel != 1 are not supported" );

		nativeTypeFactory = Cast.unchecked( type.getNativeTypeFactory() );
		primitiveTypeProperties = Cast.unchecked( PrimitiveTypeProperties.get( nativeTypeFactory.getPrimitiveType() ) );
	}

	@Override
	public T getType()
	{
		return type;
	}

	@Override
	public int numDimensions()
	{
		return source.numDimensions();
	}

	@Override
	public void copy( final long[] srcPos, final Object dest, final int[] size )
	{
		final ArrayImg< T, A > img = new ArrayImg<>( primitiveTypeProperties.wrap( dest ), Util.int2long( size ), type.getEntitiesPerPixel() );
		img.setLinkedType( nativeTypeFactory.createLinkedType( img ) );
		final FinalInterval interval = FinalInterval.createMinSize( srcPos, Util.int2long( size ) );
		LoopBuilder.setImages( Views.interval( source, interval ), img ).forEachPixel( ( a, b ) -> b.set( a ) );
	}

	@Override
	public PrimitiveBlocks< T > independentCopy()
	{
		// NB FallbackPrimitiveBlocks is stateless
		return this;
	}

	@Override
	public PrimitiveBlocks< T > threadSafe()
	{
		return this;
	}
}
