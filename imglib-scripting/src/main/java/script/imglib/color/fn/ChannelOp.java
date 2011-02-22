package script.imglib.color.fn;

import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.ARGBType;

/** Extracts the red pixel value. */
public abstract class ChannelOp extends RGBAOp {

	public ChannelOp(final Img<? extends ARGBType> img) {
		super(img);
	}

	abstract protected int getShift();

	@Override
	public final double eval() {
		c.fwd();
		return (c.get().get() >> getShift()) & 0xff;
	}
}