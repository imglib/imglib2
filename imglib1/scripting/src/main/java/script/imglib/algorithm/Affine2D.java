package script.imglib.algorithm;

import java.awt.geom.AffineTransform;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.NumericType;
import script.imglib.math.fn.IFunction;

/** 
* Expects matrix values in the same order that {@link AffineTransform} uses.
* 
* The constructors accept either an {@link Image} or an {@link IFunction} from which an {@link Image} is generated. */
public class Affine2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Affine transform the image with the best interpolation mode available. */
	public Affine2D(final Object fn,
			final Number scaleX, final Number shearX, 
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY) throws Exception {
		this(fn, scaleX.floatValue(), shearX.floatValue(), translateX.floatValue(),
				 shearY.floatValue(), scaleY.floatValue(), translateY.floatValue(),
				 Affine3D.BEST, 0);
	}

	/** Affine transform the image with the best interpolation mode available. */
	public Affine2D(final Object fn,
			final Number scaleX, final Number shearX, 
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY,
			final Mode mode, final Number outside) throws Exception {
		super(fn, scaleX.floatValue(), shearX.floatValue(), translateX.floatValue(),
				  shearY.floatValue(), scaleY.floatValue(), translateY.floatValue(),
				  mode, outside);
	}

	public Affine2D(final Object fn, final AffineTransform aff) throws Exception {
		this(fn, aff, Affine3D.BEST);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Mode mode) throws Exception {
		this(fn, aff, mode, 0);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Number outside) throws Exception {
		this(fn, aff, Affine3D.BEST, outside);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Mode mode, final Number outside) throws Exception {
		super(fn, (float)aff.getScaleX(), (float)aff.getShearX(),
				  (float)aff.getShearY(), (float)aff.getScaleY(),
				  (float)aff.getTranslateX(), (float)aff.getTranslateY(),
				  mode, outside);
	}

	public Affine2D(final Object fn,
					final float scaleX, final float shearX,
					final float shearY, final float scaleY,
					final float translateX, final float translateY,
					final Mode mode, final OutOfBoundsStrategyFactory<N> oobf) throws Exception
	{
		super(fn, scaleX, shearX, translateX,
				  shearY, scaleY, translateY, mode, oobf);
	}
}
