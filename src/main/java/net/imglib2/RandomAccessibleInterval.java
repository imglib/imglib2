/*
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

package net.imglib2;

import net.imglib2.util.Intervals;

/**
 * <p>
 * <em>f</em>:{x&isin;Z<sup><em>n</em></sup>|[min,max]&rarr;T}
 * </p>
 *
 * <p>
 * A function over an n-dimensional integer interval that can create a random
 * access {@link Sampler}.
 * </p>
 * <p>
 * By convention, a RandomAccessibleInterval represents a function that is
 * <em>defined at all coordinates of the interval</em>.
 * </p>
 * <p>
 * There is no guarantee regarding whether the function is defined outside the
 * bounds of its interval. If the function is known to be defined for
 * out-of-bounds values in a particular interval, the
 * {@link #randomAccess(Interval)} method should be used to access those
 * values&mdash;whereas the {@link #randomAccess()} (no arguments) method <em>is
 * not intended to access out-of-bounds values</em>. See
 * {@link RandomAccessible#randomAccess()} for related discussion.
 * </p>
 *
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public interface RandomAccessibleInterval< T > extends RandomAccessible< T >, Interval
{
	/**
	 *
	 * Gets an instance of T from the {@link RandomAccessibleInterval}.
	 * By default, this queries the value at the min coordinate but individual classes
	 * may choose different implementations for improved performance.
	 *
	 * @return - an instance of T
	 */
	default T getType() {
		return getAt( Intervals.minAsLongArray( this ) );
	}
}
