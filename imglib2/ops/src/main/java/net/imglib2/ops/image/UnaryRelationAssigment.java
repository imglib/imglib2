package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.UnaryRelation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

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
