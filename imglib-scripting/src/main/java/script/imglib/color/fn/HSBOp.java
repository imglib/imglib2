package script.imglib.color.fn;

import java.awt.Color;

import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.ARGBType;

/** Extracts the HSB saturation of an RGB pixel. */
public abstract class HSBOp extends RGBAOp {

	private final float[] hsb = new float[3];

	public HSBOp(final Img<? extends ARGBType> img) {
		super(img);
	}

	abstract protected int getIndex();

	@Override
	public final double eval() {
		c.fwd();
		final int v = c.get().get();
		Color.RGBtoHSB((v >> 16) & 0xff, (v >> 8) & 0xff, v & 0xff, hsb);
		return hsb[getIndex()];
	}
}