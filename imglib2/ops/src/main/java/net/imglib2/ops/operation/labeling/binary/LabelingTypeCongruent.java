package net.imglib2.ops.operation.labeling.binary;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;

/**
 * Congruent is defined as follows: if and only iff two LabelingTypes contain
 * exactly the same labels, then they are written into the output
 * 
 * @author christian.dietz
 * 
 * @param <L>
 */
public class LabelingTypeCongruent<L extends Comparable<L>> implements
		BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

	@Override
	public LabelingType<L> compute(LabelingType<L> input1,
			LabelingType<L> input2, LabelingType<L> output) {

		if (input1.getLabeling().size() == input2.getLabeling().size()) {

			for (L label : input1.getLabeling()) {
				if (!input2.getLabeling().contains(label))
					return output;
			}

			output.setLabeling(input1.getLabeling());
		}

		return output;
	}

	@Override
	public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
		return new LabelingTypeCongruent<L>();
	}

}