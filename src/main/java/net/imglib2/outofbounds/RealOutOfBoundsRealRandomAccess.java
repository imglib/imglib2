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

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public final class RealOutOfBoundsRealRandomAccess< T > extends AbstractEuclideanSpace implements RealRandomAccess< T >, Bounded
{
	/**
	 * performs the actual moves and generates/queries a Type
	 */
	final protected RealOutOfBounds< T > outOfBounds;

	/**
	 *
	 * @param realOutOfBoundsRealRandomAccess
	 */
	public RealOutOfBoundsRealRandomAccess( final RealOutOfBoundsRealRandomAccess< T > realOutOfBoundsRealRandomAccess )
	{
		super( realOutOfBoundsRealRandomAccess.n );
		this.outOfBounds = realOutOfBoundsRealRandomAccess.outOfBounds.copy();
	}

	/**
	 * @param n
	 *            number of dimensions in the {@link RealRandomAccessible}.
	 * @param outOfBounds
	 */
	public RealOutOfBoundsRealRandomAccess( final int n, final RealOutOfBounds< T > outOfBounds )
	{
		super( n );
		this.outOfBounds = outOfBounds;
	}

	/* Bounded */

	@Override
	public boolean isOutOfBounds()
	{
		return outOfBounds.isOutOfBounds();
	}

	/* Sampler */

	@Override
	public T get()
	{
		return outOfBounds.get();
	}

	@Override
	public T getType()
	{
		return outOfBounds.getType();
	}

	@Override
	public RealOutOfBoundsRealRandomAccess< T > copy()
	{
		return new RealOutOfBoundsRealRandomAccess< T >( this );
	}

	/* RealLocalizable */

	@Override
	final public void localize( final float[] position )
	{
		outOfBounds.localize( position );
	}

	@Override
	final public void localize( final double[] position )
	{
		outOfBounds.localize( position );
	}

	@Override
	final public double getDoublePosition( final int dim )
	{
		return outOfBounds.getDoublePosition( dim );
	}

	@Override
	final public float getFloatPosition( final int dim )
	{
		return outOfBounds.getFloatPosition( dim );
	}

	/* RealPositionable */

	@Override
	public void move( final float distance, final int d )
	{
		outOfBounds.move( distance, d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		outOfBounds.move( distance, d );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		outOfBounds.move( localizable );
	}

	@Override
	public void move( final float[] distance )
	{
		outOfBounds.move( distance );
	}

	@Override
	public void move( final double[] distance )
	{
		outOfBounds.move( distance );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		outOfBounds.setPosition( localizable );
	}

	@Override
	public void setPosition( final float[] position )
	{
		outOfBounds.setPosition( position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		outOfBounds.setPosition( position );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		outOfBounds.setPosition( position, d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		outOfBounds.setPosition( position, d );
	}

	/* Positionable */

	@Override
	public void fwd( final int d )
	{
		outOfBounds.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		outOfBounds.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		outOfBounds.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		outOfBounds.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		outOfBounds.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		outOfBounds.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		outOfBounds.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		outOfBounds.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		outOfBounds.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		outOfBounds.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		outOfBounds.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		outOfBounds.setPosition( position, d );
	}
}
