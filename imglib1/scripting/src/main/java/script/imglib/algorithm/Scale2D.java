package script.imglib.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.type.numeric.NumericType;
import script.imglib.math.fn.IFunction;

/** Scale a 2D or 3D image, resizing only the 2D planes as necessary. The Z axis is left untouched.
 * 
 *  The constructors accept either an {@link Image} or an {@link IFunction} from which an {@link Image} is generated. */
public class Scale2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Scale the given image in 2D using the best interpolation available. */
	public Scale2D(final Object fn, final Number scale) throws Exception {
		this(fn, scale.floatValue(), scale.floatValue(), Affine3D.BEST);
	}

	public Scale2D(final Object fn, final Number scale, final Mode mode) throws Exception {
		this(fn, scale.floatValue(), scale.floatValue(), mode);
	}

	public Scale2D(final Object fn, final Number scaleX, Number scaleY) throws Exception {
		this(fn, scaleX.floatValue(), scaleY.floatValue(), Affine3D.BEST);
	}

	public Scale2D(final Object fn, final Number scaleX, Number scaleY, final Mode mode) throws Exception {
		this(fn, scaleX.floatValue(), scaleY.floatValue(), mode);
	}

	public Scale2D(final Object fn, final float scaleX, final float scaleY, final Mode mode) throws Exception {
		super(fn, new float[]{scaleX, 0, 0, 0,
							   0, scaleY, 0, 0,
							   0, 0, 1, 0},
			  mode, new OutOfBoundsStrategyMirrorFactory<N>());
	}
}
