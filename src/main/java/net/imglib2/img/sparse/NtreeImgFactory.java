/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
import net.imglib2.util.Fraction;

/**
 * @author Tobias Pietzsch
 * 
 */
public class NtreeImgFactory< T extends NativeType< T >> extends NativeImgFactory< T >
{
	@Override
	public NtreeImg< T, ? > create( final long[] dim, final T type )
	{
		return ( NtreeImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public NtreeImg< T, ByteNtree > createByteInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, ByteNtree >( new ByteNtree( dimensions, new long[ dimensions.length ], ( byte ) 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, CharNtree > createCharInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, CharNtree >( new CharNtree( dimensions, new long[ dimensions.length ], ( char ) 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, ShortNtree > createShortInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, ShortNtree >( new ShortNtree( dimensions, new long[ dimensions.length ], ( short ) 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, IntNtree > createIntInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, IntNtree >( new IntNtree( dimensions, new long[ dimensions.length ], 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, LongNtree > createLongInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, LongNtree >( new LongNtree( dimensions, new long[ dimensions.length ], 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, FloatNtree > createFloatInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, FloatNtree >( new FloatNtree( dimensions, new long[ dimensions.length ], 0.0f ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, DoubleNtree > createDoubleInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< T, DoubleNtree >( new DoubleNtree( dimensions, new long[ dimensions.length ], 0.0d ), dimensions, entitiesPerPixel );
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
