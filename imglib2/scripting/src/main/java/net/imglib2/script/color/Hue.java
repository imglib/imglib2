package net.imglib2.script.color;

import net.imglib2.script.color.fn.HSBOp;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Hue extends HSBOp {

	/** Extract the hue component of each pixel, in the range [0, 1]. */
	public Hue(final Img<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getIndex() { return 0; }
}
