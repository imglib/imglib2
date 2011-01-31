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
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.RasterOutOfBoundsFactory;
import mpicbg.imglib.sampler.PositionableRasterIntervalSampler;
import mpicbg.imglib.type.numeric.NumericType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class LinearInterpolator2D< T extends NumericType< T > > implements Interpolator< T > 
{
	final protected RasterOutOfBoundsFactory< T > outOfBoundsStrategyFactory;
	final protected Image< T > image;
	final protected T tmp1, tmp2;
	final protected PositionableRasterIntervalSampler< T > target;
	
	/* current position, required for relative movement */
	private float x, y;
	
	protected LinearInterpolator2D( final Image< T > image, final RasterOutOfBoundsFactory< T > outOfBoundsStrategyFactory )
	{
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.image = image;
		this.target = image.createPositionableRasterSampler( outOfBoundsStrategyFactory );
		
		tmp1 = image.createType();
		tmp2 = image.createType();
		
		x = y = 0;
	}
	
	final static private int floor( final double r )
	{
		return r < 0 ? ( int )r - 1 : ( int )r;
	}
	
	final static private int floor( final float r )
	{
		return r < 0 ? ( int )r - 1 : ( int )r;
	}
	
	
	/* Dimensionality */
	
	@Override
	final public int numDimensions()
	{
		return 2;
	}
	
	
	/* Interpolator */
	
	@Override
	public RasterOutOfBoundsFactory< T > getOutOfBoundsStrategyFactory()
	{
		return outOfBoundsStrategyFactory;
	}

	@Override
	public Image< T > getImage()
	{
		return image;
	}
	
	@Override
	public void close() { target.close(); }
	
	
	/* Sampler */
	
	@Override
	public T get()
	{
		// weights
		final float t = x - target.getFloatPosition( 0 );
		final float u = y - target.getFloatPosition( 1 );

		final float t1 = 1.0f - t;
		final float u1 = 1.0f - u;

		tmp2.set( target.get() );
		tmp2.mul( t1 );
		tmp2.mul( u1 );
		
		target.fwd( 0 );
		tmp1.set( target.get() );
		tmp1.mul( t );
		tmp1.mul( u1 );
		tmp2.add( tmp1 );

		target.fwd( 1 );
		tmp1.set( target.get() );
		tmp1.mul( t );
		tmp1.mul( u );
		tmp2.add( tmp1 );

		target.bck( 0 );
		tmp1.set( target.get() );
		tmp1.mul( t1 );
		tmp1.mul( u );
		tmp2.add( tmp1 );
		
		return tmp2;
	}
	
	@Override
	@Deprecated
	public T getType(){ return get(); }
	
	
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
		default:
			return 0;
		}
	}

	@Override
	public String toString()
	{
		return new StringBuffer( "(" ).append( x ).append( ", " ).append( y ).append( ")" ).toString();
	}

	@Override
	public void localize( final float[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		position[ 0 ] = x;
		position[ 1 ] = y;
	}

	@Override
	public void localize( final double[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		position[ 0 ] = x;
		position[ 1 ] = y;
	}
	
	
	/* Positionable */

	@Override
	public void move( final double distance, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		final int floorPosition;
		switch ( dim )
		{
		case 0:
			x += distance;
			floorPosition = floor( x );
			break;
		case 1:
			y += distance;
			floorPosition = floor( y );
			break;
		default:
			floorPosition = 0;
		}
		final int floorDistance = floorPosition - target.getIntPosition( dim );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, dim );
	}
	
	@Override
	public void move( final float distance, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";
		
		final int floorPosition;
		switch ( dim )
		{
		case 0:
			x += distance;
			floorPosition = floor( x );
			break;
		case 1:
			y += distance;
			floorPosition = floor( y );
			break;
		default:
			floorPosition = 0;
		}
		final int floorDistance = floorPosition - target.getIntPosition( dim );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, dim );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		assert localizable.numDimensions() == 2 : getClass().getCanonicalName() + " cannot process other than 3 dimensions.";

		x = localizable.getFloatPosition( 0 );
		final int floorX = floor( x );
		final int floorXDistance = floorX - target.getIntPosition( 0 );
		if ( floorXDistance != 0 )
			target.move( floorXDistance, 0 );
		
		y = localizable.getFloatPosition( 1 );
		final int floorY = floor( y );
		final int floorYDistance = floorY - target.getIntPosition( 1 );
		if ( floorYDistance != 0 )
			target.move( floorYDistance, 1 );
	}

	@Override
	public void moveTo( final double[] position )
	{
		assert position.length == 2 : getClass().getCanonicalName() + " cannot process other than 3 dimensions.";

		x = ( float )position[ 0 ];
		final int floorX = floor( x );
		final int floorXDistance = floorX - target.getIntPosition( 0 );
		if ( floorXDistance != 0 )
			target.move( floorXDistance, 0 );
		
		y = ( float )position[ 1 ];
		final int floorY = floor( y );
		final int floorYDistance = floorY - target.getIntPosition( 1 );
		if ( floorYDistance != 0 )
			target.move( floorYDistance, 1 );
	}

	@Override
	public void moveTo( final float[] position )
	{
		assert position.length == 2 : getClass().getCanonicalName() + " cannot process other than 3 dimensions.";

		x = position[ 0 ];
		final int floorX = floor( x );
		final int floorXDistance = floorX - target.getIntPosition( 0 );
		if ( floorXDistance != 0 )
			target.move( floorXDistance, 0 );
		
		y = position[ 1 ];
		final int floorY = floor( y );
		final int floorYDistance = floorY - target.getIntPosition( 1 );
		if ( floorYDistance != 0 )
			target.move( floorYDistance, 1 );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() >= 2 : getClass().getCanonicalName() + " cannot process " + ( localizable.numDimensions() ) + " dimensions.";
		
		x = localizable.getFloatPosition( 0 );
		target.setPosition( floor( x ), 0 );
		
		y = localizable.getFloatPosition( 1 );
		target.setPosition( floor( y ), 1 );
	}

	@Override
	public void setPosition( final float[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		x = position[ 0 ];
		target.setPosition( floor( x ), 0 );
		
		y = position[ 1 ];
		target.setPosition( floor( y ), 1 );
	}

	@Override
	public void setPosition( final double[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		x = ( float )position[ 0 ];
		target.setPosition( floor( x ), 0 );
		
		y = ( float )position[ 1 ];
		target.setPosition( floor( y ), 1 );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = position;
			break;
		case 1:
			y = position;
		}
		target.setPosition( floor( position ), dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = ( float )position;
			break;
		case 1:
			y = ( float )position;
		}
		target.setPosition( floor( position ), dim );
	}
	
	
	/* RasterPositionable */

	@Override
	public void bck( final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x -= 1;
			break;
		case 1:
			y -= 1;
		}
		target.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x += 1;
			break;
		case 1:
			y += 1;
		}
		target.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x += distance;
			break;
		case 1:
			y += distance;
		}
		target.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x += distance;
			break;
		case 1:
			y += distance;
		}
		target.move( distance, dim );
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		assert localizable.numDimensions() >= 2 : getClass().getCanonicalName() + " cannot process " + ( localizable.numDimensions() ) + " dimensions.";
		
		final int floorX = localizable.getIntPosition( 0 );
		x = floorX;
		final int floorXDistance = floorX - target.getIntPosition( 0 );
		if ( floorXDistance != 0 )
			target.move( floorXDistance, 0 );
		
		final int floorY = localizable.getIntPosition( 1 );
		y = floorY;
		final int floorYDistance = floorY - target.getIntPosition( 1 );
		if ( floorYDistance != 0 )
			target.move( floorYDistance, 1 );
	}

	@Override
	public void moveTo( final int[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		final int floorX = position[ 0 ];
		x = floorX;
		final int floorXDistance = floorX - target.getIntPosition( 0 );
		if ( floorXDistance != 0 )
			target.move( floorXDistance, 0 );
		
		final int floorY = position[ 1 ];
		y = floorY;
		final int floorYDistance = floorY - target.getIntPosition( 1 );
		if ( floorYDistance != 0 )
			target.move( floorYDistance, 1 );
	}

	@Override
	public void moveTo( final long[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		final int floorX = ( int )position[ 0 ];
		x = floorX;
		final int floorXDistance = floorX - target.getIntPosition( 0 );
		if ( floorXDistance != 0 )
			target.move( floorXDistance, 0 );
		
		final int floorY = ( int )position[ 1 ];
		y = floorY;
		final int floorYDistance = floorY - target.getIntPosition( 1 );
		if ( floorYDistance != 0 )
			target.move( floorYDistance, 1 );
	}

	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		assert localizable.numDimensions() >= 2 : getClass().getCanonicalName() + " cannot process " + ( localizable.numDimensions() ) + " dimensions.";
		
		final int floorX = localizable.getIntPosition( 0 );
		x = floorX;
		target.setPosition( floorX, 0 );
		
		final int floorY = localizable.getIntPosition( 1 );
		y = floorY;
		target.setPosition( floorY, 1 );
	}

	@Override
	public void setPosition( final int[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		final int floorX = position[ 0 ];
		x = floorX;
		target.setPosition( floorX, 0 );
		
		final int floorY = position[ 1 ];
		y = floorY;
		target.setPosition( floorY, 1 );
	}

	@Override
	public void setPosition( final long[] position )
	{
		assert position.length >= 2 : getClass().getCanonicalName() + " cannot process " + ( position.length ) + " dimensions.";
		
		final int floorX = ( int )position[ 0 ];
		x = floorX;
		target.setPosition( floorX, 0 );
		
		final int floorY = ( int )position[ 1 ];
		y = floorY;
		target.setPosition( floorY, 1 );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = position;
			break;
		case 1:
			y = position;
		}
		target.setPosition( position, dim );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		assert dim < 2 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		switch ( dim )
		{
		case 0:
			x = position;
			break;
		case 1:
			y = position;
		}
		target.setPosition( position, dim );
	}
}
