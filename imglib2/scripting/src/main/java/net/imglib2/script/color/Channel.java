/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.color;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.color.fn.ChannelOp;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.ARGBType;

/**
 * Extracts the pixel value for the desired channel, from 1 to 4,
 * where RGBA is really ARGB and thus A=4, R=3, G=2, B=1.
 */
public class Channel extends ChannelOp {

	private final int channel;
	private final int shift;

	public Channel(final IterableRealInterval<? extends ARGBType> img, final int channel) throws IllegalArgumentException {
		super(img);
		if (channel > 4 || channel < 1) throw new IllegalArgumentException("Channel must be 1 <= channel <= 4"); 
		this.channel = channel;
		this.shift = (channel-1) * 8;
	}

	@Override
	protected final int getShift() { return shift; }

	@Override
	public IFunction duplicate() {
		return new Channel(img, channel);
	}
}
