package mpicbg.imglib.interpolation;

import mpicbg.imglib.type.Type;

public interface Interpolator1D<T extends Type<T>> extends Interpolator<T>
{
	/**
	 * Moves the interpolator to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param float x - the float position in x
	 */
	public void moveTo( float x );

	/**
	 * Moves the interpolator a certain distance given by the vector to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param float x - the float vector in x
	 */
	public void moveRel( float x );
	
	/**
	 * Sets the interpolator to a random position inside or outside the image.
	 * This method is typically less efficient than moving the position
	 * 
	 * @param float x - the float position in x
	 */
	public void setPosition( float x );

	/**
	 * Returns the current x coordinate of the interpolator
	 * 
	 * @return float - x coordinate
	 */
	public float getX();
}
