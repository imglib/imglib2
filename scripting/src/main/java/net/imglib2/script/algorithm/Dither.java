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

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.floydsteinberg.FloydSteinbergDithering;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

/** Perform {@link FloydSteinbergDithering} on an image. */
/**
 * TODO
 *
 */
public class Dither<T extends RealType<T>> extends ImgProxy<BitType>
{
	/** The dithering threshold is computed from the min and max values of the image;
	 *  see {@link FloydSteinbergDithering}. */
	public Dither(final Img<T> img) throws Exception {
		super(process(img));
	}

	public Dither(final Img<T> img, final float ditheringThreshold) throws Exception {
		super(process(img, ditheringThreshold));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Dither(final IFunction fn) throws Exception {
		this((Img)Compute.inDoubles(fn));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Dither(final IFunction fn, final float ditheringThreshold) throws Exception {
		this((Img)Compute.inDoubles(fn), ditheringThreshold);
	}

	static private final <R extends RealType<R>> Img<BitType> process(final Img<R> img, final float ditheringThreshold) throws Exception {
		return process(new FloydSteinbergDithering<R>(img, ditheringThreshold));
	}
	static private final <R extends RealType<R>> Img<BitType> process(final Img<R> img) throws Exception {
		return process(new FloydSteinbergDithering<R>(img));
	}
	static private final <R extends RealType<R>> Img<R> process(final OutputAlgorithm<Img<R>> oa) throws Exception {
		if (!oa.checkInput() || !oa.process()) {
			throw new Exception("Dither failed: " + oa.getErrorMessage());
		}
		return oa.getResult();
	}
}
