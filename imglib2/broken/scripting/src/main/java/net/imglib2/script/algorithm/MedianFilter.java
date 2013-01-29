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
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
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
