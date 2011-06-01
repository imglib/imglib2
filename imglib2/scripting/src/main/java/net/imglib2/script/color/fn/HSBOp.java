package net.imglib2.script.color.fn;

import java.awt.Color;

import net.imglib2.IterableRealInterval;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the HSB saturation of an RGB pixel. */
public abstract class HSBOp extends RGBAOp {

	private final float[] hsb = new float[3];

	public HSBOp(final IterableRealInterval<? extends ARGBType> img) {
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
