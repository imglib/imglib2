package mpicbg.imglib.interpolation;

import mpicbg.imglib.type.Type;

public interface Interpolator2D<T extends Type<T>> extends Interpolator<T>
{
	/**
	 * Moves the interpolator to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param float x - the float position in x
	 * @param float y - the float position in y
	 */
	public void moveTo( float x, float y );

	/**
	 * Moves the interpolator a certain distance given by the vector to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param float x - the float vector in x
	 * @param float y - the float vector in y
	 */
	public void moveRel( float x, float y );
	
	/**
	 * Sets the interpolator to a random position inside or outside the image.
	 * This method is typically less efficient than moving the position
	 * 
	 * @param float x - the float position in x
	 * @param float y - the float position in y
	 */
	public void setPosition( float x, float y );

	/**
	 * Returns the current x coordinate of the interpolator
	 * 
	 * @return float - x coordinate
	 */
	public float getX();

	/**
	 * Returns the current y coordinate of the interpolator
	 * 
	 * @return float - y coordinate
	 */
	public float getY();

}
