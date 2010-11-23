package mpicbg.imglib.scripting.rgb.op;

import java.awt.Color;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

/** Extracts the HSB saturation of an RGB pixel. */
public abstract class HSBOp<R extends RealType<R> > extends RGBAOp<R> {

	private final float[] hsb = new float[3];

	public HSBOp(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	abstract protected int getIndex();

	@Override
	public final void compute(final R output) {
		final int v = c.getType().get();
		Color.RGBtoHSB((v >> 16) & 0xff, (v >> 8) & 0xff, v & 0xff, hsb);
		output.setReal(hsb[getIndex()]);
	}
}