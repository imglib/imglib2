/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.stream;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.LocalizableSampler;
import net.imglib2.RealLocalizableSampler;

/**
 * Utilities for creating "localizable Streams".
 */
public class Streams
{
	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element locations are computed on demand only, which is suited for usages
	 * where location is only needed for a few elements.
	 * (Also see {@link #localizing(IterableRealInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @param <T> pixel type
	 *
	 * @return a {@code Stream<RealLocalizableSampler<T>>} over the elements of
	 *  	the given interval.
	 */
	public static < T > Stream< RealLocalizableSampler< T > > localizable( IterableRealInterval< T > interval )
	{
		return StreamSupport.stream( new RealLocalizableSamplerWrapper<>( interval.spliterator() ), false );
	}

	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element location is tracked preemptively on every step, which is suited for usages
	 * where location is needed for all elements.
	 * (Also see {@link #localizable(IterableRealInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @param <T> pixel type
	 *
	 * @return a {@code Stream<RealLocalizableSampler<T>>} over the elements of
	 *  	the given interval.
	 */
	public static < T > Stream< RealLocalizableSampler< T > > localizing( IterableRealInterval< T > interval )
	{
		return StreamSupport.stream( new RealLocalizableSamplerWrapper<>( interval.localizingSpliterator() ), false );
	}

	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element locations are computed on demand only, which is suited for usages
	 * where location is only needed for a few elements.
	 * (Also see {@link #localizing(IterableInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @return a {@code Stream<LocalizableSampler<T>>} over the elements of the
	 *  	given interval.
	 *
	 * @param <T> pixel type
	 */
	public static < T > Stream< LocalizableSampler< T > > localizable( IterableInterval< T > interval )
	{
		return StreamSupport.stream( new LocalizableSamplerWrapper<>( interval.spliterator() ), false );
	}

	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element location is tracked preemptively on every step, which is suited for usages
	 * where location is needed for all elements.
	 * (Also see {@link #localizable(IterableInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @return a {@code Stream<LocalizableSampler<T>>} over the elements of the
	 *  	given interval.
	 *
	 * @param <T> pixel type
	 */
	public static < T > Stream< LocalizableSampler< T > > localizing( IterableInterval< T > interval )
	{
		return StreamSupport.stream( new LocalizableSamplerWrapper<>( interval.localizingSpliterator() ), false );
	}
}
