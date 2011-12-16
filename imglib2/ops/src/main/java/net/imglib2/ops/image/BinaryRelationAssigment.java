package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.BinaryRelation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class BinaryRelationAssigment<T extends RealType<T>, V extends RealType<V>>
		implements
		BinaryOperation<IterableInterval<T>, IterableInterval<V>, IterableInterval<BitType>> {

	private BinaryRelation<T, V> m_rel;
	private ImgFactory<BitType> m_fac;

	public BinaryRelationAssigment(ImgFactory<BitType> fac,
			BinaryRelation<T, V> rel) {
		m_rel = rel;
		m_fac = fac;
	}

	@Override
	public IterableInterval<BitType> compute(IterableInterval<T> input1,
			IterableInterval<V> input2, IterableInterval<BitType> output) {

		Cursor<T> inCursor1 = input1.localizingCursor();
		Cursor<V> inCursor2 = input2.localizingCursor();

		if (input1.equalIterationOrder(input2)
				&& input1.equalIterationOrder(output)) {
			throw new IllegalArgumentException("Intervals are not compatible");
		}

		Cursor<BitType> outCursor = output.cursor();
		while (outCursor.hasNext()) {
			outCursor.fwd();
			inCursor1.fwd();
			inCursor2.fwd();

			outCursor.get().set(m_rel.holds(inCursor1.get(), inCursor2.get()));
		}
		return output;

	}

	@Override
	public BinaryOperation<IterableInterval<T>, IterableInterval<V>, IterableInterval<BitType>> copy() {
		return new BinaryRelationAssigment<T, V>(m_fac, m_rel.copy());
	}

}
