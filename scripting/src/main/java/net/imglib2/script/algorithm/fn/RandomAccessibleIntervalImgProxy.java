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

package net.imglib2.script.algorithm.fn;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;

/**
 * TODO
 *
 */
public class RandomAccessibleIntervalImgProxy<T extends NumericType<T>> extends RandomAccessibleImgProxy<T,RandomAccessibleInterval<T>>
{
	protected final IterableRandomAccessibleInterval<T> irai;
	
	public RandomAccessibleIntervalImgProxy(final RandomAccessibleInterval<T> rai) {
		super(rai, Util.intervalDimensions(rai));
		this.irai = new IterableRandomAccessibleInterval<T>(rai); // iterate in flat order like ArrayImg
	}

	public RandomAccessibleInterval<T> getRandomAccessibleInterval() {
		return this.rai;
	}

	@Override
	public Cursor<T> cursor() {
		return irai.cursor();
	}
	
	@Override
	public T firstElement() {
		return irai.firstElement();
	}
	
	@Override
	public RandomAccessibleIntervalImgProxy<T> copy() {
		return new RandomAccessibleIntervalImgProxy<T>(rai);
	}
}
