package script.imglib.color;

import script.imglib.color.fn.ChannelOp;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.ARGBType;

/** Extracts the red pixel value. */
public class Red extends ChannelOp {

	/** Extract the red channel of each pixel, in the range [0, 255]. */
	public Red(final Img<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 16; }
}