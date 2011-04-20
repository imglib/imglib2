package net.imglib2.script.color;

import net.imglib2.script.color.fn.ChannelOp;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the pixel value for the desired channel, from 1 to 4,
 *  where RGBA is really ARGB and thus A=4, R=3, G=2, B=1. */
public class Channel extends ChannelOp {

	private final int channel;
	private final int shift;

	public Channel(final Img<? extends ARGBType> img, final int channel) throws IllegalArgumentException {
		super(img);
		if (channel > 4 || channel < 1) throw new IllegalArgumentException("Channel must be 1 <= channel <= 4"); 
		this.channel = channel;
		this.shift = (channel-1) * 8;
	}

	@Override
	protected final int getShift() { return shift; }

	public IFunction duplicate() {
		return new Channel(c.getImg(), channel);
	}
}
