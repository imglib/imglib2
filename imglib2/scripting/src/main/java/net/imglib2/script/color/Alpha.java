package net.imglib2.script.color;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.color.fn.ChannelOp;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the alpha pixel value. */
public class Alpha extends ChannelOp {

	/** Extract the alpha channel of each pixel, in the range [0, 255]. */
	public Alpha(final IterableRealInterval<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 24; }
}
