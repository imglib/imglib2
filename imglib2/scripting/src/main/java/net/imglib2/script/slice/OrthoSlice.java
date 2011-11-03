package net.imglib2.script.slice;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class OrthoSlice<R extends RealType<R> & NativeType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	/**
	 * Perform a hyperslice, with img.numDimensions()-1 dimensions;
	 * this means for example a 2D slice for a 3D volume.
	 * 
	 * @param img
	 * @param fixedDimension
	 * @param pos
	 * @throws Exception
	 */
	public OrthoSlice(final Img<R> img, final int fixedDimension, final long startingPosition) throws Exception {
		super(Views.hyperSlice(img, fixedDimension, startingPosition));
	}
}
