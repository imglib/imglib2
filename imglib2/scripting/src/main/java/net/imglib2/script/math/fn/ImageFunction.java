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

package net.imglib2.script.math.fn;

import java.util.Collection;
import java.util.Iterator;

import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Img}
 *  at every call to {@link #eval()}.
 *  If the image given as argument to the constructors is not among those
 *  known to be flat-iterable, but it's a {@link RandomAccessibleInterval},
 *  then it will be wrapped accordingly to ensure flat iteration. */
/**
 * TODO
 *
 */
public final class ImageFunction<T extends RealType<T>> implements IFunction, Iterator<T>, Iterable<T> {

	private final IterableRealInterval<T> img;
	private final RealCursor<T> c;

	public ImageFunction(final IterableRealInterval<T> img) {
		this.img = Util.flatIterable(img);
		this.c = img.cursor();
	}

	public ImageFunction(final RandomAccessibleInterval<T> rai) {
		this.img = Util.flatIterable(rai);
		this.c = img.cursor();
	}

	public ImageFunction(final Img<T> img) {
		this.img = Util.flatIterable(img);
		this.c = img.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}
	
	/** Same as {@link #eval()} but returns the {@link RealType}. */
	@Override
	public final T next() {
		c.fwd();
		return c.get();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public ImageFunction<T> duplicate()
	{
		return new ImageFunction<T>(img);
	}

	@Override
	public void findImgs(Collection<IterableRealInterval<?>> iris) {
		iris.add(this.img);
	}

	@Override
	public final boolean hasNext() {
		return c.hasNext();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterator<T> iterator() {
		return this;
	}
}
