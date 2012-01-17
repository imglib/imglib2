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
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * {@link RealRandomAccess} that generates its samples from a target
 * {@link RealRandomAccessible} at coordinates transformed by a
 * {@link RealTransform}.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealTransformRealRandomAccess< T, R extends RealTransform > extends AbstractRealRandomAccess< T >
{
	final protected R transform;
	final protected RealPoint sourcePosition; 
	final protected RealRandomAccessible< T > target;
	final protected RealRandomAccess< T > targetAccess;
	
	protected RealTransformRealRandomAccess( final RealRandomAccessible< T > target, final R transform )
	{
		super( transform.numSourceDimensions() );
		sourcePosition = RealPoint.wrap( position );
		this.transform = transform;
		this.target = target;
		this.targetAccess = target.realRandomAccess();
		apply();
	}
	
	final private void apply()
	{
		transform.apply( sourcePosition, targetAccess );
	}

	@Override
	public void move( final float distance, final int d )
	{
		super.move( distance, d );
		apply();
	}

	@Override
	public void move( final double distance, final int d )
	{
		super.move( distance, d );
		apply();
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		super.move( localizable );
		apply();
	}

	@Override
	public void move( final float[] distance )
	{
		super.move( distance );
		apply();
	}

	@Override
	public void move( final double[] distance )
	{
		super.move( distance );
		apply();
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		super.setPosition( localizable );
		apply();
	}

	@Override
	public void setPosition( final float[] pos )
	{
		super.setPosition( pos );
		apply();
	}

	@Override
	public void setPosition( final double[] pos )
	{
		super.setPosition( pos );
		apply();
	}

	@Override
	public void setPosition( final float pos, final int d )
	{
		super.setPosition( pos, d );
		apply();
	}

	@Override
	public void setPosition( final double pos, final int d )
	{
		super.setPosition( pos, d );
		apply();
	}

	@Override
	public void fwd( final int d )
	{
		super.fwd( d );
		apply();
	}

	@Override
	public void bck( final int d )
	{
		super.bck( d );
		apply();
	}

	@Override
	public void move( final int distance, final int d )
	{
		super.move( distance, d );
		apply();
	}

	@Override
	public void move( final long distance, final int d )
	{
		super.move( distance, d );
		apply();
	}

	@Override
	public void move( final Localizable localizable )
	{
		super.move( localizable );
		apply();
	}

	@Override
	public void move( final int[] distance )
	{
		super.move( distance );
		apply();
	}

	@Override
	public void move( final long[] distance )
	{
		super.move( distance );
		apply();
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		super.setPosition( localizable );
		apply();
	}

	@Override
	public void setPosition( final int[] pos )
	{
		super.setPosition( pos );
		apply();
	}

	@Override
	public void setPosition( final long[] pos )
	{
		super.setPosition( pos );
		apply();
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		super.setPosition( pos, d );
		apply();
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		super.setPosition( pos, d );
		apply();
	}

	@Override
	public T get()
	{
		return targetAccess.get();
	}

	@Override
	public RealTransformRealRandomAccess< T, R > copy()
	{
		return new RealTransformRealRandomAccess< T, R >( target, transform );
	}

	@Override
	public RealRandomAccess< T > copyRealRandomAccess()
	{
		return copy();
	}

}
