/**
 * Copyright (c) 2009--2011, Stephan Preibisch & Stephan Saalfeld
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
package mpicbg.imglib.container;

import mpicbg.imglib.IntegerInterval;
import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.outofbounds.RealOutOfBounds;
import mpicbg.imglib.outofbounds.RealOutOfBounds;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public abstract class AbstractOutOfBoundsRandomAccessContainerSampler< T extends Type< T > > extends AbstractContainerSampler< T > implements RandomAccessContainerSampler< T >
{
	/* performs the actual moves and generates/queries a Type */
	final protected RealOutOfBounds< T > outOfBounds;
	
	public AbstractOutOfBoundsRandomAccessContainerSampler( final IntegerInterval f, final RealOutOfBounds< T > outOfBounds )
	{
		super( f.numDimensions() );
		
		this.outOfBounds = outOfBounds;
	}
	
	public AbstractOutOfBoundsRandomAccessContainerSampler( final IntegerInterval f, final RasterOutOfBoundsFactory< T, ? > outOfBoundsFactory )
	{
		super( f.numDimensions() );
		
		this.outOfBounds = outOfBoundsFactory.create( this );
	}
	
	final public boolean isOutOfBounds()
	{
		return outOfBounds.isOutOfBounds();
	}
	
	
	/* Sampler */
	
	@Override
	final public T get(){ return outOfBounds.get(); }
	
	
	/* RasterLocalizable */
	
	@Override
	final public void localize( final int[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public void localize( final long[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public int getIntPosition( final int dim ){ return outOfBounds.getIntPosition( dim ); }
	
	@Override
	final public long getLongPosition( final int dim ){ return outOfBounds.getLongPosition( dim ); }
	
	
	/* Localizable */
	
	@Override
	final public void localize( final float[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public void localize( final double[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public double getDoublePosition( final int dim ){ return outOfBounds.getDoublePosition( dim ); }
	
	@Override
	final public float  getFloatPosition( final int dim ){ return outOfBounds.getFloatPosition( dim ); }
	
	@Override
	public String toString() { return outOfBounds.toString() + " = " + get(); }
	
	
	/* RasterPositionable */
	
	@Override
	final public void fwd( final int dim )
	{
		outOfBounds.fwd( dim );
	}
	
	@Override
	final public void bck( final int dim )
	{
		outOfBounds.bck( dim );
	}
	
	@Override
	final public void move( final int distance, final int dim )
	{
		outOfBounds.move( distance, dim );
	}
	
	@Override
	final public void move( final long distance, final int dim )
	{
		outOfBounds.move( distance, dim );
	}
	
	@Override
	final public void move( final IntegerLocalizable localizable )
	{
		outOfBounds.move( localizable );
	}
	
	@Override
	final public void move( final int[] position )
	{
		outOfBounds.move( position );
	}
	
	@Override
	final public void move( final long[] position )
	{
		outOfBounds.move( position );
	}
	
	@Override
	final public void setPosition( final int distance, final int dim )
	{
		outOfBounds.setPosition( distance, dim );
	}
	
	@Override
	final public void setPosition( final long distance, final int dim )
	{
		outOfBounds.setPosition( distance, dim );
	}
	
	@Override
	final public void setPosition( final IntegerLocalizable localizable )
	{
		outOfBounds.setPosition( localizable );
	}
	
	@Override
	final public void setPosition( final int[] position )
	{
		outOfBounds.setPosition( position );
	}
	
	@Override
	final public void setPosition( final long[] position )
	{
		outOfBounds.setPosition( position );
	}
}
