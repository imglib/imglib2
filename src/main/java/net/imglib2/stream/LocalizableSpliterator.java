/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;

/**
 * A {@code Spliterator<T>} which is Localizable similar to a Cursor.
 * <p>
 * The location of the Spliterator reflects the location of the element passed
 * to the {@code Consumer} in {@link #tryAdvance} or {@link #forEachRemaining}
 * (at the time the element is passed, and until the next element is passed).
 * <p>
 * Similar to {@code Cursor}, {@code LocalizableSpliterator} usually
 * comes in two flavors:
 * <ul>
 *     <li>{@link IterableInterval#spliterator()} computes location only on demand, and</li>
 *     <li>{@link IterableInterval#localizingSpliterator()} preemptively tracks location on every step.</li>
 * </ul>
 * (Which one is more efficient depends on how often location is actually needed.)
 * <p>
 * To make the {@code Localizable} property available in a {@code Stream}, use
 * the {@link Streams} utility class to create {@code
 * Stream<LocalizableSampler<T>>} (which internally wraps {@code
 * LocalizableSpliterator}).
 * <p>
 * Corresponding to the {@code LocalizableSpliterator} flavors,
 * <ul>
 *     <li>{@link Streams#localizable(IterableInterval)} computes element location only on demand, and
 *     <li>{@link Streams#localizing(IterableInterval)} tracks location on every step.</li>
 * </ul>
 *
 * @param <T> pixel type
 */
public interface LocalizableSpliterator< T > extends RealLocalizableSpliterator< T >, Localizable
{
	@Override
	LocalizableSpliterator< T > trySplit();

	@Override
	LocalizableSpliterator< T > copy();
}
