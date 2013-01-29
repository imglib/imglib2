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

package net.imglib2.script.region.fn;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RealCursor;
import net.imglib2.algorithm.fft.LocalNeighborhoodCursor;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.script.math.fn.FloatImageOperation;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public abstract class ARegionFn<T extends RealType<T>> extends FloatImageOperation
{
	/** The input. */
	protected final Img<T> img;
	/** **/
	protected final RandomAccess<T> ra;
	/** **/
	protected final long span;
	/** The reference cursor. */
	protected final Cursor<T> ref;

	protected final LocalNeighborhoodCursor<T> lnc;
	protected final long[] position;

	/**
	 * 
	 * @param img The original {@link Img} to iterate over.
	 * @param ra A {@link RandomAccess} that iterates {@param img} with an {@link OutOfBounds} strategy.
	 * @param span The amount of values to consider before and after the current value, in each dimension, and that is used to create the {@link LocalNeighborhoodCursor}.
	 */
	public ARegionFn(
			final Img<T> img,
			final RandomAccess<T> ra,
			final long span) {
		this.img = img;
		this.ra = ra;
		this.span = span;
		this.ref = img.cursor();
		this.lnc = new LocalNeighborhoodCursor<T>(ra, span);
		this.position = new long[img.numDimensions()];
	}
	
	/** Operation to apply to the first value. */
	protected double fn0(double a) {
		return a;
	}
	
	/** Operation to apply to the result of reducing all values with {@link #fnR(double, double)}. */
	protected double fnE(double r) {
		return r;
	}

	/** Operation to reduce all values, where {@param r} is the accumulated result so far,
	 * and {@param a} is the next value. */
	protected abstract double fnR(double r, double a);

	@Override
	public final double eval() {
		// Advance cursor
		this.ref.fwd();
		// Position cursors
		this.ref.localize(position);
		this.lnc.reset(position);
		// Iterate neighborhood
		double r1 = this.lnc.next().getRealDouble();
		while (this.lnc.hasNext()) {
			r1 = fnR(r1, this.lnc.next().getRealDouble());
		}
		return fnE(r1);
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(this.ref);
	}

	@Override
	public void findImgs(final Collection<IterableRealInterval<?>> iris)
	{
		iris.add(this.img);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ARegionFn<T> duplicate() throws Exception {
		return getClass().getConstructor(Img.class, RandomAccess.class, Long.TYPE).newInstance(img, ra, span);
	}
}
