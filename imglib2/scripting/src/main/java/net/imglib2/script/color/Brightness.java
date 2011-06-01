package net.imglib2.script.color;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.color.fn.HSBOp;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Brightness extends HSBOp {

	/** Extract the brightness component of each pixel, in the range [0, 1]. */
	public Brightness(final IterableRealInterval<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getIndex() { return 2; }
}
