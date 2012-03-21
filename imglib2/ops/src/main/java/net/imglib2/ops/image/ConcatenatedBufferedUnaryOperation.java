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

import net.imglib2.ops.UnaryOperation;

public class ConcatenatedBufferedUnaryOperation<T> implements
		UnaryOperation<T, T> {

	private UnaryOperation<T, T>[] m_operations;
	protected T m_buffer;

	public ConcatenatedBufferedUnaryOperation(T buffer,
			UnaryOperation<T, T>... operations) {
		m_operations = operations;
		m_buffer = buffer;

	}

	@Override
	public T compute(T input, T output) {

		if (m_buffer == null)
			throw new IllegalArgumentException(
					"Buffer can't be null in ConcatenatedBufferedUnaryOperation");

		T tmpOutput = output;
		T tmpInput = input;

		for (UnaryOperation<T, T> op : m_operations) {
			op.compute(tmpInput, tmpOutput);

			tmpInput = tmpOutput;
			tmpOutput = m_buffer;
		}

		return output;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnaryOperation<T, T> copy() {
		UnaryOperation<T, T>[] copyOps = new UnaryOperation[m_operations.length];

		int c = 0;
		for (UnaryOperation<T, T> op : m_operations)
			copyOps[c++] = op.copy();

		return new ConcatenatedBufferedUnaryOperation<T>(m_buffer, copyOps);
	}

}
