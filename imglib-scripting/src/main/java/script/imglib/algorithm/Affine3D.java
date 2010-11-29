package script.imglib.algorithm;

import script.imglib.color.Alpha;
import script.imglib.color.Blue;
import script.imglib.color.Green;
import script.imglib.color.RGBA;
import script.imglib.color.Red;
import script.imglib.math.Compute;
import mpicbg.imglib.algorithm.transformation.ImageTransform;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.interpolation.linear.LinearInterpolatorFactory;
import mpicbg.imglib.interpolation.nearestneighbor.NearestNeighborInterpolatorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.models.AffineModel2D;
import mpicbg.models.AffineModel3D;

/** Performs a mathematically correct transformation of an image.
 * This means that an image of 2000x2000 scaled by a factor of 2
 * will result in an image of 3999x3999 pixels.
 * 
 * If the above is not what you expect, then use {@link Resample} instead. */
public class Affine3D<T extends NumericType<T>> extends Image<T>
{	
	static enum Mode { LINEAR, NEAREST_NEIGHBOR };

	static public final Mode LINEAR = Mode.LINEAR;
	static public final Mode NEAREST_NEIGHBOR = Mode.NEAREST_NEIGHBOR;
	static public final Mode BEST = Mode.LINEAR;

	/** Expects a matrix of 12 elements
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
	 *  
	 *  See also {@link AffineModel2D, AffineModel3D};
	 */
	public Affine3D(final Image<T> img, final float[] matrix, final Mode mode) throws Exception {
		this(img, matrix, mode, new OutOfBoundsStrategyValueFactory<T>(img.createType())); // default value is zero
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Image<T> img, final float[] matrix, final Mode mode, final Number outside) throws Exception {
		this(img, matrix, mode, new OutOfBoundsStrategyValueFactory<T>((T)withValue(img, img.createType(), outside))); // default value is zero
	}

	@SuppressWarnings("unchecked")
	protected static final NumericType<?> withValue(final Image<? extends NumericType<?>> img, final NumericType<?> type, final Number val) {
		final NumericType t = img.createType();
		if (RGBALegacyType.class.isAssignableFrom(t.getClass())) {
			int i = val.intValue();
			t.set((NumericType)new RGBALegacyType(i));
		} else {
			((RealType)t).setReal(val.doubleValue());
		}
		return t;
	}

	public Affine3D(final Image<T> img, final float[] matrix, final Mode mode, final OutOfBoundsStrategyFactory<T> oobf) throws Exception {
		super(process(img, matrix, mode, oobf).getContainer(), img.createType());
	}
	
	@SuppressWarnings("unchecked")
	static private final <N extends NumericType<N>>
						Image<N> process(final Image<N> img, final float[] matrix,
						final Mode mode, final OutOfBoundsStrategyFactory<N> oobf) throws Exception {
		if (matrix.length < 12) {
			throw new IllegalArgumentException("Affine transform in 2D requires a matrix array of 12 elements.");
		}
		final Type<?> type = img.createType();
		if (RGBALegacyType.class.isAssignableFrom(type.getClass())) { // type instanceof RGBALegacyType fails to compile
			return (Image)processRGBA((Image)img, matrix, mode, (OutOfBoundsStrategyFactory)oobf);
		} else if (type instanceof RealType<?>) {
			return (Image)processReal((Image)img, matrix, mode, (OutOfBoundsStrategyFactory)oobf);
		} else {
			throw new Exception("Affine transform: cannot handle type " + type.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	static private final Image<RGBALegacyType> processRGBA(final Image<RGBALegacyType> img, final float[] m,
			final Mode mode, final OutOfBoundsStrategyFactory<RGBALegacyType> oobf) throws Exception {
		// Process each channel independently and then compose them back
		OutOfBoundsStrategyFactory<FloatType> ored, ogreen, oblue, oalpha;
		if (OutOfBoundsStrategyValueFactory.class.isAssignableFrom(oobf.getClass())) { // can't use instanceof
			final int val = ((OutOfBoundsStrategyValueFactory<RGBALegacyType>)oobf).getValue().get();
			ored = new OutOfBoundsStrategyValueFactory<FloatType>(new FloatType((val >> 16) & 0xff));
			ogreen = new OutOfBoundsStrategyValueFactory<FloatType>(new FloatType((val >> 8) & 0xff));
			oblue = new OutOfBoundsStrategyValueFactory<FloatType>(new FloatType(val & 0xff));
			oalpha = new OutOfBoundsStrategyValueFactory<FloatType>(new FloatType((val >> 24) & 0xff));
		} else {
			// Jump into the pool!
			try {
				ored = oobf.getClass().newInstance();
			} catch (Exception e) {
				System.out.println("Affine3D for RGBA: oops -- using a black OutOfBoundsStrategyValueFactory");
				ored = new OutOfBoundsStrategyValueFactory<FloatType>(new FloatType());
			}
			ogreen = ored;
			oblue = ored;
			oalpha = ored;
		}
		return new RGBA(processReal(Compute.inFloats(new Red(img)), m, mode, ored),
						processReal(Compute.inFloats(new Green(img)), m, mode, ogreen),
						processReal(Compute.inFloats(new Blue(img)), m, mode, oblue),
						processReal(Compute.inFloats(new Alpha(img)), m, mode, oalpha)).asImage();
	}

	static private final <R extends RealType<R>> Image<R> processReal(final Image<R> img, final float[] m,
			final Mode mode, final OutOfBoundsStrategyFactory<R> oobf) throws Exception {
		final InterpolatorFactory<R> inter;
		switch (mode) {
		case LINEAR:
			inter = new LinearInterpolatorFactory<R>(oobf);
			break;
		case NEAREST_NEIGHBOR:
			inter = new NearestNeighborInterpolatorFactory<R>(oobf);
			break;
		default:
			throw new IllegalArgumentException("Scale: don't know how to scale with mode " + mode);
		}

		final ImageTransform<R> transform;

		if (2 == img.getNumDimensions()) {
			// Transform the single-plane image in 2D
			AffineModel2D aff = new AffineModel2D();
			aff.set(m[0], m[4], m[1], m[5], m[3], m[7]);
			transform = new ImageTransform<R>(img, aff, inter);
		} else if (3 == img.getNumDimensions()) {
			// Transform the image in 3D, or each plane in 2D
			if (m.length < 12) {
				throw new IllegalArgumentException("Affine transform in 3D requires a matrix array of 12 elements.");
			}
			AffineModel3D aff = new AffineModel3D();
			aff.set(m[0], m[1], m[2], m[3],
					m[4], m[5], m[6], m[7],
					m[8], m[9], m[10], m[11]);
			transform = new ImageTransform<R>(img, aff, inter);
			// Ensure Z dimension is not altered if scaleZ is 1:
			if (Math.abs(m[10] - 1.0f) < 0.000001) {
				int[] d = transform.getNewImageSize();
				d[2] = img.getDimension(2); // 0-based: '2' is the third dimension
				transform.setNewImageSize(d);
			}
		} else {
			throw new Exception("Affine transform: only 2D and 3D images are supported.");
		}

		if (!transform.checkInput() || !transform.process()) {
			throw new Exception("Could not affine transform the image: " + transform.getErrorMessage());
		}

		return transform.getResult();
	}
}