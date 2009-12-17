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

public interface Interpolator<T extends Type<T>>
{
	/**
	 * Returns the typed interpolator factory the Interpolator has been instantiated with.
	 * 
	 * @return - the interpolator factory
	 */
	public InterpolatorFactory<T> getInterpolatorFactory();
	
	/**
	 * Returns the typed outside strategy used for interpolation
	 * 
	 * @return - the outside strategy
	 */
	public OutsideStrategyFactory<T> getOutsideStrategyFactory();
	
	/**
	 * Returns the typed image the interpolator is working on
	 * 
	 * @return - the image
	 */
	public Image<T> getImage();
	
	/**
	 * Returns the type which stores the value for the current position of the interpolator
	 * 
	 * @return - the Type object of the interpolator
	 */
	public T getType();
	
	/**
	 * Moves the interpolator to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param position - the floating position of the same dimensionality as the image
	 */
	public void moveTo( float[] position );

	/**
	 * Moves the interpolator a certain distance given by the vector to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param vector - the floating vector of the same dimensionality as the image
	 */
	public void moveRel( float[] vector );
	
	/**
	 * Sets the interpolator to a random position inside or outside the image.
	 * This method is typically less efficient than moving the position
	 * 
	 * @param position - the floating position of the same dimensionality as the image
	 */
	public void setPosition( float[] position );

	/**
	 * Returns the positon of the interpolator.
	 * 
	 * @param position - the floating position of the same dimensionality as the image
	 */
	public void getPosition( float[] position );

	/**
	 * Returns the positon of the interpolator.
	 * 
	 * @return - the floating position of the same dimensionality as the image (as a new object)
	 */
	public float[] getPosition();
	
	/**
	 * Closes the interpolator and with it any cursors or other containers, images or datastructures
	 * that might have been created to make the interpolation work
	 */
	public void close();	
}
