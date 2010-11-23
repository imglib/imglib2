package mpicbg.imglib.scripting.rgb;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb.op.RGBAOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

/** Computes the luminance of each RGB value using the weights
 *  r: 0.299, g: 0.587, b: 0.144 */
public class Luminance<R extends RealType<R> > extends RGBAOp<R> {

	public Luminance(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	public final void compute(final R output) {
		final int v = c.getType().get();
		output.setReal(((v >> 16) & 0xff) * 0.299 + ((v >> 8) & 0xff) * 0.587 + (v & 0xff) * 0.144);
	}
}