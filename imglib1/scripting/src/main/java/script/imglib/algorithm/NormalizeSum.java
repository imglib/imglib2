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

package script.imglib.algorithm;

import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;

/**
 * TODO
 *
 */
public class NormalizeSum<T extends RealType<T>> extends Image<FloatType>
{
	public NormalizeSum(final Image<T> img) throws Exception {
		super(process(img).getContainer(), new FloatType());
	}

	@SuppressWarnings("unchecked")
	public NormalizeSum(final IFunction fn) throws Exception {
		this((Image)Compute.inFloats(fn));
	}

	static private final <R extends RealType<R>> Image<FloatType> process(final Image<R> img) throws Exception {
		NormalizeImageFloat<R> nir = new NormalizeImageFloat<R>(img);
		if (!nir.checkInput() || !nir.process()) {
			throw new Exception("Normalize: " + nir.getErrorMessage());
		}
		return nir.getResult();
	}
}
