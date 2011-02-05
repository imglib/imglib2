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
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
abstract public class AbstractInterpolator< T, F extends EuclideanSpace, LocalizablePositionable extends RealLocalizable & RealPositionable > implements Interpolator< T, F >
{
	final protected OutOfBoundsFactory< T, F > outOfBoundsStrategyFactory;
	final protected LocalizablePositionable localizablePositionable;

	final protected F img;

	/**
	 * the number of dimensions
	 */
	final protected int numDimensions;

	protected AbstractInterpolator(
			final F img,
			final OutOfBoundsFactory< T, F > outOfBoundsStrategyFactory,
			final LocalizablePositionable localizablePositionable )
	{
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.img = img;
		this.localizablePositionable = localizablePositionable;

		numDimensions = img.numDimensions();
	}

	@Override
	final public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	@Deprecated
	final public T getType()
	{
		return get();
	}

	/**
	 * Returns the {@link RasterOutOfBoundsFactory} used for interpolation
	 * 
	 * @return - the {@link RasterOutOfBoundsFactory}
	 */
	@Override
	public OutOfBoundsFactory< T, F > getOutOfBoundsStrategyFactory()
	{
		return outOfBoundsStrategyFactory;
	}

	/* Localizable */

	@Override
	public double getDoublePosition( final int dim )
	{
		return localizablePositionable.getDoublePosition( dim );
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		return localizablePositionable.getFloatPosition( dim );
	}

	@Override
	public String toString()
	{
		return localizablePositionable.toString();
	}

	@Override
	public void localize( final float[] position )
	{
		localizablePositionable.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		localizablePositionable.localize( position );
	}
	

	/* Positionable */

	@Override
	public void move( final double distance, final int dim )
	{
		localizablePositionable.move( distance, dim );
	}

	@Override
	public void move( final float distance, final int dim )
	{
		localizablePositionable.move( distance, dim );
	}

	@Override
	public void move( final double[] position )
	{
		localizablePositionable.move( position );
	}

	@Override
	public void move( final float[] position )
	{
		localizablePositionable.move( position );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		localizablePositionable.move( localizable );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		localizablePositionable.setPosition( localizable );
	}

	@Override
	public void setPosition( final float[] position )
	{
		localizablePositionable.setPosition( position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		localizablePositionable.setPosition( position );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		localizablePositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		localizablePositionable.setPosition( position, dim );
	}
	

	/* RasterPositionable */

	@Override
	public void bck( final int dim )
	{
		localizablePositionable.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		localizablePositionable.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		localizablePositionable.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		localizablePositionable.move( distance, dim );
	}

	@Override
	public void move( final Localizable localizable )
	{
		localizablePositionable.move( localizable );
	}

	@Override
	public void move( final int[] position )
	{
		localizablePositionable.move( position );
	}

	@Override
	public void move( final long[] position )
	{
		localizablePositionable.move( position );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizablePositionable.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		localizablePositionable.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		localizablePositionable.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		localizablePositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		localizablePositionable.setPosition( position, dim );
	}
}
