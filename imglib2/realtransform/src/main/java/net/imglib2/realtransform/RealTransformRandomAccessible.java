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
package net.imglib2.realtransform;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealTransformRandomAccessible< T, R extends RealTransform > extends RealTransformRealRandomAccessible< T, R > implements RandomAccessible< T >
{
	/**
	 * {@link RealRandomAccess} that generates its samples from a target
	 * {@link RealRandomAccessible} at coordinates transformed by a
	 * {@link RealTransform}.
	 *
	 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
	 */
	public class RealTransformRandomAccess extends AbstractRandomAccess< T >
	{
		final protected Point sourcePosition; 
		final protected RealRandomAccess< T > targetAccess;
		
		protected RealTransformRandomAccess()
		{
			super( transform.numSourceDimensions() );
			sourcePosition = Point.wrap( position );
			this.targetAccess = target.realRandomAccess();
		}
		
		final protected void apply()
		{
			transform.apply( sourcePosition, targetAccess );
		}

		@Override
		public void fwd( final int d )
		{
			++position[ d ];
		}

		@Override
		public void bck( final int d )
		{
			--position[ d ];
		}

		@Override
		public void move( final long distance, final int d )
		{
			position[ d ] += distance;
		}

		@Override
		public void setPosition( final int[] pos )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = pos[ d ];
		}

		@Override
		public void setPosition( final long[] pos )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = pos[ d ];
		}

		@Override
		public void setPosition( final long pos, final int d )
		{
			position[ d ] = pos;
		}

		@Override
		public T get()
		{
			apply();
			return targetAccess.get();
		}

		@Override
		public RealTransformRandomAccess copy()
		{
			return new RealTransformRandomAccess();
		}

		@Override
		public AbstractRandomAccess< T > copyRandomAccess()
		{
			return copy();
		}
	}
	
	public RealTransformRandomAccessible( final RealRandomAccessible< T > target, final R transform )
	{
		super( target, transform );
	}

	@Override
	public RealTransformRandomAccess randomAccess()
	{
		return new RealTransformRandomAccess();
	}

	/**
	 * To be overridden for {@link RealTransform} that can estimate the
	 * boundaries of a transferred {@link RealInterval}.
	 */
	@Override
	public RealTransformRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}	
}
