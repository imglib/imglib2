package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

/** Scale a 2D or 3D image, resizing only the 2D planes as necessary. The Z axis is left untouched. */
public class Scale2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Scale the given image in 2D using the best interpolation available. */
	public Scale2D(final Image<N> img, final Number scale) throws Exception {
		this(img, scale, Affine3D.BEST);
	}

	public Scale2D(final Image<N> img, final Number scale, final Mode mode) throws Exception {
		this(img, scale.floatValue(), scale.floatValue(), mode);
	}

	public Scale2D(final Image<N> img, final Number scaleX, Number scaleY) throws Exception {
		this(img, scaleX.floatValue(), scaleY.floatValue(), Affine3D.BEST);
	}

	public Scale2D(final Image<N> img, final Number scaleX, Number scaleY, final Mode mode) throws Exception {
		this(img, scaleX.floatValue(), scaleY.floatValue(), mode);
	}

	public Scale2D(final Image<N> img, final float scaleX, final float scaleY, final Mode mode) throws Exception {
		super(img, new float[]{scaleX, 0, 0, 0,
							   0, scaleY, 0, 0,
							   0, 0, 1, 0}, mode);
	}
}