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
package mpicbg.imglib.interpolation;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public abstract class InterpolatorImpl<T extends Type<T>> implements Interpolator<T>
{
	final protected InterpolatorFactory<T> interpolatorFactory;
	final protected OutsideStrategyFactory<T> outsideStrategyFactory;
	final protected Image<T> img;

	// the location of the interpolator in the image
	final protected float[] position, tmp;

	/**
	 * the number of dimensions 
	 */
	final protected int numDimensions;
	
	protected InterpolatorImpl( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		this.interpolatorFactory = interpolatorFactory;
		this.outsideStrategyFactory = outsideStrategyFactory;
		this.img = img;
		this.numDimensions = img.getNumDimensions();
	
		tmp = new float[ numDimensions ];
		position = new float[ numDimensions ];
	}

	/**
	 * Returns the typed interpolator factory the Interpolator has been instantiated with.
	 * 
	 * @return - the interpolator factory
	 */
	@Override
	public InterpolatorFactory<T> getInterpolatorFactory(){ return interpolatorFactory; }
	
	/**
	 * Returns the typed outside strategy used for interpolation
	 * 
	 * @return - the outside strategy
	 */
	@Override
	public OutsideStrategyFactory<T> getOutsideStrategyFactory() { return outsideStrategyFactory; }
	
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
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public float[] getPosition() { return position.clone();	}	

	@Override
	public void moveRel( final float[] vector )
	{		
		for ( int d = 0; d < numDimensions; ++d )
			tmp[ d ] = position[ d ] + vector[ d ];
		
		moveTo( tmp );
	}
	
}
