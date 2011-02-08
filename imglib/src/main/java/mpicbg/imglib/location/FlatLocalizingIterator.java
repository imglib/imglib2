/**
 * Copyright (c) 2009--2010, Stephan Saalfeld
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
package mpicbg.imglib.location;

import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.Iterator;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.iterator.ZeroMinIntervalIterator;
import mpicbg.imglib.util.IntervalIndexer;
import mpicbg.imglib.util.Util;

/**
 * Use this class to iterate a virtual rectangular raster in flat order, that
 * is: row by row, plane by plane, cube by cube, ...  This is useful for
 * iterating an arbitrary {@link Img} in a defined order.  For that,
 * connect a {@link FlatLocalizingIterator} to a
 * {@link ImgRandomAccess}.
 * 
 * <pre>
 * ...
 * LocalizingFlatRasterIterator i = new LocalizingFlatRasterIterator(image);
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
 * Note that {@link FlatLocalizingIterator} is the right choice in
 * situations where, for <em>each</em> pixel, you want to localize and/or set
 * the {@link ImgRandomAccess}, that is in a dense sampling
 * situation.  For localizing sparsely (e.g. under an external condition),
 * use {@link ZeroMinIntervalIterator} instead.
 * 
 * TODO implement it, it's still the basic FlatRasterIterator!!!!!!
 *  
 * @author Stephan Preibisch, Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class FlatLocalizingIterator implements Iterator, Localizable
{
	final protected long[] size, steps, position;
	final protected int n;
	final protected long lastIndex;
	protected long index = -1;
	
	public FlatLocalizingIterator( final long[] dimensions )
	{
		n = dimensions.length;
		final int m = n - 1;
		this.size = dimensions.clone();
		this.position = new long[ n ];
		steps = new long[ n ];
		long k = steps[ 0 ] = 1;
		for ( int i = 0; i < m; )
		{
			k *= dimensions[ i ];
			steps[ ++i ] = k;
		}
		lastIndex = k * dimensions[ m ] - 1;
		
		reset();
	}

	public FlatLocalizingIterator( final Interval interval ) 
	{ 
		this( Util.intervalDimensions( interval) ); 
	}
		
	/* Iterator */

	@Override
	final public void jumpFwd( final long i ) 
	{ 
		index += i;
		IntervalIndexer.indexToPosition( index, size, position );
	}

	@Override
	final public void fwd() 
	{ 
		++index; 

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] >= size[ d ] ) position[ d ] = 0;
			else break;
		}
	}

	@Override
	final public void reset() 
	{ 
		index = -1;
		
		for ( int d = 1; d < n; ++d )
			position[ d ] = 0;
		
		position[ 0 ] = -1;
	}
	
	
	/* Localizable */

	@Override
	final public long getLongPosition( final int dim ) { return position[ dim ]; }
	
	@Override
	final public void localize( final long[] position ) 
	{
		for ( int d = 0; d < n; ++d )
			position[ d  ] = this.position[ d ];
	}

	@Override
	final public int getIntPosition( final int dim ) { return (int)position[ dim ]; }

	@Override
	final public void localize( final int[] position ) 
	{ 
		for ( int d = 0; d < n; ++d )
			position[ d  ] = (int)this.position[ d ];
	}

	@Override
	final public double getDoublePosition( final int dim ) { return position[ dim ]; }
	
	@Override
	final public float getFloatPosition( final int dim ) { return position[ dim ]; }
	
	/* RealLocalizable */

	@Override
	final public void localize( final float[] position ) 
	{
		for ( int d = 0; d < n; ++d )
			position[ d  ] = this.position[ d ];
	}

	@Override
	final public void localize( final double[] position ) 
	{
		for ( int d = 0; d < n; ++d )
			position[ d  ] = this.position[ d ];
	}

	
	/* Dimensionality */
	
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
