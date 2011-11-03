package net.imglib2.script.view;

import java.util.List;

import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.NumericType;

/** Create a 2D Rectangular ROI on the first plane of the image.
 * 
 * @author Albert Cardona
 */
public class RectangleROI<R extends NumericType<R>> extends ROI<R>
{
	public RectangleROI(final RandomAccessible<R> img, final List<Number> bounds) {
		this(img, bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3));
	}
	
	public RectangleROI(final RandomAccessible<R> img,
			final Number x, final Number y, final Number width, final Number height) {
		super(img,
				toArray(img.numDimensions(), x.intValue(), y.intValue()),
				toArray(img.numDimensions(), x.intValue() + width.intValue() - 1, y.intValue() + height.intValue() - 1));
	}

	static private final long[] toArray(final int nDim, final int p0, final int p1) {
		final long[] pos = new long[nDim];
		pos[0] = p0;
		pos[1] = p1;
		return pos;
	}
}
