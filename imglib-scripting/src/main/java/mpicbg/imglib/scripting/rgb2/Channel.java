package mpicbg.imglib.scripting.rgb2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb2.fn.ChannelOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the pixel value for the desired channel, from 1 to 4,
 *  where RGBA is really ARGB and thus A=4, R=3, G=2, B=1. */
public class Channel extends ChannelOp {

	private final int shift;

	public Channel(final Image<? extends RGBALegacyType> img, final int channel) throws IllegalArgumentException {
		super(img);
		if (channel > 4 || channel < 1) throw new IllegalArgumentException("Channel must be 1 <= channel <= 4"); 
		this.shift = (channel-1) * 8;
	}

	@Override
	protected final int getShift() { return shift; }
}