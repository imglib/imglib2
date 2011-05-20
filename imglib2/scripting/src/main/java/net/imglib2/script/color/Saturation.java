package net.imglib2.script.color;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.color.fn.HSBOp;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Saturation extends HSBOp {

	/** Extract the saturation component of each pixel, in the range [0, 1]. */
	public Saturation(final IterableRealInterval<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getIndex() { return 1; }
}
