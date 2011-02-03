/**
 * Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld
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

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
public class IntegerIntervalIterator implements Iterator, IntegerLocalizable
{
	final protected long[] size;
	final protected long[] min;
	final protected long[] max;
	final protected long[] steps;
	final protected int n;
	final protected long lastIndex;
	protected long index = -1;
	
	final static protected long[] size( final IntegerInterval interval )
	{
		final long size[] = new long[ interval.numDimensions() ];
		interval.size( size );
		return size;
	}
	
	public IntegerIntervalIterator( final long[] min, final long[] max )
	{
		n = min.length;
		final int m = n - 1;
		size = new long[ n ];
		steps = new long[ n ];
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long mind = min[ d ];
			final long maxd = max[ d ];
			this.min[ d ] = mind;
			this.max[ d ] = maxd;
			final long s = maxd - mind + 1; 
			size[ d ] = s;
			k *= s;
			steps[ ++d ] = k;
		}
		final long minm = min[ m ];
		final long maxm = max[ m ];
		this.min[ m ] = minm;
		this.max[ m ] = maxm;
		final long sizem = maxm - minm + 1;
		size[ m ] = sizem;
		lastIndex = k * sizem - 1;
	}

	public IntegerIntervalIterator( final IntegerInterval interval )
	{
		n = interval.numDimensions();
		final int m = n - 1;
		size = new long[ n ];
		steps = new long[ n ];
		long k = steps[ 0 ] = 1;
		for ( int d = 0; d < m; )
		{
			final long mind = interval.min( d );
			final long maxd = interval.max( d );
			this.min[ d ] = mind;
			this.max[ d ] = maxd;
			final long s = maxd - mind + 1; 
			size[ d ] = s;
			k *= s;
			steps[ ++d ] = k;
		}
		final long minm = interval.min( m );
		final long maxm = interval.max( m );
		this.min[ m ] = minm;
		this.max[ m ] = maxm;
		final long sizem = maxm - minm + 1;
		size[ m ] = sizem;
		lastIndex = k * sizem - 1;
	}
	
	static public IntegerIntervalIterator create( final IntegerInterval interval )
	{
		final int n = interval.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( interval.min( d ) != 0 )
				return new IntegerIntervalIterator( interval );
		return new ZeroMinIntegerIntervalIterator( interval );
	}
}
