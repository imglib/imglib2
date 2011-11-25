package net.imglib2.script.slice;

import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public final class SliceXZ<R extends RealType<R> & NativeType<R>> extends Slice2D<R>
{
	/**
	 * @param img The 3D image from which to extract a XZ slice.
	 * @param slice The slice (zero-based) to extract.
	 * */
	public SliceXZ(final Img<R> img, final long slice) throws Exception {
		super(img, 0, 2, 1, slice);
	}
}
