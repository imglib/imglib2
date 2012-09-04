package net.imglib2.ops.operation.labeling.binary;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;

public class LabelingAnd<L extends Comparable<L>> implements
		BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

	@Override
	public LabelingType<L> compute(LabelingType<L> input1,
			LabelingType<L> input2, LabelingType<L> output) {

		if (input1.getLabeling().size() != input2.getLabeling().size()) {
			output.setLabeling(output.getMapping().emptyList());

			return output;
		}

		for (int i = 0; i < input1.getLabeling().size(); i++) {
			if (input1.getLabeling().get(i)
					.compareTo(input2.getLabeling().get(i)) != 0) {
				output.setLabeling(output.getMapping().emptyList());

				return output;
			}
		}

		output.setLabeling(input1.getLabeling());

		return output;
	}

	@Override
	public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
		return new LabelingAnd<L>();
	}

}