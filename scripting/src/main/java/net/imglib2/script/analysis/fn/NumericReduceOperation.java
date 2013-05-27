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

package net.imglib2.script.analysis.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.ReduceFn;
import net.imglib2.script.analysis.Reduction;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Call {@link #run()} to execute.
 * 
 *
 * @author Albert Cardona
 */
public abstract class NumericReduceOperation extends NumericResult<Double> implements ReduceFn
{
	private static final long serialVersionUID = 1L;
	protected final long imgSize;
	protected final IterableRealInterval<? extends RealType<?>> img;

	public NumericReduceOperation(final IFunction fn) throws Exception {
		this(Compute.inFloats(fn));
	}

	public NumericReduceOperation(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super();
		this.img = img;
		this.imgSize = img.size();
	}

	@SuppressWarnings("boxing")
	protected void invoke() {
		super.d = Reduction.reduce(img, this);
	}

	// Reasonable default
	@Override
	public Double initial() {
		return null; // use the first value
	}

	// Reasonable default
	@Override
	public double end(final double r) {
		return r;
	}
}
