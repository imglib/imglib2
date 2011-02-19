package script.imglib.color;

import script.imglib.color.fn.ChannelOp;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.ARGBType;

/** Extracts the alpha pixel value. */
public class Alpha extends ChannelOp {

	/** Extract the alpha channel of each pixel, in the range [0, 255]. */
	public Alpha(final Img<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 24; }
}