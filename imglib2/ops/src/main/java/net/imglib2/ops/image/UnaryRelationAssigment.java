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
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.UnaryRelation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Christian Dietz
 *
 */
public class UnaryRelationAssigment<T extends RealType<T>> implements
		UnaryOperation<IterableInterval<T>, IterableInterval<BitType>> {

	private UnaryRelation<T> m_rel;

	public UnaryRelationAssigment(UnaryRelation<T> rel) {
		m_rel = rel;
	}

	@Override
	public IterableInterval<BitType> compute(IterableInterval<T> input,
			IterableInterval<BitType> output) {

		if (!input.equalIterationOrder(output)) {
			throw new IllegalArgumentException("Intervals are not compatible");
		}

		Cursor<T> inCursor = input.localizingCursor();
		Cursor<BitType> outCursor = output.cursor();

		while (outCursor.hasNext()) {
			outCursor.get().set(m_rel.holds(inCursor.get()));
		}
		return output;
	}

	@Override
	public UnaryOperation<IterableInterval<T>, IterableInterval<BitType>> copy() {
		return new UnaryRelationAssigment<T>(m_rel.copy());
	}

}
