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

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.type.numeric.NumericType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class LinearInterpolator1D< T extends NumericType< T > > implements Interpolator< T > 
{
	final protected OutOfBoundsStrategyFactory< T > outOfBoundsStrategyFactory;
	final protected Image< T > image;
	final protected T tmp1, tmp2;
	final protected PositionableRasterSampler< T > target;
	
	/* current position, required for relative movement */
	private float x;
	
	protected LinearInterpolator1D( final Image< T > image, final OutOfBoundsStrategyFactory< T > outOfBoundsStrategyFactory )
	{
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.image = image;
		this.target = image.createPositionableRasterSampler( outOfBoundsStrategyFactory );
		
		tmp1 = image.createType();
		tmp2 = image.createType();
		
		x = 0;
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
		return 1;
	}
	
	
	/* Interpolator */
	
	@Override
	public OutOfBoundsStrategyFactory< T > getOutOfBoundsStrategyFactory()
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
	public T type()
	{
		// weights
		final float t = x - target.getFloatPosition( 0 );
		final float t1 = 1.0f - t;

		tmp2.set( target.type() );
		tmp2.mul( t1 );
		
		target.fwd( 0 );
		tmp1.set( target.type() );
		tmp1.mul( t );
		tmp2.add( tmp1 );
		
		return tmp2;
	}
	
	@Override
	@Deprecated
	public T getType(){ return type(); }
	
	
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
	public String getLocationAsString()
	{
		return new StringBuffer( "(" ).append( x ).append( ")" ).toString();
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
	public void move( final float distance, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x += distance;
		final int floorPosition = floor( x );
		final int floorDistance = floorPosition - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void move( final double distance, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x += ( float )distance;
		final int floorPosition = floor( x );
		final int floorDistance = floorPosition - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		final int floorPosition = floor( x );
		final int floorDistance = floorPosition - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void moveTo( final float[] position )
	{
		x = position[ 0 ];
		final int floorPosition = floor( x );
		final int floorDistance = floorPosition - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void moveTo( final double[] position )
	{
		x = ( float )position[ 0 ];
		final int floorPosition = floor( x );
		final int floorDistance = floorPosition - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		x = localizable.getFloatPosition( 0 );
		target.setPosition( floor( x ), 0 );
	}

	@Override
	public void setPosition( final float[] position )
	{
		x = position[ 0 ];
		target.setPosition( floor( x ), 0 );
	}

	@Override
	public void setPosition( final double[] position )
	{
		x = ( float )position[ 0 ];
		target.setPosition( floor( x ), 0 );
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = position;
		target.setPosition( floor( position ), 0 );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = ( float )position;
		target.setPosition( floor( position ), 0 );
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
		final int floorX = localizable.getIntPosition( 0 );
		x = floorX;
		final int floorDistance = floorX - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void moveTo( final int[] position )
	{
		final int floorX = position[ 0 ];
		x = floorX;
		final int floorDistance = floorX - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}

	@Override
	public void moveTo( final long[] position )
	{
		final int floorX = ( int )position[ 0 ];
		x = floorX;
		final int floorDistance = floorX - target.getIntPosition( 0 );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, 0 );
	}
	
	@Override
	public void setPosition( RasterLocalizable localizable )
	{
		final int floorX = localizable.getIntPosition( 0 );
		x = floorX;
		target.setPosition( floorX, 0 );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		final int floorX = position[ 0 ];
		x = floorX;
		target.setPosition( floorX, 0 );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		final int floorX = ( int )position[ 0 ];
		x = floorX;
		target.setPosition( floorX, 0 );
	}

	@Override
	public void setPosition( int position, int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = position;
		target.setPosition( position, 0 );
	}

	@Override
	public void setPosition( long position, int dim )
	{
		assert dim == 0 : getClass().getCanonicalName() + " cannot process " + ( dim + 1 ) + " dimensions.";

		x = position;
		target.setPosition( position, 0 );
	}
}
