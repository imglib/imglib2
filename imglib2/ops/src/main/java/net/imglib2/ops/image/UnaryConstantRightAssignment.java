package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.numeric.RealType;

public class UnaryConstantRightAssignment<T extends RealType<T>, V extends RealType<V>, O extends RealType<O>>
		implements BinaryOperation<IterableInterval<T>, V, IterableInterval<O>> {

	private BinaryOperation<T, V, O> m_op;

	public UnaryConstantRightAssignment(BinaryOperation<T, V, O> op) {
		m_op = op;
	}

	@Override
	public IterableInterval<O> compute(IterableInterval<T> input, V constant,
			IterableInterval<O> output) {
		Cursor<T> inCursor = input.cursor();
		Cursor<O> outCursor = output.cursor();

		while (inCursor.hasNext() && outCursor.hasNext()) {
			inCursor.fwd();
			outCursor.fwd();
			m_op.compute(inCursor.get(), constant, outCursor.get());
		}

		return output;
	}

	@Override
	public BinaryOperation<IterableInterval<T>, V, IterableInterval<O>> copy() {
		return new UnaryConstantRightAssignment<T, V, O>(m_op);
	}

}
