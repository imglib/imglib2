package net.imglib2.script.color;

import net.imglib2.script.color.fn.ChannelOp;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the blue pixel value. */
public class Blue extends ChannelOp {

	/** Extract the blue channel of each pixel, in the range [0, 255]. */
	public Blue(final Img<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 0; }
}
