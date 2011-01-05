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
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class NearestNeighborInterpolator3D< T extends Type< T > > extends NearestNeighborInterpolator< T >
{
	/* current position, required for relative movement */
	private float x, y, z;
	
	protected NearestNeighborInterpolator3D( final Image< T > image, final OutOfBoundsStrategyFactory< T > outOfBoundsStrategyFactory )
	{
		super( image, outOfBoundsStrategyFactory );
		
		x = 0;
		y = 0;
		z = 0;		
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
		return 3;
	}
	
	
	/* Localizable */
	
	@Override
	public double getDoublePosition( final int dim )
	{
		switch ( dim )
		{
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			return 0;
		}
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		switch ( dim )
		{
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			return 0;
		}
	}

	@Override
	public String getLocationAsString()
	{
		return new StringBuffer( "(" ).append( x ).append( ", " ).append( y ).append( ", " ).append( z ).append( ")" ).toString();
	}

	@Override
	public void localize( final float[] position )
	{
		position[ 0 ] = x;
		position[ 1 ] = y;
		position[ 2 ] = z;
	}

	@Override
	public void localize( final double[] position )
	{
		position[ 0 ] = x;
		position[ 1 ] = y;
		position[ 2 ] = z;
	}
	
	
	/* Positionable */

	@Override
	public void move( final double distance, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		final int roundPosition;
		switch ( dim )
		{
		case 0:
			x += distance;
			roundPosition = round( x );
			break;
		case 1:
			y += distance;
			roundPosition = round( y );
			break;
		case 2:
			z += distance;
			roundPosition = round( z );
			break;
		default:
			roundPosition = 0;
		}
		final int roundDistance = roundPosition - target.getIntPosition( dim );
		if ( roundDistance == 0 )
			return;
		else
			target.move( roundDistance, dim );
	}
	
	@Override
	public void move( final float distance, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		final int roundPosition;
		switch ( dim )
		{
		case 0:
			x += distance;
			roundPosition = round( x );
			break;
		case 1:
			y += distance;
			roundPosition = round( y );
			break;
		case 2:
			z += distance;
			roundPosition = round( z );
			break;
		default:
			roundPosition = 0;
		}
		final int roundDistance = roundPosition - target.getIntPosition( dim );
		if ( roundDistance == 0 )
			return;
		else
			target.move( roundDistance, dim );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		assert localizable.numDimensions() == 3 : getClass().getCanonicalName() + " cannot process other than 3 dimensions.";

		x = localizable.getFloatPosition( 0 );
		final int roundX = round( x );
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance != 0 )
			target.move( roundXDistance, 0 );
		
		y = localizable.getFloatPosition( 1 );
		final int roundY = round( y );
		final int roundYDistance = roundY - target.getIntPosition( 1 );
		if ( roundYDistance != 0 )
			target.move( roundYDistance, 1 );
		
		z = localizable.getFloatPosition( 2 );
		final int roundZ = round( z );
		final int roundZDistance = roundZ - target.getIntPosition( 2 );
		if ( roundZDistance != 0 )
			target.move( roundZDistance, 2 );
	}

	@Override
	public void moveTo( final double[] position )
	{
		assert position.length == 3 : getClass().getCanonicalName() + " cannot process other than 3 dimensions.";

		x = ( float )position[ 0 ];
		final int roundX = round( x );
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance != 0 )
			target.move( roundXDistance, 0 );
		
		y = ( float )position[ 1 ];
		final int roundY = round( y );
		final int roundYDistance = roundY - target.getIntPosition( 1 );
		if ( roundYDistance != 0 )
			target.move( roundYDistance, 1 );
		
		z = ( float )position[ 2 ];
		final int roundZ = round( z );
		final int roundZDistance = roundZ - target.getIntPosition( 2 );
		if ( roundZDistance != 0 )
			target.move( roundZDistance, 2 );
	}

	@Override
	public void moveTo( final float[] position )
	{
		assert position.length == 3 : getClass().getCanonicalName() + " cannot process other than 3 dimensions.";

		x = position[ 0 ];
		final int roundX = round( x );
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance != 0 )
			target.move( roundXDistance, 0 );
		
		y = position[ 1 ];
		final int roundY = round( y );
		final int roundYDistance = roundY - target.getIntPosition( 1 );
		if ( roundYDistance != 0 )
			target.move( roundYDistance, 1 );
		
		z = position[ 2 ];
		final int roundZ = round( z );
		final int roundZDistance = roundZ - target.getIntPosition( 2 );
		if ( roundZDistance != 0 )
			target.move( roundZDistance, 2 );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		target.setPosition( round( x ), 0 );
		
		y = localizable.getFloatPosition( 1 );
		target.setPosition( round( y ), 1 );
		
		z = localizable.getFloatPosition( 2 );
		target.setPosition( round( z ), 2 );
	}

	@Override
	public void setPosition( final float[] position )
	{
		x = position[ 0 ];
		target.setPosition( round( x ), 0 );
		
		y = position[ 1 ];
		target.setPosition( round( y ), 1 );
		
		z = position[ 2 ];
		target.setPosition( round( z ), 2 );
	}

	@Override
	public void setPosition( final double[] position )
	{
		x = ( float )position[ 0 ];
		target.setPosition( round( x ), 0 );
		
		y = ( float )position[ 1 ];
		target.setPosition( round( y ), 1 );
		
		z = ( float )position[ 2 ];
		target.setPosition( round( z ), 2 );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = position;
			break;
		case 1:
			y = position;
			break;
		case 2:
			z = position;
		}
		target.setPosition( round( position ), dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = ( float )position;
			break;
		case 1:
			y = ( float )position;
			break;
		case 2:
			z = ( float )position;
		}
		target.setPosition( round( position ), dim );
	}
	
	
	/* RasterPositionable */

	@Override
	public void bck( final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x -= 1;
			break;
		case 1:
			y -= 1;
			break;
		case 2:
			z -= 1;
		}
		target.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x += 1;
			break;
		case 1:
			y += 1;
			break;
		case 2:
			z += 1;
		}
		target.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x += distance;
			break;
		case 1:
			y += distance;
			break;
		case 2:
			z += distance;
		}
		target.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x += distance;
			break;
		case 1:
			y += distance;
			break;
		case 2:
			z += distance;
		}
		target.move( distance, dim );
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		final int roundX = localizable.getIntPosition( 0 );
		x = roundX;
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance != 0 )
			target.move( roundXDistance, 0 );
		
		final int roundY = localizable.getIntPosition( 1 );
		y = roundY;
		final int roundYDistance = roundY - target.getIntPosition( 1 );
		if ( roundYDistance != 0 )
			target.move( roundYDistance, 1 );
		
		final int roundZ = localizable.getIntPosition( 2 );
		z = roundZ;
		final int roundZDistance = roundZ - target.getIntPosition( 2 );
		if ( roundZDistance != 0 )
			target.move( roundZDistance, 2 );
	}

	@Override
	public void moveTo( final int[] position )
	{
		final int roundX = position[ 0 ];
		x = roundX;
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance != 0 )
			target.move( roundXDistance, 0 );
		
		final int roundY = position[ 1 ];
		y = roundY;
		final int roundYDistance = roundY - target.getIntPosition( 1 );
		if ( roundYDistance != 0 )
			target.move( roundYDistance, 1 );
		
		final int roundZ = position[ 2 ];
		z = roundZ;
		final int roundZDistance = roundZ - target.getIntPosition( 2 );
		if ( roundZDistance != 0 )
			target.move( roundZDistance, 2 );
	}

	@Override
	public void moveTo( final long[] position )
	{
		final int roundX = ( int )position[ 0 ];
		x = roundX;
		final int roundXDistance = roundX - target.getIntPosition( 0 );
		if ( roundXDistance != 0 )
			target.move( roundXDistance, 0 );
		
		final int roundY = ( int )position[ 1 ];
		y = roundY;
		final int roundYDistance = roundY - target.getIntPosition( 1 );
		if ( roundYDistance != 0 )
			target.move( roundYDistance, 1 );
		
		final int roundZ = ( int )position[ 2 ];
		z = roundZ;
		final int roundZDistance = roundZ - target.getIntPosition( 2 );
		if ( roundZDistance != 0 )
			target.move( roundZDistance, 2 );
	}

	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		final int roundX = localizable.getIntPosition( 0 );
		x = roundX;
		target.setPosition( roundX, 0 );
		
		final int roundY = localizable.getIntPosition( 1 );
		y = roundY;
		target.setPosition( roundY, 1 );
		
		final int roundZ = localizable.getIntPosition( 2 );
		z = roundZ;
		target.setPosition( roundZ, 2 );
	}

	@Override
	public void setPosition( final int[] position )
	{
		final int roundX = position[ 0 ];
		x = roundX;
		target.setPosition( roundX, 0 );
		
		final int roundY = position[ 1 ];
		y = roundY;
		target.setPosition( roundY, 1 );
		
		final int roundZ = position[ 2 ];
		z = roundZ;
		target.setPosition( roundZ, 2 );
	}

	@Override
	public void setPosition( final long[] position )
	{
		final int roundX = ( int )position[ 0 ];
		x = roundX;
		target.setPosition( roundX, 0 );
		
		final int roundY = ( int )position[ 1 ];
		y = roundY;
		target.setPosition( roundY, 1 );
		
		final int roundZ = ( int )position[ 2 ];
		z = roundZ;
		target.setPosition( roundZ, 2 );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = position;
			break;
		case 1:
			y = position;
			break;
		case 2:
			z = position;
		}
		target.setPosition( position, dim );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		assert dim < 3 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = position;
			break;
		case 1:
			y = position;
			break;
		case 2:
			z = position;
		}
		target.setPosition( position, dim );
	}
}
