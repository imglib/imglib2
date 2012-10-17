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

package net.imglib2.script.filter;

import java.util.ArrayList;
import java.util.Collection;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.script.math.fn.FloatImageOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.script.math.fn.Util;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class Paste<T extends RealType<T>> extends FloatImageOperation
{
	private final T background;
	private final ImageFunction<T> a;
	private final IFunction b;


	public <R extends RealType<R>> Paste(
			final RandomAccessibleInterval<T> source,
			final IterableRealInterval<R> target,
			final long[] offset) throws Exception {
		this.background = AlgorithmUtil.type(source, 0);
		this.a = new ImageFunction<T>(new RandomAccessibleIntervalImgProxy<T>(
				Views.interval(
						Views.offset(Views.extendValue(source, background), offset),
						new FinalInterval(Util.intervalDimensions(target)))));
		this.b = new ImageFunction<R>(target);
	}


	/**
	 * @param <T> The {@link Type} of the source image.
	 * @param source The image to paste, which can be smaller than the {@param target} image.
	 * @param target The function that expresses the target image.
	 * @param offset The offset from origin for the pasting operation.
	 * @throws Exception
	 */
	public Paste(
			final RandomAccessibleInterval<T> source,
			final IFunction target,
			final long[] offset) throws Exception {
		this.background = AlgorithmUtil.type(source, 0);
		this.a = new ImageFunction<T>(new RandomAccessibleIntervalImgProxy<T>(
				Views.interval(
						Views.offset(Views.extendValue(source, background), offset),
						extractDimensions(target))));
		this.b = target;
	}

	private static final Interval extractDimensions(final IFunction target) {
		final ArrayList<IterableRealInterval<?>> iris = new ArrayList<IterableRealInterval<?>>();
		target.findImgs(iris);
		return new FinalInterval(
				iris.isEmpty() ?
						new long[]{1}
						: Util.intervalDimensions(iris.get(0)));
	}

	/** For cloning this {@link IFunction}. */
	protected Paste(final ImageFunction<T> a, final IFunction b, final T background) {
		this.background = background;
		this.a = a;
		this.b = b;
	}

	@Override
	public final double eval() {
		// Advance both
		final T in = a.next(); // the source to be pasted
		final double out = b.eval(); // the target that receives the paste
		// return the source if inside
		return background == in ? out : in.getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		a.findCursors(cursors);
		b.findCursors(cursors);
	}

	public final IFunction a() { return a; }
	public final IFunction b() { return b; }

	@Override
	public Paste<T> duplicate() throws Exception
	{
		return new Paste<T>(a.duplicate(), b.duplicate(), background);
	}

	@Override
	public void findImgs(final Collection<IterableRealInterval<?>> iris)
	{
		a.findImgs(iris);
		b.findImgs(iris);
	}
}
