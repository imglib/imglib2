package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

/** Scale an image in 3D, resizing the X,Y,Z dimensions as necessary. */
public class Scale3D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Scale the given image in 2D using the best interpolation available. */
	public Scale3D(final Image<N> img, final Number scale) throws Exception {
		this(img, scale, Affine3D.BEST);
	}

	public Scale3D(final Image<N> img, final Number scale, final Mode mode) throws Exception {
		this(img, scale.floatValue(), scale.floatValue(), scale.floatValue(), mode);
	}

	public Scale3D(final Image<N> img, final Number scaleX, final Number scaleY, final Number scaleZ) throws Exception {
		this(img, scaleX.floatValue(), scaleY.floatValue(), scaleZ.floatValue(), Affine3D.BEST);
	}

	public Scale3D(final Image<N> img, final Number scaleX, final Number scaleY, final Number scaleZ, final Mode mode) throws Exception {
		this(img, scaleX.floatValue(), scaleY.floatValue(), scaleZ.floatValue(), mode);
	}

	public Scale3D(final Image<N> img, final float scaleX, final float scaleY, final float scaleZ, final Mode mode) throws Exception {
		super(img, new float[]{scaleX, 0, 0, 0,
							   0, scaleY, 0, 0,
							   0, 0, scaleZ, 0}, mode);
	}
}