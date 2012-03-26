package script.imglib.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.NumericType;
import script.imglib.algorithm.fn.AbstractAffine3D;
import script.imglib.algorithm.fn.AlgorithmUtil;

/** Performs a mathematically correct transformation of an image.
 * This means that an image of 2000x2000 scaled by a factor of 2
 * will result in an image of 3999x3999 pixels.
 * 
 * If the above is not what you expect, then use {@link Resample} instead.
 * 
 * 
 * Expects a matrix of 12 elements
 * 
 *  For 2D image it will do:
 *
 *  AffineModel2D aff = new AffineModel2D();
 *  aff.set(m[0], m[4], m[1], m[5], m[3], m[7]);
 * 
 * 
 *  For 3D image it will do:
 *  
 *  AffineModel3D aff = new AffineModel3D();
 *  aff.set(m[0], m[1], m[2], m[3],
 *          m[4], m[5], m[6], m[7],
 *          m[8], m[9], m[10], m[11]);
 *          
 *  For RGBA images, each channel will be transformed independently. Hence,
 *  the operation will take 4 times as long and require 4 times the memory of a single operation.
 *          
 *  @param img The {@link Image} to transform.
 *  @param matrix The values of the transformation matrix, ordered as explained above.
 *  @param mode Either LINEAR or NEAREST_NEIGHBOR.
 *  
 *  The constructors accept either an {@link Image} or an {@link IFunction} from which an {@link Image} is generated.
 *  
 *  See the underlying transformation model classes: {@link AffineModel2D, AffineModel3D};
 */
public class Affine3D<T extends NumericType<T>> extends AbstractAffine3D<T>
{	
	public Affine3D(final Image<T> img, final float[] matrix, final Mode mode) throws Exception {
		this(img, matrix, mode, new OutOfBoundsStrategyValueFactory<T>(img.createType())); // default value is zero
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn, final float[] matrix, final Mode mode, final Number outside) throws Exception {
		super(AlgorithmUtil.wrap(fn), matrix, mode, outside);
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn, final float[] matrix, final Mode mode, final OutOfBoundsStrategyFactory<T> oobf) throws Exception {
		super(AlgorithmUtil.wrap(fn), matrix, mode, oobf);
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn,
			final float scaleX, final float shearX,
			final float shearY, final float scaleY,
			final float translateX, final float translateY,
			final Mode mode, final Number outside) throws Exception {
		super(AlgorithmUtil.wrap(fn), new float[]{scaleX, shearX, 0, translateX,
				  						 shearY, scaleY, 0, translateY,
				  			   			 0, 0, 1, 0}, mode, outside);
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn,
			final float scaleX, final float shearX,
			final float shearY, final float scaleY,
			final float translateX, final float translateY,
			final Mode mode, final OutOfBoundsStrategyFactory<T> oobf) throws Exception {
		super(AlgorithmUtil.wrap(fn), new float[]{scaleX, shearX, 0, translateX,
				  						 shearY, scaleY, 0, translateY,
				  			   			 0, 0, 1, 0}, mode, oobf);
	}
}
