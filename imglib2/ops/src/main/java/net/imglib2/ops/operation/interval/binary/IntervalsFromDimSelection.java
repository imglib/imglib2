package net.imglib2.ops.operation.interval.binary;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.ops.operation.BinaryOutputOperation;
import net.imglib2.ops.operation.subset.unary.IterateUnaryOperation;

/**
 * Merges an interval array
 * 
 * {@link IntervalwiseUnaryManipulation}, {@link IterateUnaryOperation},
 * {@link IterateUnaryOperation} or {@link IterativeBinaryImgTransformation}.
 * 
 * @author dietzc
 */
public class IntervalsFromDimSelection implements
                BinaryOutputOperation<int[], Interval[], Interval[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Interval[] createEmptyOutput(int[] op0, Interval[] op1) {

                int totalSteps = 0;
                for (int i = 0; i < op1.length; i++) {
                        totalSteps += getNumIterationSteps(op0, op1[i]);
                }
                return new Interval[totalSteps];
        }

        /**
         * {@inheritDoc}
         * 
         * @return
         */
        @Override
        public Interval[] compute(int[] selectedDims,
                        Interval[] incomingIntervals, Interval[] resIntervals) {

                int offset = 0;
                for (int i = 0; i < incomingIntervals.length; i++) {

                        long[] min = new long[incomingIntervals[i]
                                        .numDimensions()];
                        long[] pointCtr = new long[incomingIntervals[i]
                                        .numDimensions()];
                        long[] srcDims = new long[incomingIntervals[i]
                                        .numDimensions()];

                        incomingIntervals[i].min(min);
                        incomingIntervals[i].max(pointCtr);
                        incomingIntervals[i].dimensions(srcDims);

                        long[] max = pointCtr.clone();

                        int[] unselectedDims = getUnselectedDimIndices(
                                        selectedDims, srcDims.length);

                        long[] indicators = new long[unselectedDims.length];
                        Interval interval = new FinalInterval(min, pointCtr);

                        for (int j = indicators.length - 1; j > -1; j--) {
                                indicators[j] = 1;
                                if (j < indicators.length - 1)
                                        indicators[j] = (srcDims[unselectedDims[j + 1]])
                                                        * indicators[j + 1];
                        }

                        for (int u : unselectedDims) {
                                pointCtr[u] = -1;
                        }

                        for (int n = 0; n < getNumIterationSteps(selectedDims,
                                        incomingIntervals[i]); n++) {
                                max = pointCtr.clone();

                                for (int j = 0; j < indicators.length; j++) {
                                        if (n % indicators[j] == 0)
                                                pointCtr[unselectedDims[j]]++;

                                        if (srcDims[unselectedDims[j]] == pointCtr[unselectedDims[j]])
                                                pointCtr[unselectedDims[j]] = 0;
                                }

                                for (int u : unselectedDims) {
                                        max[u] = pointCtr[u] + min[u];
                                        min[u] = max[u];
                                }

                                resIntervals[offset + n] = new FinalInterval(
                                                min, max);
                                interval.min(min);
                        }
                }
                return resIntervals;
        }

        /**
	 * 
	 */

        public Interval[] compute(int[] in1, Interval[] in2) {
                return compute(in1, in2, createEmptyOutput(in1, in2));
        }

        /**
         * @param dims
         * @return
         */
        private final static synchronized int getNumIterationSteps(
                        int[] selectedDims, Interval interval) {

                long[] dims = new long[interval.numDimensions()];
                interval.dimensions(dims);

                int[] unselectedDims = getUnselectedDimIndices(selectedDims,
                                dims.length);
                int steps = 1;
                for (int i = 0; i < unselectedDims.length; i++) {
                        steps *= dims[unselectedDims[i]];
                }

                return steps;
        }

        /**
         * @return
         */
        private final static synchronized int[] getUnselectedDimIndices(
                        int[] selectedDims, int numDims) {
                final boolean[] tmp = new boolean[numDims];
                int i;
                for (i = 0; i < selectedDims.length; i++) {
                        if (selectedDims[i] >= numDims) {
                                break;
                        }
                        tmp[selectedDims[i]] = true;
                }

                int[] res = new int[numDims - i];

                int j = 0;
                for (int k = 0; j < res.length; k++) {
                        if (k >= tmp.length || !tmp[k]) {
                                res[j++] = k;
                        }
                }
                return res;

        }

        @Override
        public BinaryOutputOperation<int[], Interval[], Interval[]> copy() {
                return new IntervalsFromDimSelection();
        }

}
