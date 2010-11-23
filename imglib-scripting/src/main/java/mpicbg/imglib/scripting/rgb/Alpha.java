package mpicbg.imglib.scripting.rgb;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb.op.ChannelOp;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the alpha pixel value. */
public class Alpha<R extends RealType<R> > extends ChannelOp<R> {

	public Alpha(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 24; }
}