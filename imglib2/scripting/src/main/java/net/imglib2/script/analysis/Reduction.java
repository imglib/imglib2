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

package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.analysis.fn.NumericResult;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class Reduction extends NumericResult<Double>
{
	private static final long serialVersionUID = 1435418604719008040L;

	@SuppressWarnings("boxing")
	public Reduction(final IterableRealInterval<? extends RealType<?>> img,  final ReduceFn fn) {
		super(reduce(img, fn));
	}

	static public final double reduce(final IterableRealInterval<? extends RealType<?>> img,  final ReduceFn fn) {
		final RealCursor<? extends RealType<?>> c = img.cursor();
		Double initial = fn.initial();
		double r;
		if (null == initial) {
			c.fwd();
			r = c.get().getRealDouble();
		} else {
			r = initial.doubleValue();
		}
		while (c.hasNext()) {
			c.fwd();
			r = fn.reduce(r, c.get().getRealDouble());
		}
		return fn.end(r);
	}
}
