package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;

public class UnaryOperationAssignment<T extends Type<T>, V extends Type<V>>
		implements UnaryOperation<IterableInterval<T>, IterableInterval<V>> {

	private final UnaryOperation<T, V> m_op;

	public UnaryOperationAssignment(final UnaryOperation<T, V> op) {
		m_op = op;
	}

	@Override
	public IterableInterval<V> compute(IterableInterval<T> input,
			IterableInterval<V> output) {

		if (input.equalIterationOrder(output)
				|| output.equalIterationOrder(input)) {
			throw new IllegalArgumentException("Intervals are not compatible");
		}

		final Cursor<T> opc = input.cursor();
		final Cursor<V> outCursor = output.cursor();
		while (opc.hasNext()) {
			opc.fwd();
			outCursor.fwd();
			m_op.compute(opc.get(), outCursor.get());
		}

		return output;
	}
	

	@Override
	public UnaryOperation<IterableInterval<T>, IterableInterval<V>> copy() {
		return new UnaryOperationAssignment<T, V>(m_op.copy());
	}

	// public Img<V> createEmptyOutput(K dataHint) {
	// long[] dims = new long[dataHint.numDimensions()];
	// dataHint.dimensions(dims);
	// return m_fac.create(dims,
	// m_op.createEmptyOutput(dataHint.firstElement()));
	// }

}
