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
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.AbstractContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.CharAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.LongAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.CharArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.basictypecontainer.array.LongArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.type.Type;

/**
 * 
 * 
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ArrayContainerFactory extends DirectAccessContainerFactory
{
	public static int numEntitiesRangeCheck( final long[] dimensions, final int entitiesPerPixel )
	{
		final long numEntities = AbstractContainer.numElements( dimensions ) * entitiesPerPixel;

		if ( numEntities > ( long ) Integer.MAX_VALUE )
			throw new RuntimeException( "Number of elements in Container too big, use for example CellContainer instead: " + numEntities + " > " + Integer.MAX_VALUE );

		return ( int ) numEntities;
	}

	@Override
	public < T extends Type< T > > Array< T, BitAccess > createBitInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, BitAccess >( type, new BitArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, ByteAccess > createByteInstance( final T type, final long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, ByteAccess >( type, new ByteArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, CharAccess > createCharInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, CharAccess >( type, new CharArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, DoubleAccess > createDoubleInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, DoubleAccess >( type, new DoubleArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, FloatAccess > createFloatInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, FloatAccess >( type, new FloatArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, IntAccess > createIntInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, IntAccess >( type, new IntArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, LongAccess > createLongInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, LongAccess >( type, new LongArray( numEntities ), dimensions, entitiesPerPixel );
	}

	@Override
	public < T extends Type< T > > Array< T, ShortAccess > createShortInstance( final T type, long[] dimensions, final int entitiesPerPixel )
	{
		final int numEntities = numEntitiesRangeCheck( dimensions, entitiesPerPixel );

		return new Array< T, ShortAccess >( type, new ShortArray( numEntities ), dimensions, entitiesPerPixel );
	}
}
