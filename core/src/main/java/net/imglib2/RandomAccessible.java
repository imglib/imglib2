/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2;

/**
 * <p>
 * <em>f:Z<sup>n</sup>&rarr;T</em>
 * </p>
 * 
 * <p>
 * A function over integer space that can create a random access {@link Sampler}
 * .
 * </p>
 * 
 * <p>
 * If your algorithm takes a RandomAccessible, this usually means that you
 * expect that the domain is infinite. (In contrast to this,
 * {@link RandomAccessibleInterval}s have a finite domain.)
 * </p>
 * 
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public interface RandomAccessible< T > extends EuclideanSpace
{
	/**
	 * Create a random access sampler for integer coordinates.
	 * 
	 * <p>
	 * The returned random access covers as much of the domain as possible.
	 * </p>
	 * 
	 * @return random access sampler
	 */
	public RandomAccess< T > randomAccess();

	/**
	 * Create a random access sampler for integer coordinates.
	 * 
	 * <p>
	 * The returned random access is intended to be used in the specified
	 * interval only. Thus, the RandomAccessible may provide optimized versions.
	 * If the interval is completely contained in the domain, the random access
	 * is guaranteed to provide the same values as that obtained by
	 * {@link #randomAccess()} within the interval.
	 * </p>
	 * 
	 * @param interval
	 *            in which interval you intend to use the random access.
	 * 
	 * @return random access sampler
	 */
	public RandomAccess< T > randomAccess( Interval interval );
}
