package net.imglib2.ops.operation.labeling.binary;

import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.BinaryOperation;
   public class LabelingXOR<L extends Comparable<L>>
                        implements
                        BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> {

                @Override
                public LabelingType<L> compute(LabelingType<L> input1,
                                LabelingType<L> input2, LabelingType<L> output) {

                        if (input1.getLabeling().isEmpty()
                                        && !input2.getLabeling().isEmpty()) {

                                output.setLabeling(input2.getLabeling());
                                return output;
                        }

                        if (!input1.getLabeling().isEmpty()
                                        && input2.getLabeling().isEmpty()) {
                                output.setLabeling(input1.getLabeling());
                                return output;
                        }

                        return output;
                }

                @Override
                public BinaryOperation<LabelingType<L>, LabelingType<L>, LabelingType<L>> copy() {
                        return new LabelingXOR<L>();
                }
        }