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

package net.imglib2.script.edit;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.IterableIntervalProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

/**
 * Copy a source image into a target image, with a positive or negative offset;
 * then this instance becomes a proxy to the target image.
 * 
 * The target image is altered in place, and this instance becomes a proxy to it.
 * 
 * @param <T>
 * @param <RI>
 * @param <Y>
 *
 * @author Albert Cardona
 */
public class Insert<T extends RealType<T>, RI extends IterableInterval<T> & RandomAccessible<T>, Y extends RealType<Y>> extends IterableIntervalProxy<Y>
{
	/** 
	 * @param source The image to copy from.
	 * @param offset The offset in the image to copy from.
	 * @param target The image to copy into.
	 */
	public Insert(final RI source, final IterableInterval<Y> target, final long[] offset) {
		super(new Paste<T,RI,Y>(source, target, offset).paste());
	}

	private static final class Paste<T extends RealType<T>, RI extends IterableInterval<T> & RandomAccessible<T>, Y extends RealType<Y>>
	{
		private final Cursor<Y> tc;
		private final long[] min, max;
		private final Copier copier;
		final IterableInterval<Y> target;
		
		private Paste(final RI source, final IterableInterval<Y> target, final long[] offset) {
			this.target = target;
			this.tc = target.cursor();
			this.min = new long[source.numDimensions()];
			this.max = new long[source.numDimensions()];
			for (int i=0; i<this.min.length; ++i) {
				this.min[i] = (long) Math.min(offset[i], target.realMax(i));
				this.max[i] = (long) Math.min(offset[i] + source.realMax(i), target.realMax(i));
			}
			RandomAccessibleInterval<T> view = Views.interval(source, min, max);
			this.copier = source.equalIterationOrder(target) ? new CompatibleCopier(view) : new RandomAccessCopier(view);
		}
		
		private final IterableInterval<Y> paste() {
			// Do the paste
			copier.eval();
			
			return target;
		}

		static private final boolean inside(final long[] pos, final long[] min, final long[] max) {
			for (int i=0; i<pos.length; ++i) {
				if (pos[i] < min[i] || pos[i] > max[i]) return false;
			}
			return true;
		}

		private class Copier
		{
			protected void eval() {}
		}

		private final class CompatibleCopier extends Copier
		{
			private final RandomAccessibleIntervalCursor<T> sc;
			private final long[] position;

			CompatibleCopier(final RandomAccessibleInterval<T> view) {
				this.sc = new RandomAccessibleIntervalCursor<T>(view);
				this.position = new long[view.numDimensions()];
			}
			@Override
			protected final void eval() {
				// TODO determine intersection bounds and iterate just that
				while (tc.hasNext()) {
					sc.fwd();
					tc.fwd();
					tc.localize(position);
					if (inside(position, min, max)) {
						tc.get().setReal(sc.get().getRealDouble());
					}
				}
			}
		}
		private final class RandomAccessCopier extends Copier
		{
			private final RandomAccess<T> sc;
			private final long[] position;

			RandomAccessCopier(final RandomAccessibleInterval<T> view) {
				this.sc = view.randomAccess();
				this.position = new long[view.numDimensions()];
			}
			@Override
			protected final void eval() {
				// TODO determine intersection bounds and iterate just that
				while (tc.hasNext()) {
					tc.fwd();
					tc.localize(position);
					if (inside(position, min, max)) {
						for (int i=0; i<position.length; ++i) {
							position[i] -= min[i];
						}
						sc.setPosition(position);
						tc.get().setReal(sc.get().getRealDouble());
					}
				}
			}
		}
	}
}
