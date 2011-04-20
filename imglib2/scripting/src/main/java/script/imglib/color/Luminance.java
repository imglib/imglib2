package script.imglib.color;

import script.imglib.color.fn.RGBAOp;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.ARGBType;

/** Computes the luminance of each RGB value using the weights
 *  r: 0.299, g: 0.587, b: 0.144 */
public class Luminance extends RGBAOp {

	public Luminance(final Img<? extends ARGBType> img) {
		super(img);
	}

	@Override
	public final double eval () {
		c.fwd();
		final int v = c.get().get();
		return ((v >> 16) & 0xff) * 0.299 + ((v >> 8) & 0xff) * 0.587 + (v & 0xff) * 0.144;
	}
}
