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
 *
 */
package mpicbg.imglib.location.link;

import mpicbg.imglib.location.LinkablePositionable;
import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.Positionable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.location.RasterPositionable;
import mpicbg.imglib.location.VoidPositionable;

/**
 * Links a {@link Localizable} with a {@link RasterPositionable} by
 * transferring real coordinates to floor discrete coordinates.  For practical
 * useage, the floor operation is defined as the integer smaller than the real
 * value:
 * 
 * f = r < 0 ? (long)r - 1 : (long)r
 * 
 * The {@link RasterPositionable} is not the linked {@link Positionable} of
 * this link, that is, other {@link Positionable Positionables} can be linked
 * to it in addition. 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class LocalizableFloorRasterPositionable implements LinkablePositionable
{
	final protected RasterPositionable target;
	final protected Localizable source;
	
	final private int numDimensions;
	
	final private long[] floor;
	final private double[] position;
	
	protected RasterPositionable linkedRasterPositionable = VoidPositionable.getInstance();
	protected Positionable linkedPositionable = VoidPositionable.getInstance();
	
	public LocalizableFloorRasterPositionable( final Localizable source, final RasterPositionable target )
	{
		this.source = source;
		this.target = target;
		
		numDimensions = source.numDimensions();
		
		position = new double[ numDimensions ];
		floor = new long[ numDimensions ];
	}
	
	final static private long floor( final double r )
	{
		return r < 0 ? ( long )r - 1 : ( long )r;
	}
	
	final static private long floor( final float r )
	{
		return r < 0 ? ( long )r - 1 : ( long )r;
	}
	
	final static private void floor( final double[] r, final long[] f )
	{
		for ( int i = 0; i < r.length; ++i )
			f[ i ] = floor( r[ i ] );
	}
	
	final static private void floor( final float[] r, final long[] f )
	{
		for ( int i = 0; i < r.length; ++i )
			f[ i ] = floor( r[ i ] );
	}
	
	
	/* Dimensionality */
	
	@Override
	public int numDimensions(){ return source.numDimensions(); }

	
	/* Positionable */
	
	@Override
	public void move( final float distance, final int dim )
	{
		target.setPosition( floor( source.getDoublePosition( dim ) ), dim );
		linkedPositionable.move( distance, dim );
	}

	@Override
	public void move( final double distance, final int dim )
	{
		target.setPosition( floor( source.getDoublePosition( dim ) ), dim );
		linkedPositionable.move( distance, dim );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		source.localize( position );
		moveTo( position );
	}

	@Override
	public void moveTo( final float[] position )
	{
		floor( position, floor );
		target.moveTo( floor );
		linkedPositionable.moveTo( position );
	}

	@Override
	public void moveTo( final double[] position )
	{
		floor( position, floor );
		target.moveTo( floor );
		linkedPositionable.moveTo( position );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		source.localize( position );
		setPosition( position );
	}

	@Override
	public void setPosition( final float[] position )
	{
		floor( position, floor );
		target.setPosition( floor );
		linkedPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		floor( position, floor );
		target.setPosition( floor );
		linkedPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		target.setPosition( floor( position ), dim );
		linkedPositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		target.setPosition( floor( position ), dim );
		linkedPositionable.setPosition( position, dim );
	}

	
	/* RasterPositionable */
	
	@Override
	public void bck( final int dim )
	{
		target.bck( dim );
		linkedRasterPositionable.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		target.fwd( dim );
		linkedRasterPositionable.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		target.move( distance, dim );
		linkedRasterPositionable.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		target.move( distance, dim );
		linkedRasterPositionable.move( distance, dim );
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		target.moveTo( localizable );
		linkedRasterPositionable.moveTo( localizable );
	}

	@Override
	public void moveTo( final int[] position )
	{
		target.moveTo( position );
		linkedRasterPositionable.moveTo( position );
	}

	@Override
	public void moveTo( final long[] position )
	{
		target.moveTo( position );
		linkedRasterPositionable.moveTo( position );
	}
	
	@Override
	public void setPosition( RasterLocalizable localizable )
	{
		target.setPosition( localizable );
		linkedRasterPositionable.setPosition( localizable );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		target.setPosition( position );
		linkedRasterPositionable.setPosition( position );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		target.setPosition( position );
		linkedRasterPositionable.setPosition( position );
	}

	@Override
	public void setPosition( int position, int dim )
	{
		target.setPosition( position, dim );
		linkedRasterPositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( long position, int dim )
	{
		target.setPosition( position, dim );
		linkedRasterPositionable.setPosition( position, dim );
	}
	
	
	/* LinkablePositionable */
	
	@Override
	public void linkPositionable( final Positionable positionable )
	{
		linkedRasterPositionable = positionable;
		linkedPositionable = positionable;
	}

	@Override
	public Positionable unlinkPositionable()
	{
		final Positionable positionable = linkedPositionable;
		linkedPositionable = VoidPositionable.getInstance();
		linkedRasterPositionable = VoidPositionable.getInstance();
		return positionable;
	}

	@Override
	public void linkRasterPositionable( final RasterPositionable rasterPositionable )
	{
		linkedRasterPositionable = rasterPositionable;
	}

	@Override
	public RasterPositionable unlinkRasterPositionable()
	{
		final RasterPositionable rasterPositionable = linkedRasterPositionable;
		linkedRasterPositionable = VoidPositionable.getInstance();
		return rasterPositionable;
	}
}
