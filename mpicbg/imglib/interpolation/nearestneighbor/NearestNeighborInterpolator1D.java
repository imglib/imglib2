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
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.RasterOutOfBoundsFactory;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class NearestNeighborInterpolator1D< T extends Type< T > > extends NearestNeighborInterpolator< T >
{
	
	/* current position, required for relative movement */
	private float x;
	
	protected NearestNeighborInterpolator1D( final Image< T > image, final RasterOutOfBoundsFactory< T > outOfBoundsStrategyFactory )
	{
		super( image, outOfBoundsStrategyFactory );
		//this.target = image.createPositionableRasterSampler( outOfBoundsStrategyFactory );
		
		x = 0;
	}
	
	final static private int round( final double r )
	{
		return r < 0 ? ( int )( r - 0.5 ) : ( int )( r + 0.5 );
	}
	
	final static private int round( final float r )
	{
		return r < 0 ? ( int )( r - 0.5f ) : ( int )( r + 0.5f );
	}

	
	/* Dimensionality */
	
	@Override
	final public int numDimensions()
	{
		return 1;
	}
	
	
	/* Localizable */
	
	@Override
	public double getDoublePosition( final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		return x;
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		return x;
	}

	@Override
	public String toString()
	{
		return new StringBuffer( "(" ).append( x ).append( ") = " ).append( type() ).toString();
	}

	@Override
	public void localize( final float[] position )
	{
		position[ 0 ] = x;
	}

	@Override
	public void localize( final double[] position )
	{
		position[ 0 ] = x;
	}
	
	
	/* Positionable */

	@Override
	public void move( final double distance, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		x += distance;
		final int roundPosition = round( x );
		final int roundDistance = roundPosition - target.getIntPosition( dim );
		if ( roundDistance == 0 )
			return;
		else
			target.move( roundDistance, 0 );
	}
	
	@Override
	public void move( final float distance, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		x += distance;
		final int roundPosition = round( x );
		final int roundDistance = roundPosition - target.getIntPosition( dim );
		if ( roundDistance == 0 )
			return;
		else
			target.move( roundDistance, 0 );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		final int roundX = round( x );
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance == 0 )
			return;
		else
			target.move( roundXDistance, 0 );
	}

	@Override
	public void moveTo( final double[] position )
	{
		x = ( float )position[ 0 ];
		final int roundX = round( x );
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance == 0 )
			return;
		else
			target.move( roundXDistance, 0 );
	}

	@Override
	public void moveTo( final float[] position )
	{
		x = position[ 0 ];
		final int roundX = round( x );
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance == 0 )
			return;
		else
			target.move( roundXDistance, 0 );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		target.setPosition( round( x ), 0 );
	}

	@Override
	public void setPosition( final float[] position )
	{
		x = position[ 0 ];
		target.setPosition( round( x ), 0 );
	}

	@Override
	public void setPosition( final double[] position )
	{
		x = ( float )position[ 0 ];
		target.setPosition( round( x ), 0 );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = position;
		target.setPosition( round( position ), 0 );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = ( float )position;
		target.setPosition( round( position ), 0 );
	}
	
	
	/* RasterPositionable */

	@Override
	public void bck( final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x -= 1;
		target.bck( 0 );
	}

	@Override
	public void fwd( final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x += 1;
		target.fwd( 0 );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x += distance;
		target.move( distance, 0 );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x += distance;
		target.move( distance, 0 );
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		final int roundX = localizable.getIntPosition( 0 );
		x = roundX;
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance == 0 )
			return;
		else
			target.move( roundXDistance, 0 );
	}

	@Override
	public void moveTo( final int[] position )
	{
		final int roundX = position[ 0 ];
		x = roundX;
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance == 0 )
			return;
		else
			target.move( roundXDistance, 0 );
	}

	@Override
	public void moveTo( final long[] position )
	{
		final int roundX = ( int )position[ 0 ];
		x = roundX;
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance == 0 )
			return;
		else
			target.move( roundXDistance, 0 );
	}

	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		final int roundX = localizable.getIntPosition( 0 );
		x = roundX;
		target.setPosition( roundX, 0 );
	}

	@Override
	public void setPosition( final int[] position )
	{
		final int roundX = position[ 0 ];
		x = roundX;
		target.setPosition( roundX, 0 );
	}

	@Override
	public void setPosition( final long[] position )
	{
		final int roundX = ( int )position[ 0 ];
		x = roundX;
		target.setPosition( roundX, 0 );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = position;
		target.setPosition( position, 0 );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = position;
		target.setPosition( position, 0 );
	}
}
