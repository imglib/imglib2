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

import java.util.function.Supplier;

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
	public NtreeImgFactory( final T type )
	{
		super( type );
	}

	public NtreeImgFactory( final Supplier< T > supplier )
	{
		super( supplier );
	}

	@Override
	public NtreeImg< T, ? > create( final long[] dim )
	{
		return ( NtreeImg< T, ? > ) type().createSuitableNativeImg( this, dim );
	}

	@Override
	public NtreeImg< T, ByteNtree > createByteInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new ByteNtree( dimensions, new long[ dimensions.length ], ( byte ) 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, CharNtree > createCharInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new CharNtree( dimensions, new long[ dimensions.length ], ( char ) 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, ShortNtree > createShortInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new ShortNtree( dimensions, new long[ dimensions.length ], ( short ) 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, IntNtree > createIntInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new IntNtree( dimensions, new long[ dimensions.length ], 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, LongNtree > createLongInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new LongNtree( dimensions, new long[ dimensions.length ], 0 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, FloatNtree > createFloatInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new FloatNtree( dimensions, new long[ dimensions.length ], 0.0f ), dimensions, entitiesPerPixel );
	}

	@Override
	public NtreeImg< T, DoubleNtree > createDoubleInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		if ( entitiesPerPixel.getNumerator() != entitiesPerPixel.getDenominator() )
			throw new RuntimeException( "not implemented" );

		return new NtreeImg< >( new DoubleNtree( dimensions, new long[ dimensions.length ], 0.0d ), dimensions, entitiesPerPixel );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new NtreeImgFactory();
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}

	@Deprecated
	public NtreeImgFactory()
	{
		super();
	}

	@Deprecated
	@Override
	public NtreeImg< T, ? > create( final long[] dim, final T type )
	{
		return ( NtreeImg< T, ? > ) cache( type ).createSuitableNativeImg( this, dim );
	}

}
