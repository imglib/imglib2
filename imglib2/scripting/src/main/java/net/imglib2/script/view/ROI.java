/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.script.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.Views;

/**
 * ROI is a view of the image, either smaller or larger.
 * If larger, the image should be an extension of an image that can provide data for the outside domain,
 * for example {@code new ROI(new ExtendMirrorDouble(...), ...)}.
 * 
 * @param <R>
 * @author Albert Cardona
 */
public class ROI<R extends NumericType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	public ROI(final RandomAccessibleInterval<R> img, final long[] offset, final long[] dimensions) {
		super(Views.zeroMin(Views.interval(img, offset, asMax(offset, dimensions))));
	}

	public ROI(final RandomAccessibleIntervalImgProxy<R> proxy, final long[] offset, final long[] dimensions) {
		this(proxy.getRandomAccessibleInterval(), offset, dimensions);
	}

	private static final long[] asMax(final long[] offset, final long[] dimensions) {
		final long[] max = new long[offset.length];
		for (int i=0; i<offset.length; ++i)
			max[i] = offset[i] + dimensions[i] - 1;
		return max;
	}
}
