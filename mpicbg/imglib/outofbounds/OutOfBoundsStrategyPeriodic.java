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
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.type.Type;

/**
 * Coordinates out of image bounds are periodically repeated.
 * 
 * <pre>
 * Example:
 * 
 * width=4
 * 
 *                                  |<-inside->|
 * x:    -9 -8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9
 * f(x):  3  0  1  2  3  0  1  2  3  0  1  2  3  0  1  2  3  0  1
 * </pre>
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class OutOfBoundsStrategyPeriodic<T extends Type<T>> implements OutOfBoundsStrategy< T >
{
	final protected PositionableRasterSampler< T > outOfBoundsPositionable;
	
	final protected int numDimensions;
	
	final protected int[] dimension, position;
	
	final protected boolean[] dimIsOutOfBounds;
	
	protected boolean isOutOfBounds = false;
	
	public OutOfBoundsStrategyPeriodic( final PositionableRasterSampler< T > source )
	{
		this( source, source.getImage().createPositionableRasterSampler() );
	}
	
	OutOfBoundsStrategyPeriodic(
			final PositionableRasterSampler< T > source,
			final PositionableRasterSampler< T > outOfBoundsPositionable )
	{
		this.outOfBoundsPositionable = outOfBoundsPositionable;
		numDimensions = source.getImage().numDimensions();
		dimension = source.getImage().getDimensions();
		position = new int[ numDimensions ];
		
		dimIsOutOfBounds = new boolean[ numDimensions ];
	}
	
	final protected void checkOutOfBounds()
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( dimIsOutOfBounds[ d ] )
			{
				isOutOfBounds = true;
				return;
			}
		}
		isOutOfBounds = false;
	}
	
	/* Dimensionality */
	
	@Override
	public int numDimensions(){ return numDimensions; }
	
	
	/* OutOfBounds */
	
	@Override
	public boolean isOutOfBounds(){ return isOutOfBounds; }

	
	/* Sampler */
	
	@Override
	public T type(){ return outOfBoundsPositionable.type(); }
	
	@Override
	@Deprecated
	final public T getType(){ return type(); }
	
	
	/* RasterLocalizable */
	
	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public float getFloatPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public double getDoublePosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }

	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String getLocationAsString() { return MathLib.printCoordinates( position ); }
	
	@Override
	public String toString() { return getLocationAsString() + " = " + type(); }
	
	
	/* RasterPositionable */
	
	@Override
	final public void fwd( final int dim )
	{
		final int p = ++position[ dim ];
		if ( p == 0 )
		{
			dimIsOutOfBounds[ dim ] = false;
			if ( isOutOfBounds ) checkOutOfBounds();
		}
		else if ( p == dimension[ dim ] )
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		
		final int q = outOfBoundsPositionable.getIntPosition( dim ) + 1;
		if ( q == dimension[ dim ] )
			outOfBoundsPositionable.setPosition( 0, dim );
		else
			outOfBoundsPositionable.fwd( dim  );
	}
	
	@Override
	final public void bck( final int dim )
	{
		final int p = position[ dim ]--;
		if ( p == 0 )
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		else if ( p == dimension[ dim ] )
		{
			dimIsOutOfBounds[ dim ] = false;
			if ( isOutOfBounds ) checkOutOfBounds();
		}
		
		final int q = outOfBoundsPositionable.getIntPosition( dim );
		if ( q == 0 )
			outOfBoundsPositionable.setPosition( dimension[ dim ] - 1, dim );
		else
			outOfBoundsPositionable.bck( dim  );
	}
	
	@Override
	final public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;
		final int mod = dimension[ dim ];
		if ( position < 0 )
		{
			outOfBoundsPositionable.setPosition( mod - 1 + ( ( position + 1 ) % mod ), dim );
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		}
		else if ( position >= mod )
		{
			outOfBoundsPositionable.setPosition( position % mod, dim );
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		}
		else
		{
			outOfBoundsPositionable.setPosition( position, dim );
			dimIsOutOfBounds[ dim ] = false;
			if ( isOutOfBounds )
				checkOutOfBounds();
		}
	}
	
	@Override
	public void move( final int distance, final int dim )
	{
		if ( distance > 0 )
		{
			for ( int i = 0; i < distance; ++i )
				fwd( dim );
		}
		else
		{
			for ( int i = -distance; i > 0; --i )
				bck( dim );
		}
	}
	
	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );
	}
	
	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( localizable.getIntPosition( d ) - position[ d ], d );
	}
	
	@Override
	public void moveTo( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( position[ d ] - this.position[ d ], d );
	}
	
	@Override
	public void moveTo( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( position[ d ] - this.position[ d ], d );
	}
	
	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}
	
	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		for ( int d = 0; d < numDimensions; ++d )
			setPosition( localizable.getIntPosition( d ), d );
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
	
	@Override
	public void close()
	{
		outOfBoundsPositionable.close();
	}
}
