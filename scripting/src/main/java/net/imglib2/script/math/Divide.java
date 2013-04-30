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

package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class Divide extends BinaryOperation
{
	public <S extends RealType<S>, R extends RealType<R>> Divide(final IterableRealInterval<S> left, final IterableRealInterval<R> right) {
		super(left, right);
	}

	public <R extends RealType<R>> Divide(final IFunction fn, final IterableRealInterval<R> right) {
		super(fn, right);
	}

	public <R extends RealType<R>> Divide(final IterableRealInterval<R> left, final IFunction fn) {
		super(left, fn);
	}

	public Divide(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public <R extends RealType<R>> Divide(final IterableRealInterval<R> left, final Number val) {
		super(left, val);
	}

	public <R extends RealType<R>> Divide(final Number val,final IterableRealInterval<R> right) {
		super(val, right);
	}

	public Divide(final IFunction left, final Number val) {
		super(left, val);
	}

	public Divide(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Divide(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Divide(final Object... elems) throws Exception {
		super(elems);
	}

	/** 1 / img */
	@SuppressWarnings("boxing")
	public <R extends RealType<R>> Divide(final IterableRealInterval<R> right) {
		super(1, right);
	}

	/** 1 / val */
	@SuppressWarnings("boxing")
	public Divide(final Number val) {
		super(1, val);
	}

	/** 1 / fn.eval() */
	@SuppressWarnings("boxing")
	public Divide(final IFunction fn) {
		super(1, fn);
	}

	@Override
	public final double eval() {
		return a().eval() / b().eval();
	}
}
