/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator3D;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class NearestNeighborInterpolator3D<T extends Type<T>> extends NearestNeighborInterpolator<T> implements Interpolator3D<T>
{
	//final LocalizableByDimCursor<T> cursor;
	//final T type;
	
	float x, y, z;
	
	protected NearestNeighborInterpolator3D( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		super( img, interpolatorFactory, outsideStrategyFactory );
		
		//cursor = img.createLocalizableByDimCursor( outsideStrategyFactory );
		//type = cursor.getType();
		
		x = 0;
		y = 0;
		z = 0;		
	}

	/**
	 * Returns the typed image the interpolator is working on
	 * 
	 * @return - the image
	 */
	@Override
	public Image<T> getImage() { return img; }		
	
	@Override
	public void getPosition( final float[] position )
	{
		position[ 0 ] = x;
		position[ 1 ] = y;
		position[ 2 ] = z;
	}

	@Override
	public float[] getPosition() 
	{ 
		return new float[]{ x, y, z };	
	}	
	
	@Override
	public void close() { cursor.close(); }

	@Override
	public T getType() { return type; }
	
	@Override
	public void moveTo( final float x, final float y, final float z )
	{		
		this.x = x;
		this.y = y;
		this.z = z;
		
		final int ix = MathLib.round( x ); //(int)(x + (0.5f * Math.signum(x) )); 
		final int iy = MathLib.round( y ); //(int)(y + (0.5f * Math.signum(y) )); 
		final int iz = MathLib.round( z ); //(int)(z + (0.5f * Math.signum(z) )); 
		
		cursor.move( ix - cursor.getPosition( 0 ), 0 );
		cursor.move( iy - cursor.getPosition( 1 ), 1 );
		cursor.move( iz - cursor.getPosition( 2 ), 2 );
		
		/*
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[d];

			final int pos = Math.round( position[d] );
			cursor.move( pos - cursor.getPosition(d), d );
		}

		*/
	}

	@Override
	public void moveTo( final float[] position )
	{
		moveTo( position[0], position[1], position[2] );
	}

	@Override
	public void moveRel( final float x, final float y, final float z )
	{
		this.x += x;
		this.y += y;
		this.z += z;

		//cursor.move( (int)( this.x + (0.5f * Math.signum(this.x) ) ) - cursor.getPosition( 0 ), 0 );
		//cursor.move( (int)( this.y + (0.5f * Math.signum(this.y) ) ) - cursor.getPosition( 1 ), 1 );
		//cursor.move( (int)( this.z + (0.5f * Math.signum(this.z) ) ) - cursor.getPosition( 2 ), 2 );
		
		cursor.move( MathLib.round( this.x ) - cursor.getPosition( 0 ), 0 );
		cursor.move( MathLib.round( this.y ) - cursor.getPosition( 1 ), 1 );
		cursor.move( MathLib.round( this.z ) - cursor.getPosition( 2 ), 2 );
		
		/*
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] += vector[ d ];
			
			final int pos = Math.round( position[d] );			
			cursor.move( pos - cursor.getPosition(d), d );
		}
		*/
	}
	
	@Override
	public void moveRel( final float[] vector )
	{
		moveRel( vector[0], vector[1], vector[2] );
	}
	
	@Override
	public void setPosition( final float x, final float y, final float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;

		//cursor.setPosition( (int)(x + (0.5f * Math.signum(x) ) ), 0 );
		//cursor.setPosition( (int)(y + (0.5f * Math.signum(y) ) ), 1 );
		//cursor.setPosition( (int)(z + (0.5f * Math.signum(z) ) ), 2 );
		
		cursor.setPosition( MathLib.round( x ), 0 );
		cursor.setPosition( MathLib.round( y ), 1 );
		cursor.setPosition( MathLib.round( z ), 2 );
		
		/*
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[d];

			final int pos = Math.round( position[d] );
			cursor.setPosition( pos, d );
		}
		*/
	}
	
	@Override
	public void setPosition( final float[] position )
	{
		setPosition( position[0], position[1], position[2] );
	}

	@Override
	public float getX() { return x;	}

	@Override
	public float getY() { return y; }

	@Override
	public float getZ() { return z;	}
}
