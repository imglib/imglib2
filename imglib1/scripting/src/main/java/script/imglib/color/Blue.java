package script.imglib.color;

import script.imglib.color.fn.ChannelOp;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the blue pixel value. */
public class Blue extends ChannelOp {

	/** Extract the blue channel of each pixel, in the range [0, 255]. */
	public Blue(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 0; }
}
