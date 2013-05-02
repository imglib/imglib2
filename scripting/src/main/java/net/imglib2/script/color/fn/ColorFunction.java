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

package net.imglib2.script.color.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.Img;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageComputation;
import net.imglib2.script.math.fn.NumberFunction;
import net.imglib2.script.math.fn.Util;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public abstract class ColorFunction implements IFunction, ImageComputation<ARGBType> {

	protected static final NumberFunction empty = new NumberFunction(0.0d);

	public static final class Channel implements IFunction {
		final RealCursor<? extends RealType<?>> c;
		final IterableRealInterval<? extends RealType<?>> img;
		final int shift;

		/** In RGBALegacyType, A=4, R=3, G=2, B=1, or H=3, S=2, B=1 */
		public Channel(final IterableRealInterval<? extends RealType<?>> img, final int channel) {
			this.img = img;
			this.c = img.cursor();
			this.shift = (channel-1) * 8;
		}

		@Override
		public final IFunction duplicate() throws Exception {
			return new Channel(img, (shift / 8) + 1);
		}

		@Override
		public final double eval() {
			c.fwd();
			return (((int)c.get().getRealDouble()) >> shift) & 0xff;
		}

		@Override
		public final void findCursors(final Collection<RealCursor<?>> cursors) {
			cursors.add(c);
		}
		@Override
		public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
			iris.add(img);
		}
	}

	static protected IFunction wrap(final Object ob) throws Exception {
		if (null == ob) return empty;
		return Util.wrap(ob);
	}

	@Override
	public Img<ARGBType> asImage() throws Exception {
		return asImage(Runtime.getRuntime().availableProcessors());
	}

	@Override
	public Img<ARGBType> asImage(final int numThreads) throws Exception {
		return Compute.apply(this, new ARGBType(), numThreads);
	}
}
