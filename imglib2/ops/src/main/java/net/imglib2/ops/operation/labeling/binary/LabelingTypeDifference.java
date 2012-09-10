package net.imglib2.ops.operation.labeling.binary;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;

/**
 * Computes the difference between two labelings at a given pixel position
 * 
 * @author christian.dietz
 * 
 * @param <L>
 */
public class LabelingTypeDifference<L extends Comparable<L>> implements
		BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

	@Override
	public LabelingType<L> compute(LabelingType<L> input1,
			LabelingType<L> input2, LabelingType<L> output) {

		output.setLabeling(difference(input1.getLabeling(),
				input2.getLabeling()));

		return output;

	}

	private List<L> difference(List<L> labelingsA, List<L> labelingsB) {

		List<L> set = new ArrayList<L>();
		for (L label : labelingsA) {
			if (!labelingsB.contains(label))
				set.add(label);
		}

		for (L label : labelingsB) {
			if (!labelingsA.contains(label))
				set.add(label);
		}

		return set;
	}

	@Override
	public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
		return new LabelingTypeDifference<L>();
	}

}
