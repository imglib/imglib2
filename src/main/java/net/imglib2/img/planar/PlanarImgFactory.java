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

package net.imglib2.img.planar;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;

/**
 * Factory that creates an appropriate {@link PlanarImg}.
 *
 * @author Tobias Pietzsch
 * @author Jan Funke
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Johannes Schindelin
 */
public class PlanarImgFactory< T extends NativeType< T > > extends NativeImgFactory< T >
{
	@Override
	public PlanarImg< T, ? > create( final long[] dimensions, final T type )
	{
		return create( type.getPrimitiveTypeInfo(), dimensions, type.getEntitiesPerPixel() );
	}

	private < A > PlanarImg< T, ? > create(
			final PrimitiveTypeInfo< T, A > info,
			final long[] dimensions,
			final Fraction entitiesPerPixel )
	{
		return create( ArrayDataAccessFactory.get( info ).createArray( 0 ), info, dimensions, entitiesPerPixel );
		// calling createArray( 0 ) is necessary here, because otherwise javac
		// will not infer the ArrayDataAccess type

	}

	private < A extends ArrayDataAccess< A > > PlanarImg< T, ? > create(
			final A creator,
			final PrimitiveTypeInfo< T, ? super A > info,
			final long[] dimensions,
			final Fraction entitiesPerPixel )
	{
		final PlanarImg< T, A > img = new PlanarImg<>( creator, dimensions, entitiesPerPixel );
		img.setLinkedType( info.createLinkedType( img ) );
		return img;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new PlanarImgFactory();
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
