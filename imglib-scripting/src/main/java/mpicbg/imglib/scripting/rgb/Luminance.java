package mpicbg.imglib.scripting.rgb;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb.fn.RGBAOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Computes the luminance of each RGB value using the weights
 *  r: 0.299, g: 0.587, b: 0.144 */
public class Luminance extends RGBAOp {

	public Luminance(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	public final double eval () {
		c.fwd();
		final int v = c.getType().get();
		return ((v >> 16) & 0xff) * 0.299 + ((v >> 8) & 0xff) * 0.587 + (v & 0xff) * 0.144;
	}
}