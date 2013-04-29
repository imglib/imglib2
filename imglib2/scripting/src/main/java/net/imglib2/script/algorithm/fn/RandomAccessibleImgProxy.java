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

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class RandomAccessibleImgProxy<T extends NumericType<T>, RAI extends RandomAccessible<T>> implements Img<T> {

	protected final RAI rai;
	protected final long[] dims;

	/** Wrap the {@param ra} in this {@link Img},
	 * which is then able to iterate over the domain from 0 to {@param dims}.
	 * 
	 * @param ra
	 * @param dims
	 */
	public RandomAccessibleImgProxy(final RAI rai, final long[] dims) {
		this.rai = rai;
		this.dims = dims;
	}
	
	public RAI getRandomAccessible() {
		return this.rai;
	}
	
	@Override
	public RandomAccess<T> randomAccess() {
		return rai.randomAccess();
	}

	@Override
	public RandomAccess<T> randomAccess(Interval interval) {
		return rai.randomAccess(interval);
	}

	@Override
	public int numDimensions() {
		return dims.length;
	}

	@Override
	public long min(int d) {
		return 0;
	}

	@Override
	public void min(long[] min) {
		for (int i=0; i<min.length; ++i) {
			min[i] = 0;
		}
	}

	@Override
	public void min(Positionable min) {
		for (int i=0; i<dims.length; ++i) {
			min.setPosition(0, i);
		}
	}

	@Override
	public long max(int d) {
		return dims[d] -1;
	}

	@Override
	public void max(long[] max) {
		for (int i=0; i<dims.length; ++i) {
			max[i] = dims[i] -1;
		}
	}

	@Override
	public void max(Positionable max) {
		for (int i=0; i<dims.length; ++i) {
			max.setPosition(dims[i] -1, i);
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		for (int i=0; i<dims.length; ++i) {
			dimensions[i] = dims[i];
		}
	}

	@Override
	public long dimension(int d) {
		return dims[d];
	}

	@Override
	public double realMin(int d) {
		return 0;
	}

	@Override
	public void realMin(double[] min) {
		for (int i=0; i<min.length; ++i) {
			min[i] = 0;
		}
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int i=0; i<dims.length; ++i) {
			min.setPosition(0, i);
		}
	}

	@Override
	public double realMax(int d) {
		return dims[d] -1;
	}

	@Override
	public void realMax(double[] max) {
		for (int i=0; i<max.length; ++i) {
			max[i] = dims[i] -1;
		}
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int i=0; i<dims.length; ++i) {
			max.setPosition(dims[i] -1, i);
		}
	}

	@Override
	public Cursor<T> cursor() {
		return new RandomAccessibleIntervalCursor<T>(Views.interval(rai, new long[dims.length], dims));
	}

	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		if (0 == dims.length) return 0;
		long size = 1;
		for (int i=0; i<dims.length; ++i) {
			size *= dims[i];
		}
		return size;
	}

	@Override
	public T firstElement() {
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return new FlatIterationOrder( this );
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator<T> iterator() {
		return cursor();
	}

	@Override
	public ImgFactory<T> factory() {
		return null;
	}

	@Override
	public RandomAccessibleImgProxy<T,RAI> copy() {
		return new RandomAccessibleImgProxy<T,RAI>(rai, dims.clone());
	}

}
