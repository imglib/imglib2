/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
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

package script.imglib.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;
import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;

/** AND two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImageJ does
 *  and is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  images when possible. */
/**
 * TODO
 *
 */
public class And extends BinaryOperation
{
	public And(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public And(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public And(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public And(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public And(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public And(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public And(final IFunction left, final Number val) {
		super(left, val);
	}

	public And(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public And(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public And(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return ((int)a().eval()) & ((int)b().eval());
	}
}
