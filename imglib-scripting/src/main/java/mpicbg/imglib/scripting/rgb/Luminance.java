package mpicbg.imglib.scripting.rgb;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Computes the luminance of each RGB value using the weights
 *  r: 0.299, g: 0.587, b: 0.144 */
public class Luminance<R extends RealType<R> > implements Operation<R> {

	private final Cursor<? extends RGBALegacyType> c;

	public Luminance(final Image<? extends RGBALegacyType> img) {
		this.c = img.createCursor();
	}

	@Override
	public final void compute(final R output) {
		final int v = c.getType().get();
		output.setReal(((v >> 16) & 0xff) * 0.299 + ((v >> 8) & 0xff) * 0.587 + (v & 0xff) * 0.144);
	}

	@Override
	public final void fwd() {
		c.fwd();
	}

	@Override
	public final void getImages(final Set<Image<?>> images) {
		images.add(c.getImage());
	}

	@Override
	public final void init(final R ref) {}

	@Override
	public final void compute(final RealType<?> ignored1, final RealType<?> ignored2, final R output) {
		// Won't be used, but just in case
		compute(output);
	}
}
