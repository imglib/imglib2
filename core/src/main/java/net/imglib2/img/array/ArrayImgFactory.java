/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

/**
 * 
 * 
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ArrayImgFactory< T extends NativeType< T > > extends NativeImgFactory< T >
{
	@Override
	public ArrayImg< T, ? > create( final long[] dim, final T type )
	{
		return ( ArrayImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	public static int numEntitiesRangeCheck( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final long numEntities = entitiesPerPixel.mulCeil( AbstractImg.numElements( dimensions ) );

		if ( numEntities > Integer.MAX_VALUE )
			throw new RuntimeException( "Number of elements in Container too big, use for example CellContainer instead: " + numEntities + " > " + Integer.MAX_VALUE );

		return ( int ) numEntities;
	}

	@Override
	public ArrayImg< T, ByteArray > createByteInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, ByteArray >( new ByteArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, CharArray> createCharInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, CharArray >( new CharArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, DoubleArray > createDoubleInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, DoubleArray >( new DoubleArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, FloatArray > createFloatInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, FloatArray >( new FloatArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, IntArray > createIntInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, IntArray >( new IntArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, LongArray > createLongInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, LongArray >( new LongArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, ShortArray > createShortInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, ShortArray >( new ShortArray( numEntities ), dimensions, entitiesPerPixel );
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
