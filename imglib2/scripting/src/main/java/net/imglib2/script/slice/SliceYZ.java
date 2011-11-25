package net.imglib2.script.slice;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public final class SliceYZ<R extends RealType<R> & NativeType<R>> extends Slice2D<R>
{
	/**
	 * @param img The 3D image from which to extract a YZ slice.
	 * @param slice The slice (zero-based) to extract.
	 * */
	public SliceYZ(final Img<R> img, final long slice) throws Exception {
		super(img, 1, 2, 0, slice);
	}
}
