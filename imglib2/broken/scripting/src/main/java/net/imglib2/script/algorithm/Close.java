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

import java.util.Arrays;

import net.imglib2.algorithm.roi.MorphClose;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;

/** Operates on an {@link Image} or an {@link IFunction}. */
/**
 * TODO
 *
 */
public class Close<T extends RealType<T>> extends ImgProxy<T>
{
	@SuppressWarnings("unchecked")
	public Close(final Object fn) throws Exception {
		super(process(asImage(fn), 3));
	}

	@SuppressWarnings("unchecked")
	public Close(final Object fn, final Number side) throws Exception {
		super(process(asImage(fn), side.longValue()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static private final Img asImage(final Object fn) throws Exception {
		if (fn instanceof Img)
			return (Img) fn;
		return Compute.inFloats(AlgorithmUtil.wrap(fn));
	}

	static private final <R extends RealType<R>> Img<R> process(final Img<R> img, final long side) throws Exception {
		final long[] cell = new long[img.numDimensions()];
		Arrays.fill(cell, side);
		return process(img, cell);
	}

	static private final <R extends RealType<R>> Img<R> process(final Img<R> img, final long[] box) throws Exception {
		MorphClose<R> mc = new MorphClose<R>(img, box);
		if (!mc.checkInput() || !mc.process()) throw new Exception(mc.getErrorMessage());
		return mc.getResult();
	}
}
