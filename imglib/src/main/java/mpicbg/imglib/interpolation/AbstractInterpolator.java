/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
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
package mpicbg.imglib.interpolation;

import mpicbg.imglib.EuclideanSpace;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

TODO: // TODO: Do we need this class at all?

/**
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
abstract public class AbstractInterpolator< T, F extends EuclideanSpace, RealLocalizableRealPositionable extends RealLocalizable & RealPositionable > implements Interpolator< T, F >
{
	final protected RealLocalizableRealPositionable realLocalizableRealPositionable;

	final protected F img;

	/**
	 * the number of dimensions
	 */
	final protected int n;

	protected AbstractInterpolator(
			final F img,
			final RealLocalizableRealPositionable realLocalizableRealPositionable )
	{
		this.img = img;
		this.realLocalizableRealPositionable = realLocalizableRealPositionable;

		n = img.numDimensions();
	}

	@Override
	final public int numDimensions()
	{
		return n;
	}

	@Override
	@Deprecated
	final public T getType()
	{
		return get();
	}

	/* RealLocalizable */

	@Override
	public double getDoublePosition( final int dim )
	{
		return realLocalizableRealPositionable.getDoublePosition( dim );
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		return realLocalizableRealPositionable.getFloatPosition( dim );
	}

	@Override
	public String toString()
	{
		return realLocalizableRealPositionable.toString();
	}

	@Override
	public void localize( final float[] position )
	{
		realLocalizableRealPositionable.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		realLocalizableRealPositionable.localize( position );
	}
	

	/* RealPositionable */

	@Override
	public void move( final double distance, final int dim )
	{
		realLocalizableRealPositionable.move( distance, dim );
	}

	@Override
	public void move( final float distance, final int dim )
	{
		realLocalizableRealPositionable.move( distance, dim );
	}

	@Override
	public void move( final double[] position )
	{
		realLocalizableRealPositionable.move( position );
	}

	@Override
	public void move( final float[] position )
	{
		realLocalizableRealPositionable.move( position );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		realLocalizableRealPositionable.move( localizable );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		realLocalizableRealPositionable.setPosition( localizable );
	}

	@Override
	public void setPosition( final float[] position )
	{
		realLocalizableRealPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		realLocalizableRealPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		realLocalizableRealPositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		realLocalizableRealPositionable.setPosition( position, dim );
	}
	

	/* Positionable */

	@Override
	public void bck( final int dim )
	{
		realLocalizableRealPositionable.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		realLocalizableRealPositionable.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		realLocalizableRealPositionable.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		realLocalizableRealPositionable.move( distance, dim );
	}

	@Override
	public void move( final Localizable localizable )
	{
		realLocalizableRealPositionable.move( localizable );
	}

	@Override
	public void move( final int[] position )
	{
		realLocalizableRealPositionable.move( position );
	}

	@Override
	public void move( final long[] position )
	{
		realLocalizableRealPositionable.move( position );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		realLocalizableRealPositionable.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		realLocalizableRealPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		realLocalizableRealPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		realLocalizableRealPositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		realLocalizableRealPositionable.setPosition( position, dim );
	}
}
