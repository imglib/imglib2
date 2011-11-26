package net.imglib2.script.algorithm;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

public class FlipHorizontal<R extends RealType<R>> extends InvertAxis<R>
{
	/** Flip the X axis of an image. */
	public FlipHorizontal(final RandomAccessibleInterval<R> img) {
		super(img, 0);
	}
}
