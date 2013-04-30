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
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.NumericType;

/**
 * TODO
 *
 */
public class ImgProxy<T extends NumericType<T>> implements Img<T> {

	private final Img<T> img;
	
	public ImgProxy(final Img<T> img) {
		this.img = img;
	}
	
	/** Return the {@link Img} wrapped by this proxy. */
	public Img<T> image() {
		return img;
	}
	
	@Override
	public int numDimensions() {
		return img.numDimensions();
	}

	@Override
	public long size() {
		return img.size();
	}

	@Override
	public T firstElement() {
		return img.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return img.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin(int d) {
		return img.realMin(d);
	}

	@Override
	public void realMin(double[] min) {
		img.realMin(min);
	}

	@Override
	public double realMax(int d) {
		return img.realMax(d);
	}

	@Override
	public void realMax(double[] max) {
		img.realMax(max);
	}

	@Override
	public Iterator<T> iterator() {
		return img.iterator();
	}

	@Override
	public long min(int d) {
		return img.min(d);
	}

	@Override
	public void min(long[] min) {
		img.min(min);
	}

	@Override
	public long max(int d) {
		return img.max(d);
	}

	@Override
	public void max(long[] max) {
		img.max(max);
	}

	@Override
	public void dimensions(long[] dimensions) {
		img.dimensions(dimensions);
	}

	@Override
	public long dimension(int d) {
		return img.dimension(d);
	}

	@Override
	public Cursor<T> cursor() {
		return img.cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		return img.localizingCursor();
	}

	@Override
	public ImgFactory<T> factory() {
		return img.factory();
	}

	@Override
	public RandomAccess<T> randomAccess(Interval interval) {
		return img.randomAccess(interval);
	}

	@Override
	public RandomAccess<T> randomAccess() {
		return img.randomAccess();
	}

	@Override
	public Img<T> copy() {
		return img.copy();
	}

	@Override
	public void min(Positionable min) {
		img.min(min);
	}

	@Override
	public void max(Positionable max) {
		img.max(max);
	}

	@Override
	public void realMin(RealPositionable min) {
		img.realMin(min);
	}

	@Override
	public void realMax(RealPositionable max) {
		img.realMax(max);
	}
}
