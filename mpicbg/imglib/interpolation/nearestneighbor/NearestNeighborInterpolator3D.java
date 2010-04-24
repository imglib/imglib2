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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class NearestNeighborInterpolator3D<T extends Type<T>> extends NearestNeighborInterpolator<T>
{
	private float x, y, z;
	
	protected NearestNeighborInterpolator3D( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory )
	{
		super( img, interpolatorFactory, outOfBoundsStrategyFactory );
		
		x = 0;
		y = 0;
		z = 0;		
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
		linkedPositionable.move( distance, dim );
	}
	
	@Override
	public void move( final float distance, final int dim )
	{
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
		linkedPositionable.move( distance, dim );
	}

	@Override
	public void moveTo( final double[] position )
	{
		x = ( float )position[ 0 ];
		y = ( float )position[ 1 ];
		z = ( float )position[ 2 ];
		linkedPositionable.moveTo( position );
	}

	@Override
	public void moveTo( final float[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedPositionable.moveTo( position );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		y = localizable.getFloatPosition( 1 );
		z = localizable.getFloatPosition( 2 );
		linkedPositionable.moveTo( localizable );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		y = localizable.getFloatPosition( 1 );
		z = localizable.getFloatPosition( 2 );
		linkedPositionable.setPosition( localizable );
	}

	@Override
	public void setPosition( final float[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final double[] position )
	{
		x = ( float )position[ 0 ];
		y = ( float )position[ 1 ];
		z = ( float )position[ 2 ];
		linkedPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
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
		linkedPositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
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
		linkedPositionable.setPosition( position, dim );
	}
	
	
	/* RasterPositionable */

	@Override
	public void bck( final int dim )
	{
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
		linkedRasterPositionable.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
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
		linkedRasterPositionable.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
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
		linkedRasterPositionable.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
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
		linkedRasterPositionable.move( distance, dim );
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		x = localizable.getIntPosition( 0 );
		y = localizable.getIntPosition( 1 );
		z = localizable.getIntPosition( 2 );
		linkedRasterPositionable.moveTo( localizable );
	}

	@Override
	public void moveTo( final int[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedRasterPositionable.moveTo( position );
	}

	@Override
	public void moveTo( final long[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedRasterPositionable.moveTo( position );
	}

	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		x = localizable.getIntPosition( 0 );
		y = localizable.getIntPosition( 1 );
		z = localizable.getIntPosition( 2 );
		linkedRasterPositionable.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedRasterPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedRasterPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
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
		linkedRasterPositionable.setPosition( position, dim );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
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
		linkedRasterPositionable.setPosition( position, dim );
	}
}
