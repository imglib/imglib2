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
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.NumericType;

public class LinearInterpolator3D<T extends NumericType<T>> extends LinearInterpolator<T>
{
	private float x, y, z;
	
	protected LinearInterpolator3D( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory )
	{
		super( img, interpolatorFactory, outOfBoundsStrategyFactory, false );
	}
	
	@Override
	public T type()
	{
		// How to iterate the cube
		//
		//       y7     y6
		//        *------>*
		//       ^       /|
		//   y3 /    y2 / v
		//     *<------*  * y5
		//     |    x  ^ /
		//     |       |/
		//     *------>*
		//     y0    y1

		// weights
		final float t = position[ 0 ] - cursor.getFloatPosition( 0 );
		final float u = position[ 1 ] - cursor.getFloatPosition( 1 );
		final float v = position[ 2 ] - cursor.getFloatPosition( 2 );

		final float t1 = 1 - t;
		final float u1 = 1 - u;
		final float v1 = 1 - v;

		//final float tmp2 = t1*u1*v1*y0 + t*u1*v1*y1 + t*u*v1*y2 + t1*u*v1*y3 +
		//			     	  t1*u1*v*y4  + t*u1*v*y5  + t*u*v*y6  + t1*u*v*y7;

		//final float y0 = strategy.get(baseX1    , baseX2,     baseX3);
		tmp1.set( cursor.type() );
		tmp1.mul( t1 );
		tmp1.mul( u1 );
		tmp1.mul( v1 );
		tmp2.set( tmp1 );

		//final float y1 = strategy.get(baseX1 + 1, baseX2,     baseX3);
		cursor.fwd( 0 );
		tmp1.set( cursor.type() );
		tmp1.mul( t );
		tmp1.mul( u1 );
		tmp1.mul( v1 );
		tmp2.add( tmp1 );

		//final float y2 = strategy.get(baseX1 + 1, baseX2 + 1, baseX3);
		cursor.fwd( 1 );
		tmp1.set( cursor.type() );
		tmp1.mul( t );
		tmp1.mul( u );
		tmp1.mul( v1 );
		tmp2.add( tmp1 );

		//final float y3 = strategy.get(baseX1    , baseX2 + 1, baseX3);
		cursor.bck( 0 );
		tmp1.set( cursor.type() );
		tmp1.mul( t1 );
		tmp1.mul( u );
		tmp1.mul( v1 );
		tmp2.add( tmp1 );

		//final float y7 = strategy.get(baseX1    , baseX2 + 1, baseX3 + 1);
		cursor.fwd( 2 );
		tmp1.set( cursor.type() );
		tmp1.mul( t1 );
		tmp1.mul( u );
		tmp1.mul( v );
		tmp2.add( tmp1 );

		//final float y6 = strategy.get(baseX1 + 1, baseX2 + 1, baseX3 + 1);
		cursor.fwd( 0 );
		tmp1.set( cursor.type() );
		tmp1.mul( t );
		tmp1.mul( u );
		tmp1.mul( v );
		tmp2.add( tmp1 );

		//final float y5 = strategy.get(baseX1 + 1, baseX2,     baseX3 + 1);
		cursor.bck( 1 );
		tmp1.set( cursor.type() );
		tmp1.mul( t );
		tmp1.mul( u1 );
		tmp1.mul( v );
		tmp2.add( tmp1 );

		//final float y4 = strategy.get(baseX1    , baseX2,	  baseX3 + 1);
		cursor.bck( 0 );
		tmp1.set( cursor.type() );
		tmp1.mul( t1 );
		tmp1.mul( u1 );
		tmp1.mul( v );
		tmp2.add( tmp1 );
		
		return tmp2;
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
		linkedPositionable.moveTo( position );
	}

	@Override
	public void moveTo( final long[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedPositionable.moveTo( position );
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
		linkedPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		x = position[ 0 ];
		y = position[ 1 ];
		z = position[ 2 ];
		linkedPositionable.setPosition( position );
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
