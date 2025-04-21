/*-
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
package net.imglib2.stream;

import java.util.Spliterator;
import net.imglib2.IterableRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * A {@code Spliterator<T>} which is Localizable similar to a Cursor.
 * <p>
 * The location of the Spliterator reflects the location of the element passed
 * to the {@code Consumer} in {@link #tryAdvance} or {@link #forEachRemaining}
 * (at the time the element is passed, and until the next element is passed).
 * <p>
 * Similar to {@code RealCursor}, {@code RealLocalizableSpliterator} usually
 * comes in two flavors:
 * <ul>
 *     <li>{@link IterableRealInterval#spliterator()} computes location only on demand, and</li>
 *     <li>{@link IterableRealInterval#localizingSpliterator()} preemptively tracks location on every step.</li>
 * </ul>
 * (Which one is more efficient depends on how often location is actually needed.)
 * <p>
 * To make the {@code RealLocalizable} property available in a {@code Stream},
 * use the {@link Streams} utility class to create {@code
 * Stream<RealLocalizableSampler<T>>} (which internally wraps {@code
 * RealLocalizableSpliterator}).
 * <p>
 * Corresponding to the {@code RealLocalizableSpliterator} flavors,
 * <ul>
 *     <li>{@link Streams#localizable(IterableRealInterval)} computes element location only on demand, and
 *     <li>{@link Streams#localizing(IterableRealInterval)} tracks location on every step.</li>
 * </ul>
 *
 * @param <T> pixel type
 */
public interface RealLocalizableSpliterator< T > extends Spliterator< T >, RealLocalizable, Sampler< T >
{
	@Override
	RealLocalizableSpliterator< T > trySplit();

	@Override
	RealLocalizableSpliterator< T > copy();
}
