package net.imglib2.ops.operation.labeling.binary;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;

/**
 * LabelingTypes are merged.
 * 
 * @author christian.dietz
 * 
 * @param <L>
 */
public class LabelingTypeMerge<L extends Comparable<L>> implements
		BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

	@Override
	public LabelingType<L> compute(LabelingType<L> input1,
			LabelingType<L> input2, LabelingType<L> output) {

		List<L> labelings = new ArrayList<L>(input1.getLabeling());
		labelings.addAll(input2.getLabeling());
		output.setLabeling(labelings);

		return output;

	}

	@Override
	public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
		return new LabelingTypeMerge<L>();
	}

}