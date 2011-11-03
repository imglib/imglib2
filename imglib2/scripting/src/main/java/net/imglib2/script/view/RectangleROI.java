package net.imglib2.script.view;

import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.NumericType;

/** Create a 2D Rectangular ROI on the first plane of the image.
 * 
 * @author Albert Cardona
 */
public class RectangleROI<R extends NumericType<R>> extends ROI<R>
{
	public RectangleROI(RandomAccessible<R> img, Number x, Number y, Number width, Number height) {
		super(img,
				toArray(img.numDimensions(), x.intValue(), y.intValue()),
				toArray(img.numDimensions(), x.intValue() + width.intValue() - 1, y.intValue() + height.intValue() - 1));
	}

	static private final long[] toArray(final int nDim, final int d0, final int d1) {
		final long[] dims = new long[nDim];
		dims[0] = d0;
		dims[1] = d1;
		return dims;
	}
}
