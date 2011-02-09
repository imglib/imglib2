/**
 * Copyright (c) 20011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the ImgLib/Fiji project nor
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
package mpicbg.imglib.roi.rectangular;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.outofbounds.OutOfBounds;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

/**
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 * 
 */
public class SubImageOutOfBoundsRandomAccess< T > extends AbstractSubImageRandomAccess< T, OutOfBounds< T > >
{
	public SubImageOutOfBoundsRandomAccess( final SubImage<T> subImage, final OutOfBoundsFactory< T, SubImage< T > > outOfBoundsFactory )
	{
		super( subImage, outOfBoundsFactory.create( subImage ) );
	}
	
	// TODO: might need an interface
	final public boolean isOutOfBounds()
	{
		return access.isOutOfBounds();
	}	

	/* Localizable */
	/* We need to override them as the randomAccess created by the OutOfBounds does that already */
	
	@Override
	public void localize( final int[] position ) { access.localize( position ); }

	@Override
	public void localize( final long[] position ) { access.localize( position ); }

	@Override
	public int getIntPosition( final int dim ) { return access.getIntPosition( dim ); }

	@Override
	public long getLongPosition( final int dim ) { return access.getIntPosition( dim ); }

	@Override
	public void localize( final float[] position ) { access.localize( position ); }

	@Override
	public void localize( final double[] position ) { access.localize( position ); }

	@Override
	public float getFloatPosition( final int dim ) { return access.getIntPosition( dim ); }

	@Override
	public double getDoublePosition( final int dim ) { return access.getIntPosition( dim ); }

	/* Absolute Movement */

	@Override
	public void setPosition( final Localizable localizable ) { access.setPosition( localizable ); }

	@Override
	public void setPosition( final int[] position ) { access.setPosition( position ); }

	@Override
	public void setPosition( final long[] position ) { access.setPosition( position ); }

	@Override
	public void setPosition( final int position, final int dim )  { access.setPosition( position + (int)offset[ dim ], dim );	}

	@Override
	public void setPosition( final long position, final int dim ) { access.setPosition( position, dim ); }
}
