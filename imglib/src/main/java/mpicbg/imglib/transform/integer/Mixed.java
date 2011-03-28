package mpicbg.imglib.transform.integer;

import Jama.Matrix;
import mpicbg.imglib.transform.Transform;
import mpicbg.imglib.view.BoundingBoxTransform;

public interface Mixed extends Transform, BoundingBoxTransform
{
	/**
	 * Get the translation. Translation is added to the target vector after
	 * applying permutation, projection, inversion operations.
	 * 
	 * @param t
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getTranslation( final long[] translation );

	/**
	 * Get the d-th component of translation {@see #getTranslation(long[])}.
	 * 
	 * @param d
	 */
	public long getTranslation( final int d );

	/**
	 * Get a boolean array indicating which target dimensions are _not_ taken
	 * from source dimensions.
	 * 
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, the result will be [false, false,
	 * true]
	 * </p>
	 * 
	 * @param zero
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getComponentZero( final boolean[] zero );

	/**
	 * Get the d-th component of zeroing vector {@see
	 * #getComponentZero(boolean[])}.
	 * 
	 * @param d
	 */
	public boolean getComponentZero( final int d );

	/**
	 * Get an array indicating for each target dimensions from which source
	 * dimension it is taken.
	 * 
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, the result will be [0, 1, x].
	 * Here, the value of x is undefined because the third target dimension does
	 * not correspond to any source dimension. {@see #getZero(boolean[])}.
	 * </p>
	 * 
	 * @param component
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getComponentMapping( final int[] component );

	/**
	 * Get the source dimension which is mapped to the d-th target dimension
	 * {@see #getComponentMapping(int[])}.
	 * 
	 * @param d
	 */
	public int getComponentMapping( final int d );

	/**
	 * Get an array indicating for each target component, whether the source
	 * component it is taken from should be inverted.
	 * 
	 * <p>
	 * For instance, if rotating a 2D (x,y) coordinates by 180 degrees will map
	 * it to (-x,-y). In this case, the result will be [true, true].
	 * </p>
	 * 
	 * @param component
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getComponentInversion( final boolean[] invert );

	/**
	 * Get the d-th component of inversion vector {@see
	 * #getComponentInversion(boolean[])}.
	 * 
	 * @param d
	 */
	public boolean getComponentInversion( final int d );

	/**
	 * Get the matrix that transforms homogeneous source points to homogeneous
	 * target points. For testing purposes.
	 */
	public Matrix getMatrix();
}
