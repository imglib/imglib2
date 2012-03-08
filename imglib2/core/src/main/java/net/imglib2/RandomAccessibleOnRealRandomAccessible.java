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
package net.imglib2;

/**
 * {@link RandomAccessible} on a {@link RealRandomAccessible}.  For optimal
 * performance, no integer coordinates are stored in the {@link RandomAccess}
 * but only method calls passed through to an actual {@link RealRandomAccess}.
 * Therefore, localization into integer fields performs a Math.round operation
 * per field and is thus not very efficient.  Localization into real fields,
 * however, is passed through and thus performans optimally. 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RandomAccessibleOnRealRandomAccessible< T > implements RandomAccessible< T >
{
	final protected int n;
	final protected RealRandomAccessible< T > target;
	
	final protected class RandomAccessOnRealRandomAccessible implements RandomAccess< T >
	{
		final protected RealRandomAccess< T > targetAccess;
		
		public RandomAccessOnRealRandomAccessible()
		{
			targetAccess = target.realRandomAccess();
		}
		
		public RandomAccessOnRealRandomAccessible( final Interval interval )
		{
			targetAccess = target.realRandomAccess( interval );
		}
		
		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = ( int )Math.round( targetAccess.getDoublePosition( d ) );
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = Math.round( targetAccess.getDoublePosition( d ) );			
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int )Math.round( targetAccess.getDoublePosition( d ) );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return Math.round( targetAccess.getDoublePosition( d ) );
		}

		@Override
		public void localize( final float[] position )
		{
			targetAccess.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			targetAccess.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return targetAccess.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return targetAccess.getDoublePosition( d );
		}
		
		@Override
		public void fwd( final int d )
		{
			targetAccess.fwd( d );
		}

		@Override
		public void bck( final int d )
		{
			targetAccess.fwd( d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			targetAccess.move( distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			targetAccess.move( distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			targetAccess.move( localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			targetAccess.move( distance );
		}

		@Override
		public void move( final long[] distance )
		{
			targetAccess.move( distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			targetAccess.setPosition( localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			targetAccess.setPosition( position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			targetAccess.setPosition( position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			targetAccess.setPosition( position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			targetAccess.setPosition( position, d );
		}

		@Override
		public T get()
		{
			return targetAccess.get();
		}

		@Override
		public RandomAccessOnRealRandomAccessible copy()
		{
			return new RandomAccessOnRealRandomAccessible();
		}

		@Override
		public RandomAccess< T > copyRandomAccess()
		{
			return copy();
		}

		@Override
		public int numDimensions()
		{
			return n;
		}
	}
	
	public RandomAccessibleOnRealRandomAccessible( final RealRandomAccessible< T > target )
	{
		this.target = target;
		n = target.numDimensions();
	}
	
	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new RandomAccessOnRealRandomAccessible();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return new RandomAccessOnRealRandomAccessible( interval );
	}
	
}
