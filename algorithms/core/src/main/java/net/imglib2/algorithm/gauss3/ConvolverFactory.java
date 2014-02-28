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

package net.imglib2.algorithm.gauss3;

import net.imglib2.RandomAccess;

/**
 * {@link ConvolverFactory} creates 1-dimensional line convolvers. See
 * {@link #create(double[], RandomAccess, RandomAccess, int, long)}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * 
 * @param <S>
 * @param <T>
 */
public interface ConvolverFactory< S, T >
{
	/**
	 * Create a 1-dimensional line convolver. A line convolver has an input and
	 * an output {@link RandomAccess}. They are moved forwards a long a line in
	 * dimension d, reading source values from the input and writing convolved
	 * values to the output. The line convolver is a Runnable. The idea is to
	 * put the input and an output {@link RandomAccess} to the start of a line,
	 * then call {@link Runnable#run()} to do the convolution. Then the input
	 * and an output {@link RandomAccess} are moved to the next line,
	 * {@link Runnable#run()} is called again, and so on.
	 * 
	 * @param halfkernel
	 *            the upper half (starting at the center pixel) of the symmetric
	 *            convolution kernel.
	 * @param in
	 *            {@link RandomAccess} on the source values.
	 * @param out
	 *            {@link RandomAccess} on the target (convolved) values.
	 * @param d
	 *            dimension in which to convolve.
	 * @param lineLength
	 *            how many convolved values to produce in one
	 *            {@link Runnable#run()}.
	 * @return a line convolver.
	 */
	public Runnable create( final double[] halfkernel, final RandomAccess< S > in, final RandomAccess< T > out, final int d, final long lineLength );
}
