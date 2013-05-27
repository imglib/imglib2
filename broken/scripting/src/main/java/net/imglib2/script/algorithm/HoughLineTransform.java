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

package net.imglib2.script.algorithm;

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * TODO
 *
 */
public class HoughLineTransform<T extends RealType<T>> extends ImgProxy<LongType>
{
	/** A {@link net.imglib2.algorithm.transformation.HoughLineTransform} with a LongType vote space.*/
	public HoughLineTransform(final Img<T> img) throws Exception {
		super(process(img));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HoughLineTransform(final IFunction fn) throws Exception {
		this((Img)Compute.inDoubles(fn));
	}

	static private final <S extends RealType<S>> Img<LongType> process(final Img<S> img) throws Exception {
		net.imglib2.algorithm.transformation.HoughLineTransform<LongType, S> h = 
			new net.imglib2.algorithm.transformation.HoughLineTransform<LongType, S>(img, new LongType());
		if (!h.checkInput() || !h.process()) {
			throw new Exception("HoughLineTransform: " + h.getErrorMessage());
		}
		return h.getResult();
	}
}
