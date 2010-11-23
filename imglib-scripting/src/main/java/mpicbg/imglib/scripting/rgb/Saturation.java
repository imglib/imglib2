package mpicbg.imglib.scripting.rgb;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb.op.HSBOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Saturation<R extends RealType<R> > extends HSBOp<R> {

	public Saturation(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	protected final int getIndex() { return 1; }
}