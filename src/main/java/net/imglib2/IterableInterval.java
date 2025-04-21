/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */

package net.imglib2;

import java.util.Spliterator;
import net.imglib2.stream.CursorSpliterator;
import net.imglib2.stream.LocalizableSpliterator;

/**
 * An {@link IterableRealInterval} whose elements are located at integer
 * coordinates.
 * <p>
 * An {@code IterableInterval} is <em>not</em> guaranteed to iterate over all
 * coordinates of its containing {@link Interval}. In the typical case of a
 * hyperrectangular image, it will do so; however, there are some
 * {@code IterableInterval}s which visit only a subset of the {@code Interval}
 * coordinates. For example, the {@code imglib2-roi} library provides means to
 * model regions of interest (ROIs), along with the ability to iterate over
 * coordinates within a particular ROI; see e.g.
 * {@code net.imglib2.roi.labeling.LabelRegion}.
 * </p>
 * 
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public interface IterableInterval< T > extends IterableRealInterval< T >, Interval
{
	@Override
	Cursor< T > cursor();

	@Override
	Cursor< T > localizingCursor();

	/**
	 * Creates a {@link LocalizableSpliterator} over the elements of this {@code
	 * IterableInterval}. The returned {@code Spliterator} iterates with optimal
	 * speed without calculating the location at each iteration step.
	 * Localization is performed on demand.
	 * <p>
	 * The default implementation wraps a {@code Cursor} on the interval. The
	 * created {@code Spliterator} reports {@link Spliterator#SIZED}, {@link
	 * Spliterator#SUBSIZED},  {@link Spliterator#ORDERED} and {@link
	 * Spliterator#NONNULL}.
	 */
	@Override
	default LocalizableSpliterator< T > spliterator()
	{
		return new CursorSpliterator<>( cursor(),0, size(), Spliterator.ORDERED | Spliterator.NONNULL );
	}

	/**
	 * Creates a {@link LocalizableSpliterator} over the elements of this {@code
	 * IterableInterval}. The returned {@code Spliterator} calculates its
	 * location at each iteration step. That is, localization is performed with
	 * optimal speed.
	 * <p>
	 * The default implementation wraps a {@link #localizingCursor() localizing}
	 * {@code Cursor} on the interval. The created {@code Spliterator} reports
	 * {@link Spliterator#SIZED}, {@link Spliterator#SUBSIZED},  {@link
	 * Spliterator#ORDERED} and {@link Spliterator#NONNULL}.
	 */
	@Override
	default LocalizableSpliterator< T > localizingSpliterator()
	{
		return new CursorSpliterator<>( localizingCursor(), 0, size(), Spliterator.ORDERED | Spliterator.NONNULL );
	}
}
