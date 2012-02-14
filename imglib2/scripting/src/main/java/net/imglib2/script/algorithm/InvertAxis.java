package net.imglib2.script.algorithm;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class InvertAxis<R extends RealType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	/** Flip one axis of an image; for example, flip the orientation of the x axis.
	 * @param img
	 * @param dimension The index of the dimension to flip; x=0, y=1, z=2, ...
	 */
	public InvertAxis(final RandomAccessibleInterval<R> img, final int dimension) {
		super(Views.zeroMin(Views.invertAxis(img, dimension)));
	}
}
