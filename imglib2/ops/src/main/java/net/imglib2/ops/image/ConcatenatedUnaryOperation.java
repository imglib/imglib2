package net.imglib2.ops.image;

import net.imglib2.ops.UnaryOperation;

public class ConcatenatedUnaryOperation<T> implements UnaryOperation<T, T> {

	private UnaryOperation<T, T>[] m_operations;

	public ConcatenatedUnaryOperation(UnaryOperation<T, T>... operations) {
		m_operations = operations;

	}

	@Override
	public T compute(T input, T output) {

		T tmpInput = input;
		for (UnaryOperation<T, T> op : m_operations) {
			op.compute(tmpInput, output);
			tmpInput = output;
		}
		return tmpInput;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnaryOperation<T, T> copy() {
		UnaryOperation<T, T>[] copyOps = new UnaryOperation[m_operations.length];

		int c = 0;
		for (UnaryOperation<T, T> op : m_operations)
			copyOps[c++] = op.copy();

		return new ConcatenatedUnaryOperation<T>(copyOps);
	}

}
