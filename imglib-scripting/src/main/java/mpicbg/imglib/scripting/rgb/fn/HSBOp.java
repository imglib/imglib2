package mpicbg.imglib.scripting.rgb.fn;

import java.awt.Color;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the HSB saturation of an RGB pixel. */
public abstract class HSBOp extends RGBAOp {

	private final float[] hsb = new float[3];

	public HSBOp(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	abstract protected int getIndex();

	@Override
	public final double eval() {
		c.fwd();
		final int v = c.getType().get();
		Color.RGBtoHSB((v >> 16) & 0xff, (v >> 8) & 0xff, v & 0xff, hsb);
		return hsb[getIndex()];
	}
}