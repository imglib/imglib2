package net.imglib2.script.color.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the red pixel value. */
public abstract class ChannelOp extends RGBAOp {

	public ChannelOp(final IterableRealInterval<? extends ARGBType> img) {
		super(img);
	}

	abstract protected int getShift();

	@Override
	public final double eval() {
		c.fwd();
		return (c.get().get() >> getShift()) & 0xff;
	}
}
