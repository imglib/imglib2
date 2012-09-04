package net.imglib2.ops.operation.labeling.binary;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;

public class LabelingDifference<L extends Comparable<L>> implements
		BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

	@Override
	public LabelingType<L> compute(LabelingType<L> input1,
			LabelingType<L> input2, LabelingType<L> output) {

		if (input1.getLabeling().size() != 0
				&& input2.getLabeling().size() != 0) {

			output.setLabeling(intersect(
					new ArrayList<L>(input1.getLabeling()), new ArrayList<L>(
							input2.getLabeling())));
			return output;
		}

		return output;
	}

	private List<L> intersect(List<L> labelingsA, List<L> labelingsB) {

		List<L> intersection = new ArrayList<L>();
		for (L label : labelingsA) {
			if (labelingsB.contains(label))
				intersection.add(label);
		}
		return intersection;
	}

	@Override
	public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
		return new LabelingDifference<L>();
	}

}
