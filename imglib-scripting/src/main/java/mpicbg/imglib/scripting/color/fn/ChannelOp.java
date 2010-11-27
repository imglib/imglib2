package mpicbg.imglib.scripting.color.fn;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the red pixel value. */
public abstract class ChannelOp extends RGBAOp {

	public ChannelOp(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	abstract protected int getShift();

	@Override
	public final double eval() {
		c.fwd();
		return (c.getType().get() >> getShift()) & 0xff;
	}
}