package script.imglib.color;

import script.imglib.color.fn.ChannelOp;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the red pixel value. */
public class Red extends ChannelOp {

	/** Extract the red channel of each pixel, in the range [0, 255]. */
	public Red(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 16; }
}