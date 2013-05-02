/**
 * Copyright (c) 2009--2012, ImgLib2 developers
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
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
package net.imglib2.tutorial.t02;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * This benchmarks two generic versions of Find-Maximum-Value-And-Location.
 * The first one is findmax() from FindMaximumValueAndLocationGeneric which copies the cursor everytime a better max is found.
 * The second one findmax2() localizes and copies the value everytime a better max is found.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FindMaximumValueAndLocationGenericBenchmark
{
	static class MaxAndPos<T>
	{
		final T max;

		final long[] pos;

		MaxAndPos( final T max, final long[] pos )
		{
			this.max = max;
			this.pos = pos;
		}
	}

	public static < T extends Type<T> & Comparable< T > > MaxAndPos< T > findmax2( final IterableInterval< T > iterable )
	{
		final Cursor< T > cursor = iterable.cursor();
		final T max = iterable.firstElement().copy();
		final long[] pos = new long[ iterable.numDimensions() ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final T t = cursor.get();
			if ( t.compareTo( max ) > 0 )
			{
				max.set( t );
				cursor.localize( pos );
			}
		}
		return new MaxAndPos<T>( max, pos );
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< UnsignedByteType > img = new ImgOpener().openImg( "graffiti.tif", new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		for ( int j = 0; j < 20; ++j )
		{
			{
				final long t0 = System.currentTimeMillis();
				Cursor< UnsignedByteType > max = null;
				for ( int i = 0; i < 10000; ++i )
					max = FindMaximumValueAndLocationGeneric.findmax( img );
				final long t1 = System.currentTimeMillis();
				System.out.print( ( t1 - t0 ) + " ms - findmax()" );
				System.out.print( "\t\tmax = " + max.get().get() );
				System.out.println( " found at ( " + max.getLongPosition( 0 ) + ", " + max.getLongPosition( 1 ) + ")" );
			}
			{
				final long t0 = System.currentTimeMillis();
				MaxAndPos< UnsignedByteType > max = null;
				for ( int i = 0; i < 10000; ++i )
					max = findmax2( img );
				final long t1 = System.currentTimeMillis();
				System.out.print( ( t1 - t0 ) + " ms - findmax2()" );
				System.out.print( "\t\tmax = " + max.max.get() );
				System.out.println( " found at ( " + max.pos[ 0 ] + ", " + max.pos[ 1 ] + ")" );
			}
		}
	}
}
