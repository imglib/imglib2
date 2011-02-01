/**
 * Copyright (c) 2009--2010, Stephan Saalfeld
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
package mpicbg.imglib.location.transform;

import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.IntegerRandomAccess;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealRandomAccess;

/**
 * Links a {@link RealRandomAccess} with a {@link IntegerRandomAccess} by
 * transferring real coordinates to floor discrete coordinates.  For practical
 * useage, the floor operation is defined as the integer smaller than the real
 * value:
 * 
 * f = r < 0 ? (long)r - 1 : (long)r
 * 
 * The {@link IntegerRandomAccess} is not the linked {@link RealRandomAccess} of
 * this link, that is, other {@link RealRandomAccess Positionables} can be linked
 * to it in addition. 
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class PositionableFloorRasterPositionable< LocalizablePositionable extends RealLocalizable & RealRandomAccess > implements RealRandomAccess
{
	final protected LocalizablePositionable source;
	final protected IntegerRandomAccess target;
	
	final private int numDimensions;
	
	/* temporary floor position register */
	final private long[] floor;
	
	/* temporary position register */
	final private double[] position;
	
	public PositionableFloorRasterPositionable( final LocalizablePositionable source, final IntegerRandomAccess target )
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
		source.move( distance, dim );
		target.setPosition( floor( source.getDoublePosition( dim ) ), dim );
	}

	@Override
	public void move( final double distance, final int dim )
	{
		source.move( distance, dim );
		target.setPosition( floor( source.getDoublePosition( dim ) ), dim );
	}

	@Override
	public void moveTo( final RealLocalizable localizable )
	{
		localizable.localize( position );
		moveTo( position );
	}

	@Override
	public void moveTo( final float[] pos )
	{
		source.moveTo( pos );
		floor( pos, floor );
		target.moveTo( floor );
	}

	@Override
	public void moveTo( final double[] pos )
	{
		source.moveTo( pos );
		floor( pos, floor );
		target.moveTo( floor );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		localizable.localize( position );
		setPosition( position );
	}

	@Override
	public void setPosition( final float[] position )
	{
		source.setPosition( position );
		floor( position, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final double[] position )
	{
		source.setPosition( position );
		floor( position, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		source.setPosition( position, dim );
		target.setPosition( floor( position ), dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		source.setPosition( position, dim );
		target.setPosition( floor( position ), dim );
	}

	
	/* RasterPositionable */
	
	@Override
	public void bck( final int dim )
	{
		source.bck( dim );
		target.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		source.fwd( dim );
		target.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		source.move( distance, dim );
		target.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		source.move( distance, dim );
		target.move( distance, dim );
	}

	@Override
	public void moveTo( final IntegerLocalizable localizable )
	{
		source.moveTo( localizable );
		target.moveTo( localizable );
	}

	@Override
	public void moveTo( final int[] pos )
	{
		source.moveTo( pos );
		target.moveTo( pos );
	}

	@Override
	public void moveTo( final long[] pos )
	{
		source.moveTo( pos );
		target.moveTo( pos );
	}
	
	@Override
	public void setPosition( IntegerLocalizable localizable )
	{
		source.setPosition( localizable );
		target.setPosition( localizable );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		source.setPosition( position );
		target.setPosition( position );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		source.setPosition( position );
		target.setPosition( position );
	}

	@Override
	public void setPosition( int position, int dim )
	{
		source.setPosition( position, dim );
		target.setPosition( position, dim );
	}

	@Override
	public void setPosition( long position, int dim )
	{
		source.setPosition( position, dim );
		target.setPosition( position, dim );
	}
}
