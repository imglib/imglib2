/**
 * Copyright (c) 2009--2011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package mpicbg.imglib.iterator;

import mpicbg.imglib.IntegerInterval;
import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.Iterator;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.location.LocalizingFlatIntegerIntervalIterator;
import mpicbg.imglib.util.Util;

/**
 * Use this class to iterate a virtual rectangular raster in flat order, that
 * is: row by row, plane by plane, cube by cube, ...  This is useful for
 * iterating an arbitrary {@link Img} in a defined order.  For that,
 * connect a {@link ZeroMinIntegerIntervalIterator} to a {@link ImgRandomAccess}.
 * 
 * <pre>
 * ...
 * FlatRasterIterator i = new FlatRasterIterator(image);
 * PositionableRasterSampler s = image.createPositionableRasterSampler();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.type().performOperation(...);
 *   ...
 * }
 * ...
 * </pre>
 * 
 * Note that {@link ZeroMinIntegerIntervalIterator} is the right choice in situations where
 * <em>not</em> for each pixel you want to localize and/or set the
 * {@link ImgRandomAccess}, that is in a sparse sampling situation.
 * For localizing at each iteration step (as in the simplified example above),
 * use {@link LocalizingFlatIntegerIntervalIterator} instead.
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class ZeroMinIntegerIntervalIterator extends IntegerIntervalIterator
{
	final static private long[] max( final long[] size )
	{
		final long[] max = new long[ size.length ];
		for ( int d = 0; d < size.length; ++d )
			max[ d ] = size[ d ] - 1;
		return max;
	}
	
	public ZeroMinIntegerIntervalIterator( final long[] dimensions )
	{
		super( new long[ dimensions.length ], max( dimensions ) );
	}

	public ZeroMinIntegerIntervalIterator( final IntegerInterval interval )
	{
		this( size( interval ) );
	}
	
	final public static void indexToPosition( final long i, final long[] steps, final int[] l )
	{
		long x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final long ld = x / steps[ d ];
			l[ d ] = ( int )ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = ( int )i;
	}

	final public static void indexToPosition( final long i, final long[] steps, final long[] l )
	{
		long x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final long ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}
	
	final public static void indexToPosition( final long i, final long[] steps, final float[] l )
	{
		long x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final long ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}
	
	final public static void indexToPosition( final long i, final long[] steps, final double[] l )
	{
		long x = i;
		for ( int d = steps.length - 1; d > 0; --d )
		{
			final long ld = x / steps[ d ];
			l[ d ] = ld;
			x -= ld * steps[ d ];
		}
		l[ 0 ] = i;
	}
	
	final public static long indexToPosition( final long i, final long[] steps, final int dim )
	{
		long x = i;
		for ( int d = steps.length - 1; d > dim; --d )
			x %= steps[ d ];

		return x / steps[ dim ];
	}
	
	/* Iterator */

	@Override
	final public void jumpFwd( final long i ) { index += i; }

	@Override
	final public void fwd() { ++index; }

	@Override
	final public void reset() { index = -1; }
	
	
	/* IntegerLocalizable */

	@Override
	final public long getLongPosition( final int dim ) { return indexToPosition( index, steps, dim ); }
	
	@Override
	final public void localize( final long[] position ) { indexToPosition( index, steps, position ); }

	@Override
	final public int getIntPosition( final int dim ) { return ( int )indexToPosition( index, steps, dim ); }

	@Override
	final public void localize( final int[] position ) { indexToPosition( index, steps, position ); }

	@Override
	final public double getDoublePosition( final int dim ) { return indexToPosition( index, steps, dim ); }
	
	
	/* RealLocalizable */

	@Override
	final public float getFloatPosition( final int dim ) { return indexToPosition( index, steps, dim ); }

	@Override
	final public void localize( final float[] position ) { indexToPosition( index, steps, position ); }

	@Override
	final public void localize( final double[] position ) { indexToPosition( index, steps, position ); }

	
	/* EuclideanSpace */
	
	@Override
	final public int numDimensions() { return n; }
	
	
	/* Object */
	
	@Override
	final public String toString()
	{
		final int[] l = new int[ size.length ];
		localize( l );
		return Util.printCoordinates( l );
	}
}
