/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
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

package net.imglib2.script.algorithm;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** A bandpass filter. */
/**
 * TODO
 *
 */
public class BandpassFilter<T extends RealType<T>> extends ImgProxy<T>
{
	public BandpassFilter(final Img<T> img, final int beginRadius, final int endRadius) throws Exception {
		super(process(img, beginRadius, endRadius));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BandpassFilter(final IFunction fn, final int beginRadius, final int endRadius) throws Exception {
		this((Img)Compute.inDoubles(fn), beginRadius, endRadius);
	}

	static private final <T extends RealType<T>> Img<T> process(final Img<T> img, final int beginRadius, final int endRadius) throws Exception {
		net.imglib2.algorithm.fft.Bandpass<T> bp = new net.imglib2.algorithm.fft.Bandpass<T>(img, beginRadius, endRadius);
		if (!bp.checkInput() || !bp.process()) {
			throw new Exception(bp.getClass().getSimpleName() + " failed: " + bp.getErrorMessage());
		}
		return bp.getResult();
	}
}
