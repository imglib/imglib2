package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.numeric.RealType;

public class UnaryConstantLeftAssignment<V extends RealType<V>, T extends RealType<T>, O extends RealType<V>>
		implements BinaryOperation<V, IterableInterval<T>, IterableInterval<O>> {

	private BinaryOperation<V, T, O> m_op;

	public UnaryConstantLeftAssignment(BinaryOperation<V, T, O> op) {
		m_op = op;
	}

	@Override
	public IterableInterval<O> compute(V constant, IterableInterval<T> input,
			IterableInterval<O> output) {
		Cursor<T> inCursor = input.cursor();
		Cursor<O> outCursor = output.cursor();

		while (inCursor.hasNext() && outCursor.hasNext()) {
			inCursor.fwd();
			outCursor.fwd();
			m_op.compute(constant, inCursor.get(), outCursor.get());
		}

		return output;
	}

	@Override
	public BinaryOperation<V, IterableInterval<T>, IterableInterval<O>> copy() {
		return new UnaryConstantLeftAssignment<V, T, O>(m_op);
	}

}
