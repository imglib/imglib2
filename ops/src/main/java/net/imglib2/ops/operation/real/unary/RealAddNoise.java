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

package net.imglib2.ops.operation.real.unary;

import java.util.Random;

import net.imglib2.type.numeric.RealType;

/**
 * Sets the real component of an output real number to the addition of
 * the real component of an input real number with an amount of Gaussian
 * noise. The noise parameters are specified in the constructor.
 * 
 * @author Barry DeZonia
 */
public final class RealAddNoise<I extends RealType<I>, O extends RealType<O>>
	implements RealUnaryOperation<I,O>
{
	private final double rangeMin;
	private final double rangeMax;
	private final double rangeStdDev;
	private final Random rng;

	/**
	 * Constructor specifying noise parameters.
	 * @param min - the desired lower bound on the output pixel values
	 * @param max - the desired upper bound on the output pixel values
	 * @param stdDev - the stand deviation of the gaussian random variable
	 */
	public RealAddNoise(double min, double max, double stdDev) {
		this.rangeMin = min;
		this.rangeMax = max;
		this.rangeStdDev = stdDev;
		this.rng = new Random();
		this.rng.setSeed(System.currentTimeMillis());
	}

	@Override
	public O compute(I x, O output) {
		int i = 0;
		do {
			double newVal = x.getRealDouble()
					+ (rng.nextGaussian() * rangeStdDev);

			if ((rangeMin <= newVal) && (newVal <= rangeMax)) {
				output.setReal(newVal);
				return output;
			}

			if (i++ > 100)
				throw new IllegalArgumentException(
						"noise function failing to terminate. probably misconfigured.");
		} while (true);
	}

	@Override
	public RealAddNoise<I,O> copy() {
		return new RealAddNoise<I,O>(rangeMin, rangeMax, rangeStdDev);
	}

}
