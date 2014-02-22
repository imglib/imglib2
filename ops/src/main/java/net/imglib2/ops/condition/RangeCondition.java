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

package net.imglib2.ops.condition;

/**
* RangeCondition supports the testing of a specific axis of a dimensional
* position. A range is defined as "from first to last by step". So from
* 1 to 9 by 2 would be an example. The construction specifies which axis
* to test. The specified range makes up a set of step values that are
* tested against in isTrue().
* 
* @author Barry DeZonia
*
*/
public class RangeCondition implements Condition<long[]> {
	final int dimIndex;
	final long first;
	final long last;
	final long step;
	
	/**
	 * Specifies the range values to test a positional axis against. For
	 * instance given a set of data whose Y axis is axis 1 we can test that
	 * Y values range from 1 to 10 by 4. Note that the last value may not be
	 * part of the range (i.e. in this case 1, 5, and 9 are in the range).
	 * 
	 * @param dimIndex
	 *   The axis number of interest
	 * @param first
	 *   The first value in the range
	 * @param last
	 *   The last value in the range.
	 * @param step
	 *   The step between values in the range
	 */
	public RangeCondition(int dimIndex, long first, long last, long step) {
		this.dimIndex = dimIndex;
		this.first = first;
		this.last = last;
		this.step = step;
	}

	/**
	 * Returns true if the given point whose position in the predetermined axis
	 * is one of the step values specified in the constructor. 
	 */
	@Override
	public boolean isTrue(long[] val) {
		long value = val[dimIndex];
		if (value < first) return false;
		if (value > last) return false;
		return (value - first) % step == 0;
	}

	@Override
	public RangeCondition copy() {
		return new RangeCondition(dimIndex, first, last, step);
	}
}

