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

package net.imglib2.util;

/**
 * {@link RealSum} implements a method to reduce numerical instabilities when
 * summing up a very large number of double precision numbers. Numerical
 * problems occur when a small number is added to an already very large sum. In
 * such case, the reduced accuracy of the very large number may lead to the
 * small number being entirely ignored. The method here is Neumaier's
 * improvement of the Kahan summation algorithm.
 * See <a href="https://en.wikipedia.org/wiki/Kahan_summation_algorithm">this
 * Wikipedia article</a> for details.
 *
 * @author Michael Innerberger, Stephan Saalfeld
 */
public class RealSum {
	private double sum = 0.0;
	private double compensation = 0.0;

	/**
	 * Create a new {@link RealSum} initialized to zero.
	 */
	public RealSum() {}

	/**
	 * Create a new {@link RealSum} initialized to zero. This constructor
	 * was used in a previous version of {@link RealSum} and is kept for
	 * backwards compatibility.
	 *
	 * @param capacity unused
	 */
	@Deprecated
	public RealSum(final int capacity) {
		this();
	}

	/**
	 * Get the current sum.
	 */
	public double getSum() {
		return sum + compensation;
	}

	/**
	 * Add an element to the sum.
	 *
	 * @param value the summand to be added
	 */
	public void add(double value) {
		double t = sum + value;
		if (Math.abs(sum) >= Math.abs(value)) {
			compensation += (sum - t) + value;
		} else {
			compensation += (value - t) + sum;
		}
		sum = t;
	}
}
