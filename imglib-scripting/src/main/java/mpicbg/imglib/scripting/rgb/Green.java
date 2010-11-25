package mpicbg.imglib.scripting.rgb;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb.fn.ChannelOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the green pixel value. */
public class Green extends ChannelOp {

	public Green(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 8; }
}