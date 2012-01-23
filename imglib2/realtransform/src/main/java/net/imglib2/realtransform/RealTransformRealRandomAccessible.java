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

import net.imglib2.AbstractRealRandomAccess;
import net.imglib2.RealInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealTransformRealRandomAccessible< T, R extends RealTransform > implements RealRandomAccessible< T >
{
	final protected RealRandomAccessible< T > target;
	final protected R transform;
	
	/**
	 * {@link RealRandomAccess} that generates its samples from a target
	 * {@link RealRandomAccessible} at coordinates transformed by a
	 * {@link RealTransform}.
	 *
	 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
	 */
	public class RealTransformRealRandomAccess extends AbstractRealRandomAccess< T >
	{
		final protected RealPoint sourcePosition; 
		final protected RealRandomAccess< T > targetAccess;
		
		protected RealTransformRealRandomAccess()
		{
			super( transform.numSourceDimensions() );
			sourcePosition = RealPoint.wrap( position );
			this.targetAccess = target.realRandomAccess();
		}
		
		final protected void apply()
		{
			transform.apply( sourcePosition, targetAccess );
		}

		@Override
		public T get()
		{
			apply();
			return targetAccess.get();
		}

		@Override
		public RealTransformRealRandomAccess copy()
		{
			return new RealTransformRealRandomAccess();
		}

		@Override
		public RealRandomAccess< T > copyRealRandomAccess()
		{
			return copy();
		}

	}
	
	public RealTransformRealRandomAccessible( final RealRandomAccessible< T > target, final R transform )
	{
		assert target.numDimensions() == transform.numTargetDimensions();
		
		this.target = target;
		this.transform = transform;
	}
	
	@Override
	public int numDimensions()
	{
		return transform.numSourceDimensions();
	}

	@Override
	public RealTransformRealRandomAccess realRandomAccess()
	{
		return new RealTransformRealRandomAccess();
	}

	/**
	 * To be overridden for {@link RealTransform} that can estimate the
	 * boundaries of a transferred {@link RealInterval}.
	 */
	@Override
	public RealTransformRealRandomAccess realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}

}
