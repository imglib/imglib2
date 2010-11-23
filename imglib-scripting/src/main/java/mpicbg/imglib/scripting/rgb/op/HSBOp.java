package mpicbg.imglib.scripting.rgb.op;

import java.awt.Color;
import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the HSB saturation of an RGB pixel. */
public abstract class HSBOp<R extends RealType<R> > implements Operation<R> {

	private final Cursor<? extends RGBALegacyType> c;
	private final float[] hsb = new float[3];

	public HSBOp(final Image<? extends RGBALegacyType> img) {
		this.c = img.createCursor();
	}
	
	abstract protected int getChannel();

	@Override
	public final void compute(final R output) {
		final int v = c.getType().get();
		Color.RGBtoHSB((v >> 16) & 0xff, (v >> 8) & 0xff, v & 0xff, hsb);
		output.setReal(hsb[getChannel()]);
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