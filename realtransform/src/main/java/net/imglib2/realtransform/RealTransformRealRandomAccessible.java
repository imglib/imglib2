/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.realtransform;

import net.imglib2.RandomAccess;
import net.imglib2.RealInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * A {@link RealRandomAccessible} whose samples are generated from a
 * {@link RealRandomAccessible} transformed by an {@link RealTransform}.
 * Changing the {@link RealTransform} will affect the
 * {@link RealTransformRealRandomAccessible} but not any existing
 * {@link RealRandomAccess} on it because each {@link RealRandomAccess}
 * internally works with a copy of the transform.  Make sure that you request
 * a new {@link RandomAccess} after modifying the transformation.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
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
	 */
	public class RealTransformRealRandomAccess extends RealPoint implements RealRandomAccess< T >
	{
		final protected RealRandomAccess< T > targetAccess;

		final protected R transformCopy;

		@SuppressWarnings( "unchecked" )
		protected RealTransformRealRandomAccess()
		{
			super( transform.numSourceDimensions() );
			targetAccess = target.realRandomAccess();
			transformCopy = ( R )transform.copy();
		}

		@SuppressWarnings( "unchecked" )
		private RealTransformRealRandomAccess( final RealTransformRealRandomAccess a )
		{
			super( a );
			this.targetAccess = a.targetAccess.copyRealRandomAccess();
			transformCopy = ( R )a.transformCopy.copy();
		}

		final protected void apply()
		{
			transformCopy.apply( this, targetAccess );
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
			return new RealTransformRealRandomAccess( this );
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
