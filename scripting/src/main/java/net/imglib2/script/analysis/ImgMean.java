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
import net.imglib2.script.analysis.fn.NumericReduceOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/**
 * Add all values and when done divide by the total number of values.
 * 
 * @author Albert Cardona
 * @see ImgIterativeMean
 * @see Reduction
 * @see ReduceFn
 */
public final class ImgMean extends NumericReduceOperation
{
	private static final long serialVersionUID = 1L;

	public ImgMean(final IFunction fn) throws Exception {
		super(fn);
		invoke();
	}
	
	public ImgMean(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
		invoke();
	}

	@Override
	public final double reduce(final double r, final double v) {
		return r + v;
	}
	
	@Override
	public final double end(final double r) {
		return r / imgSize;
	}
}
