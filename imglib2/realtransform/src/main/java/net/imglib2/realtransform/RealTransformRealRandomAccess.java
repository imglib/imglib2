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
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealTransformRealRandomAccess< T, R extends RealTransform > extends AbstractRealRandomAccess< T >
{
	final protected R transform;
	final double[] targetPosition;
	
	protected RealTransformRealRandomAccess( final R transform )
	{
		super( transform.numSourceDimensions() );
		this.transform = transform;
		targetPosition = new double[ transform.numTargetDimensions() ];
		transform.apply( position, targetPosition );
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealLocalizable#localize(float[])
	 */
	@Override
	public void localize( final float[] position )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealLocalizable#localize(double[])
	 */
	@Override
	public void localize( final double[] position )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealLocalizable#getFloatPosition(int)
	 */
	@Override
	public float getFloatPosition( final int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealLocalizable#getDoublePosition(int)
	 */
	@Override
	public double getDoublePosition( final int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.EuclideanSpace#numDimensions()
	 */
	@Override
	public int numDimensions()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(float, int)
	 */
	@Override
	public void move( final float distance, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(double, int)
	 */
	@Override
	public void move( final double distance, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(net.imglib2.RealLocalizable)
	 */
	@Override
	public void move( final RealLocalizable localizable )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(float[])
	 */
	@Override
	public void move( final float[] distance )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#move(double[])
	 */
	@Override
	public void move( final double[] distance )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(net.imglib2.RealLocalizable)
	 */
	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(float[])
	 */
	@Override
	public void setPosition( final float[] position )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(double[])
	 */
	@Override
	public void setPosition( final double[] position )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(float, int)
	 */
	@Override
	public void setPosition( final float position, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealPositionable#setPosition(double, int)
	 */
	@Override
	public void setPosition( final double position, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#fwd(int)
	 */
	@Override
	public void fwd( final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#bck(int)
	 */
	@Override
	public void bck( final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(int, int)
	 */
	@Override
	public void move( final int distance, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(long, int)
	 */
	@Override
	public void move( final long distance, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(net.imglib2.Localizable)
	 */
	@Override
	public void move( final Localizable localizable )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(int[])
	 */
	@Override
	public void move( final int[] distance )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#move(long[])
	 */
	@Override
	public void move( final long[] distance )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(net.imglib2.Localizable)
	 */
	@Override
	public void setPosition( final Localizable localizable )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(int[])
	 */
	@Override
	public void setPosition( final int[] position )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(long[])
	 */
	@Override
	public void setPosition( final long[] position )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(int, int)
	 */
	@Override
	public void setPosition( final int position, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(long, int)
	 */
	@Override
	public void setPosition( final long position, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Sampler#get()
	 */
	@Override
	public T get()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Sampler#copy()
	 */
	@Override
	public Sampler< T > copy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RealRandomAccess#copyRealRandomAccess()
	 */
	@Override
	public RealRandomAccess< T > copyRealRandomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
