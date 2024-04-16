/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.outofbounds;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.util.Util;

/**
 * Abstract implementation of shared functions for mirroring out of bounds.
 * Internally used coordinates use an interval [0<sup><em>n</em></sup>,max<sup>
 * <em>n</em></sup>-min<sup><em>n</em></sup>] and compensate for min-shift on
 * localization and positioning.
 *
 * @param <T>
 *
 * @author Stephan Saalfeld
 */
public abstract class AbstractOutOfBoundsMirror< T > implements OutOfBounds< T >
{
	final protected RandomAccess< T > outOfBoundsRandomAccess;

	final protected int n;

	/**
	 * Dimensions of the wrapped {@link RandomAccessible}.
	 */
	final protected long[] dimension;

	/**
	 * Position relative to min, for internal calculations.
	 * <em>zeroMinPos = position - min</em>.
	 */
	final protected long[] zeroMinPos;

	/**
	 * Minimum of the wrapped {@link RandomAccessible}.
	 */
	final protected long[] min;

	/**
	 * Period of the extended interval. This depends on whether boundary pixels
	 * are mirrored. See {@link OutOfBoundsMirrorDoubleBoundary}, see
	 * {@link OutOfBoundsMirrorSingleBoundary}.
	 */
	final protected long[] p;

	/* true when increasing, false when decreasing */
	final protected boolean[] inc;

	final protected boolean[] dimIsOutOfBounds;

	protected boolean isOutOfBounds = false;

	protected AbstractOutOfBoundsMirror( final AbstractOutOfBoundsMirror< T > outOfBounds )
	{
		n = outOfBounds.numDimensions();
		dimension = new long[ n ];
		min = new long[ n ];
		zeroMinPos = new long[ n ];
		p = new long[ n ];
		dimIsOutOfBounds = new boolean[ n ];
		inc = new boolean[ n ];
		for ( int d = 0; d < n; ++d )
		{
			dimension[ d ] = outOfBounds.dimension[ d ];
			min[ d ] = outOfBounds.min[ d ];
			zeroMinPos[ d ] = outOfBounds.zeroMinPos[ d ];
			p[ d ] = outOfBounds.p[ d ];
			dimIsOutOfBounds[ d ] = outOfBounds.dimIsOutOfBounds[ d ];
			inc[ d ] = outOfBounds.inc[ d ];
		}

		outOfBoundsRandomAccess = outOfBounds.outOfBoundsRandomAccess.copy();
	}

	public < F extends Interval & RandomAccessible< T > > AbstractOutOfBoundsMirror( final F f )
	{
		n = f.numDimensions();
		dimension = new long[ n ];
		f.dimensions( dimension );
		min = new long[ n ];
		f.min( min );
		zeroMinPos = new long[ n ];
		p = new long[ n ];
		dimIsOutOfBounds = new boolean[ n ];
		inc = new boolean[ n ];
		for ( int i = 0; i < dimension.length; ++i )
			inc[ i ] = true;

		outOfBoundsRandomAccess = f.randomAccess();
	}

	final protected void checkOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( dimIsOutOfBounds[ d ] )
			{
				isOutOfBounds = true;
				return;
			}
		}
		isOutOfBounds = false;
	}

	/* EuclideanSpace */

	@Override
	public int numDimensions()
	{
		return n;
	}

	/* OutOfBounds */

	@Override
	public boolean isOutOfBounds()
	{
		return isOutOfBounds;
	}

	/* Sampler */

	@Override
	public T get()
	{
		return outOfBoundsRandomAccess.get();
	}

	@Override
	abstract public AbstractOutOfBoundsMirror< T > copy();

	/* Localizable */

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = this.zeroMinPos[ d ] + min[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = this.zeroMinPos[ d ] + min[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = ( int ) ( this.zeroMinPos[ d ] + min[ d ] );
	}

	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
			pos[ d ] = this.zeroMinPos[ d ] + min[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return zeroMinPos[ d ] + min[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return zeroMinPos[ d ] + min[ d ];
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) ( zeroMinPos[ d ] + min[ d ] );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return zeroMinPos[ d ] + min[ d ];
	}

	/* Positionable */

	/**
	 * Override with a more efficient version.
	 */
	@Override
	public void move( final long distance, final int d )
	{
		setPosition( getLongPosition( d ) + distance, d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		move( ( long ) distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			move( localizable.getLongPosition( d ), d );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		setPosition( ( long ) position, d );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			setPosition( localizable.getLongPosition( d ), d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}

	/* Object */

	@Override
	public String toString()
	{
		return Util.printCoordinates( zeroMinPos ) + " = " + get();
	}
}
