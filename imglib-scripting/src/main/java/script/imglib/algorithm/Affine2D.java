package script.imglib.algorithm;

import java.awt.geom.AffineTransform;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.NumericType;

/** 
* Expects matrix values in the same order that {@link AffineTransform} uses.
*/
public class Affine2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Affine transform the @param img with the best interpolation mode available. */
	public Affine2D(final Image<N> img,
			final Number scaleX, final Number shearX, 
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY) throws Exception {
		this(img, scaleX.floatValue(), shearX.floatValue(), translateX.floatValue(),
				  shearY.floatValue(), scaleY.floatValue(), translateY.floatValue(),
				  Affine3D.BEST, 0);
	}

	/** Affine transform the @param img with the best interpolation mode available. */
	@SuppressWarnings("unchecked")
	public Affine2D(final Image<N> img,
			final Number scaleX, final Number shearX, 
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY,
			final Mode mode, final Number outside) throws Exception {
		this(img, scaleX.floatValue(), shearX.floatValue(), translateX.floatValue(),
				  shearY.floatValue(), scaleY.floatValue(), translateY.floatValue(),
				  mode, new OutOfBoundsStrategyValueFactory<N>((N)withValue(img, img.createType(), outside)));
	}

	public Affine2D(final Image<N> img, final AffineTransform aff) throws Exception {
		this(img, aff, Affine3D.BEST);
	}

	public Affine2D(final Image<N> img, final AffineTransform aff, final Mode mode) throws Exception {
		this(img, aff, mode, 0);
	}

	public Affine2D(final Image<N> img, final AffineTransform aff, final Number outside) throws Exception {
		this(img, aff, Affine3D.BEST, outside);
	}

	@SuppressWarnings("unchecked")
	public Affine2D(final Image<N> img, final AffineTransform aff, final Mode mode, final Number outside) throws Exception {
		this(img, (float)aff.getScaleX(), (float)aff.getShearX(),
				  (float)aff.getShearY(), (float)aff.getScaleY(),
				  (float)aff.getTranslateX(), (float)aff.getTranslateY(),
				  mode, new OutOfBoundsStrategyValueFactory<N>((N)withValue(img, img.createType(), outside)));
	}

	public Affine2D(final Image<N> img,
					final float scaleX, final float shearX,
					final float shearY, final float scaleY,
					final float translateX, final float translateY,
					final Mode mode, final OutOfBoundsStrategyFactory<N> oobf) throws Exception
	{
		super(img, new float[]{scaleX, shearX, 0, translateX,
							   shearY, scaleY, 0, translateY,
							   0, 0, 1, 0}, mode, oobf);
	}
}