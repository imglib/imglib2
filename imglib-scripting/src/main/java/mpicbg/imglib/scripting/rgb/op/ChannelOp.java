package mpicbg.imglib.scripting.rgb.op;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

/** Extracts the red pixel value. */
public abstract class ChannelOp<R extends RealType<R> > extends RGBAOp<R> {

	public ChannelOp(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	abstract protected int getShift();

	@Override
	public final void compute(final R output) {
		output.setReal((c.getType().get() >> getShift()) & 0xff);
	}
}