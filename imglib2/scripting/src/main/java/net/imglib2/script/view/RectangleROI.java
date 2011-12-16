package net.imglib2.script.view;

import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.NumericType;

/** Create a Rectangular ROI on the x,y plane, with other dimensions left the same.
 * 
 * @author Albert Cardona
 */
public class RectangleROI<R extends NumericType<R>> extends ROI<R>
{
	/**
	 * 
	 * @param img
	 * @param bounds A list containing the bounds values x,y,width,height.
	 */
	public RectangleROI(final RandomAccessibleInterval<R> img, final List<Number> bounds) {
		this(img, bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3));
	}
	
	/**
	 * 
	 * @param img
	 * @param bounds An array containing the bounds values x,y,width,height.
	 */
	@SuppressWarnings("boxing")
	public RectangleROI(final RandomAccessibleInterval<R> img, final long[] bounds) {
		this(img, bounds[0], bounds[1], bounds[2], bounds[3]);
	}

	public RectangleROI(final RandomAccessibleInterval<R> img,
			final Number x, final Number y, final Number width, final Number height) {
		super(img,
				toMinArray(img, x.intValue(), y.intValue()),
				toMaxArray(img, width.intValue(), height.intValue()));
	}

	static private final long[] toMinArray(final RandomAccessibleInterval<?> img, final int p0, final int p1) {
		final long[] pos = new long[img.numDimensions()];
		pos[0] = p0;
		pos[1] = p1;
		return pos;
	}
	
	static private final long[] toMaxArray(final RandomAccessibleInterval<?> img, final int p0, final int p1) {
		final long[] pos = new long[img.numDimensions()];
		pos[0] = p0;
		pos[1] = p1;
		for (int i=2; i<pos.length; ++i) {
			pos[i] = img.dimension(i);
		}
		return pos;
	}
}
