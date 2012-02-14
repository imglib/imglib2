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
import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.Type;

/**
 * 
 * @author Christian Dietz
 *
 */
public final class BinaryOperationAssignment<I extends Type<I>, V extends Type<V>, O extends Type<O>>
		implements
		BinaryOperation<IterableInterval<I>, IterableInterval<V>, IterableInterval<O>> {

	private final BinaryOperation<I, V, O> m_op;

	public BinaryOperationAssignment(
			final net.imglib2.ops.BinaryOperation<I, V, O> op) {
		m_op = op;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public IterableInterval<O> compute(IterableInterval<I> op1,
			IterableInterval<V> op2, IterableInterval<O> res) {

		if (!IterationOrderUtil.equalIterationOrder(op1, op2)) {
			if (!IterationOrderUtil.equalInterval(op1, op2)) {
				throw new IllegalArgumentException(
						"Intervals are not compatible!");
			}
			Cursor<I> c1 = op1.localizingCursor();
			Cursor<V> c2 = op2.cursor();
			Cursor<O> resC = res.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				resC.fwd();
				m_op.compute(c1.get(), c2.get(), resC.get());
			}
		} else {
			throw new IllegalArgumentException("Intervals are not compatible!");
		}
		return res;
	}

	@Override
	public BinaryOperation<IterableInterval<I>, IterableInterval<V>, IterableInterval<O>> copy() {
		return new BinaryOperationAssignment<I, V, O>(m_op.copy());
	}

}
