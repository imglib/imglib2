package net.imglib2.script.slice;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class Slice2D<R extends RealType<R> & NativeType<R>> extends OrthoSlice<R>
{
	private final long slice;

	/**
	 * @param img The 3D image from which to extract a 2D slice.
	 * @param firstDimension The first dimension to use for the 2D slice.
	 * @param secondDimension The second dimension to use for the 2D slice.
	 * @param fixedDimension The dimension that remains fixed in the 3D image.
	 * @param slice The slice (zero-based) to extract.
	 * */
	public Slice2D(final Img<R> img, final int firstDimension, final int secondDimension,
			final int fixedDimension, final long slice) throws Exception {
		super(img, fixedDimension, slice);
		this.slice = slice;
	}

	/** The index of the selected slice. */
	public final long getSlice() {
		return this.slice;
	}
}
