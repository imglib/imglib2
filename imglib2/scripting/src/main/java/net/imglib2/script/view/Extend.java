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
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class Extend<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	/** Infinitely extend the domain of the image with {@param value}. */
	public Extend(final RandomAccessibleInterval<T> img, final Number value) {
		super(Views.interval(Views.extendValue(img, AlgorithmUtil.type(img, value.doubleValue())), img));
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img) {
		this(img, 0);
	}

	/** Infinitely extend the domain of the image with {@param value}. */
	public Extend(final RandomAccessibleIntervalImgProxy<T> proxy, final Number value) {
		this(proxy.getRandomAccessibleInterval(), value);
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleIntervalImgProxy<T> proxy) {
		this(proxy.getRandomAccessibleInterval(), 0);
	}
}
