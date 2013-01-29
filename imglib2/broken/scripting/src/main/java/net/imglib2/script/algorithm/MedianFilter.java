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

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class MedianFilter<T extends RealType<T>> extends ImgProxy<T>
{
	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public MedianFilter(final Img<T> img, final Number radius) throws Exception {
		this(img, radius, new OutOfBoundsMirrorFactory<T,Img<T>>(OutOfBoundsMirrorFactory.Boundary.SINGLE));
	}

	public MedianFilter(final Img<T> img, final Number radius, final OutOfBoundsFactory<T,Img<T>> oobs) throws Exception {
		super(process(img, radius, oobs));
	}

	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MedianFilter(final IFunction fn, final Number radius) throws Exception {
		this((Img)Compute.inDoubles(fn), radius, new OutOfBoundsMirrorFactory<T,Img<T>>(OutOfBoundsMirrorFactory.Boundary.SINGLE));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MedianFilter(final IFunction fn, final Number radius, final OutOfBoundsFactory<T,Img<T>> oobs) throws Exception {
		this((Img)Compute.inDoubles(fn), radius, oobs);
	}

	static private final <S extends RealType<S>> Img<S> process(final Img<S> img, final Number radius, final OutOfBoundsFactory<S,Img<S>> oobs) throws Exception {
		long[] sides = new long[img.numDimensions()];
		Arrays.fill(sides, radius.longValue());
		final net.imglib2.algorithm.roi.MedianFilter<S> mf = new net.imglib2.algorithm.roi.MedianFilter<S>(img, sides, oobs);
		if (!mf.process()) {
			throw new Exception("MedianFilter: " + mf.getErrorMessage());
		}
		return mf.getResult();
	}
}
