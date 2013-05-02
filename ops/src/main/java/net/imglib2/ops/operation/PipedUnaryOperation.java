/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.ops.img.UnaryObjectFactory;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public class PipedUnaryOperation<T> implements UnaryOutputOperation<T, T> {

	private UnaryOutputOperation<T, T>[] operations;

	private UnaryObjectFactory<T, T> factory;

	public PipedUnaryOperation(UnaryOutputOperation<T, T>... operations) {
		this(operations[0].bufferFactory(), operations);
	}

	public PipedUnaryOperation(UnaryObjectFactory<T, T> bufferFactory,
			UnaryOutputOperation<T, T>[] operations) {
		this.factory = bufferFactory;
		this.operations = unpack(operations);
	}

	@SuppressWarnings("unchecked")
	public static <T> UnaryOutputOperation<T, T>[] unpack(
			UnaryOutputOperation<T, T>[] ops) {
		List<UnaryOutputOperation<T, T>> all = new ArrayList<UnaryOutputOperation<T, T>>();

		for (int k = 0; k < ops.length; k++) {
			if (ops[k] instanceof PipedUnaryOperation) {
				for (UnaryOutputOperation<T, T> op : unpack(((PipedUnaryOperation<T>) ops[k])
						.ops())) {
					all.add(op);
				}
			} else {
				all.add(ops[k]);
			}
		}

		return all.toArray(new UnaryOutputOperation[all.size()]);
	}

	@Override
	public T compute(T input, T output) {
		return Operations.compute(input, output, operations);
	}

	@Override
	public UnaryOutputOperation<T, T> copy() {
		@SuppressWarnings("unchecked")
		UnaryOutputOperation<T, T>[] operationCopy = new UnaryOutputOperation[operations.length];

		for (int i = 0; i < operationCopy.length; i++) {
			operationCopy[i] = operations[i].copy();
		}

		return new PipedUnaryOperation<T>(operations);
	}

	public void append(UnaryOutputOperation<T, T> op) {
		@SuppressWarnings("unchecked")
		UnaryOutputOperation<T, T>[] res = new UnaryOutputOperation[operations.length + 1];

		for (int i = 0; i < operations.length; i++)
			res[i] = operations[i];

		res[operations.length] = op;

		operations = res;
	}

	public void append(UnaryOutputOperation<T, T>[] ops) {
		@SuppressWarnings("unchecked")
		UnaryOutputOperation<T, T>[] res = new UnaryOutputOperation[operations.length
				+ ops.length];

		for (int i = 0; i < operations.length; i++)
			res[i] = operations[i];

		for (int i = operations.length; i < operations.length + ops.length; i++)
			res[i] = ops[i - operations.length];

		operations = res;
	}

	public UnaryObjectFactory<T, T> bufferFactory() {
		return factory;
	}

	public UnaryOutputOperation<T, T>[] ops() {
		return operations;
	}

	public int numOps() {
		return operations.length;
	}
}
