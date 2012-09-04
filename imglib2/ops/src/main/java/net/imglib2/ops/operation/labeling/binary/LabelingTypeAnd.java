package net.imglib2.ops.operation.labeling.binary;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;

/**
 * AND is defined as follows: if both labelingtypes are not empty
 * the labels of input1 written into output
 * 
 * @author christian.dietz
 * 
 * @param <L>
 */
public class LabelingTypeAnd<L extends Comparable<L>> implements
		BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

	@Override
	public LabelingType<L> compute(LabelingType<L> input1,
			LabelingType<L> input2, LabelingType<L> output) {

		if (input1.getLabeling().size() != 0
				&& input2.getLabeling().size() != 0) {
			output.setLabeling(input1.getLabeling());
			return output;
		}

		return output;
	}

	@Override
	public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
		return new LabelingTypeAnd<L>();
	}

}