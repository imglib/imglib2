package net.imglib2.script.algorithm;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

public class FlipVertical<R extends RealType<R>> extends InvertAxis<R>
{
	/** Flip the Y axis of an image. */
	public FlipVertical(final RandomAccessibleInterval<R> img) {
		super(img, 1);
	}
}
