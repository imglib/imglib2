/*

Copyright (c) 2011, Christian Dietz
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.BinaryRelation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Christian Dietz
 *
 */
public class BinaryRelationAssigment<T extends RealType<T>, V extends RealType<V>>
		implements
		BinaryOperation<IterableInterval<T>, IterableInterval<V>, IterableInterval<BitType>> {

	private BinaryRelation<T, V> m_rel;
	private ImgFactory<BitType> m_fac;

	public BinaryRelationAssigment(ImgFactory<BitType> fac,
			BinaryRelation<T, V> rel) {
		m_rel = rel;
		m_fac = fac;
	}

	@Override
	public IterableInterval<BitType> compute(IterableInterval<T> input1,
			IterableInterval<V> input2, IterableInterval<BitType> output) {

		Cursor<T> inCursor1 = input1.localizingCursor();
		Cursor<V> inCursor2 = input2.localizingCursor();

		if (input1.equalIterationOrder(input2)
				&& input1.equalIterationOrder(output)) {
			throw new IllegalArgumentException("Intervals are not compatible");
		}

		Cursor<BitType> outCursor = output.cursor();
		while (outCursor.hasNext()) {
			outCursor.fwd();
			inCursor1.fwd();
			inCursor2.fwd();

			outCursor.get().set(m_rel.holds(inCursor1.get(), inCursor2.get()));
		}
		return output;

	}

	@Override
	public BinaryOperation<IterableInterval<T>, IterableInterval<V>, IterableInterval<BitType>> copy() {
		return new BinaryRelationAssigment<T, V>(m_fac, m_rel.copy());
	}

}
