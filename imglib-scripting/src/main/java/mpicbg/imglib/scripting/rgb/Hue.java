package mpicbg.imglib.scripting.rgb;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb.op.HSBOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Hue<R extends RealType<R> > extends HSBOp<R> {

	public Hue(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getIndex() { return 0; }
}