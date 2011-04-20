package script.imglib.color;

import script.imglib.color.fn.HSBOp;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Hue extends HSBOp {

	/** Extract the hue component of each pixel, in the range [0, 1]. */
	public Hue(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getIndex() { return 0; }
}