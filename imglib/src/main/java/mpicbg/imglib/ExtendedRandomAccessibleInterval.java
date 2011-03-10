/**
 * Copyright (c) 2011, Stephan Saalfeld
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

package mpicbg.imglib;

import java.util.Iterator;

import mpicbg.imglib.outofbounds.AbstractOutOfBoundsRandomAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

/**
 * Implements {@link RandomAccessible} for a {@link RandomAccessibleInterval}
 * through an {@link OutOfBoundsFactory}.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class ExtendedRandomAccessibleInterval< T, F extends RandomAccessibleInterval< T > > implements RandomAccessibleInterval< T > 
{
	final protected F interval;
	final protected OutOfBoundsFactory< T, F > factory;

	public ExtendedRandomAccessibleInterval( final F interval, final OutOfBoundsFactory< T, F > factory )
	{
		this.interval = interval;
		this.factory = factory;
	}
	
	@Override
	final public long dimension( final int d )
	{
		return interval.dimension( d );
	}

	@Override
	final public void dimensions( final long[] dimensions )
	{
		interval.dimensions( dimensions );
	}

	@Override
	final public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	final public void max( final long[] max )
	{
		interval.max( max );
	}

	@Override
	final public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	final public void min( final long[] min )
	{
		interval.min( min );
	}

	@Override
	final public double realMax( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	final public void realMax( final double[] max )
	{
		realMax( max );
	}

	@Override
	final public double realMin( final int d )
	{
		return interval.realMin( d );
	}

	@Override
	final public void realMin( final double[] min )
	{
		realMin( min );
	}

	@Override
	final public int numDimensions()
	{
		return interval.numDimensions();
	}

	@Override
	final public RandomAccess< T > randomAccess()
	{
		return new AbstractOutOfBoundsRandomAccess< T >( interval.numDimensions(), factory.create( interval ) );
	}

	@Override
	public Cursor< T > cursor()
	{
		return interval.cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return interval.localizingCursor();
	}

	@Override
	public long size()
	{
		return interval.size();
	}

	@Override
	public T firstElement()
	{
		return interval.firstElement();
	}

	@Override
	public boolean equalIterationOrder( IterableRealInterval< ? > f )
	{
		return false;
	}

	@Override
	public Iterator< T > iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}	
}
