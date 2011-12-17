package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.Type;

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
