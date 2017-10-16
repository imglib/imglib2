/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.array;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;

/**
 * Factory for {@link ArrayImg}s.
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ArrayImgFactory< T extends NativeType< T > > extends NativeImgFactory< T >
{
	@Override
	public ArrayImg< T, ? > create( final long[] dimensions, final T type )
	{
		return create( dimensions, type, type.getPrimitiveTypeInfo() );
	}

	private < A > ArrayImg< T, A > create( final long[] dimensions, final T type, final PrimitiveTypeInfo< T, A > info )
	{
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );
		final A data = ArrayDataAccessFactory.get( info ).createArray( numEntities );
		final ArrayImg< T, A > img = new ArrayImg<>( data, dimensions, entitiesPerPixel );
		img.setLinkedType( info.createLinkedType( img ) );
		return img;
	}

	public static int numEntitiesRangeCheck( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final long numEntities = entitiesPerPixel.mulCeil( AbstractImg.numElements( dimensions ) );

		if ( numEntities > Integer.MAX_VALUE )
			throw new RuntimeException( "Number of elements in ArrayImg too big, use for example CellImg instead: " + numEntities + " > " + Integer.MAX_VALUE );

		return ( int ) numEntities;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new ArrayImgFactory();
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
