/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2.img.array;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;

/**
 * 
 * 
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ArrayImgFactory< T extends NativeType<T> > extends NativeImgFactory< T >
{
	@Override
	public ArrayImg< T, ? > create( final long[] dim, final T type )
	{
		return ( ArrayImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}

	public static int numEntitiesRangeCheck( final long[] dimensions, final int entitiesPerPixel )
	{
		final long numEntities = AbstractImg.numElements( dimensions ) * entitiesPerPixel;

		if ( numEntities > ( long ) Integer.MAX_VALUE )
			throw new RuntimeException( "Number of elements in Container too big, use for example CellContainer instead: " + numEntities + " > " + Integer.MAX_VALUE );

		return ( int ) numEntities;
	}

	@Override
	public ArrayImg< T, BitAccess > createBitInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, BitAccess >( new BitArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, ByteAccess > createByteInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, ByteAccess >( new ByteArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, CharAccess > createCharInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, CharAccess >( new CharArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, DoubleAccess > createDoubleInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, DoubleAccess >( new DoubleArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, FloatAccess > createFloatInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, FloatAccess >( new FloatArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, IntAccess > createIntInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, IntAccess >( new IntArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, LongAccess > createLongInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, LongAccess >( new LongArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public ArrayImg< T, ShortAccess > createShortInstance( long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new ArrayImg< T, ShortAccess >( new ShortArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public <S> ImgFactory<S> imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new ArrayImgFactory();
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
