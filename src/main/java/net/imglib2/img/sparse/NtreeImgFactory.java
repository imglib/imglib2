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

package net.imglib2.img.sparse;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;

/**
 * @author Tobias Pietzsch
 *
 */
public class NtreeImgFactory< T extends NativeType< T > > extends NativeImgFactory< T >
{
	@Override
	public NtreeImg< T, ? extends NtreeAccess< ?, ? > > create( final long[] dimensions, final T type )
	{
		return create( dimensions, type, type.getPrimitiveTypeInfo() );
	}

	private < A > NtreeImg< T, ? > create( final long[] dimensions, final T type, final PrimitiveTypeInfo< T, A > info )
	{
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		final long[] pos = new long[ dimensions.length ];
		final NtreeImg< T, ? extends A > img = new NtreeImg<>(
				createNtreeAccess( info, dimensions ).createInstance( pos ),
				// calling createInstance(pos) is necessary here, because
				// otherwise javac will not infer the NtreeAccess type
				dimensions,
				entitiesPerPixel );
		img.setLinkedType( info.createLinkedType( img ) );
		return img;
	}

	@SuppressWarnings( "unchecked" )
	public static < A extends NtreeAccess< ?, A > > A createNtreeAccess(
			final PrimitiveTypeInfo< ?, ? super A > primitiveType,
			final long[] dimensions )
	{
		switch ( primitiveType.getPrimitiveType() )
		{
		case BYTE:
			return ( A ) new ByteNtree( dimensions, null, ( byte ) 0 );
		case CHAR:
			return ( A ) new CharNtree( dimensions, null, ( char ) 0 );
		case DOUBLE:
			return ( A ) new DoubleNtree( dimensions, null, 0 );
		case FLOAT:
			return ( A ) new FloatNtree( dimensions, null, 0 );
		case INT:
			return ( A ) new IntNtree( dimensions, null, 0 );
		case LONG:
			return ( A ) new LongNtree( dimensions, null, 0 );
		case SHORT:
			return ( A ) new ShortNtree( dimensions, null, ( short ) 0 );
		default:
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new NtreeImgFactory();
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
