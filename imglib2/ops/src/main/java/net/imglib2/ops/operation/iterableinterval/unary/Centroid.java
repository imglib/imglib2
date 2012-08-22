package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.Type;

public final class Centroid
                implements
                UnaryOutputOperation<IterableInterval<? extends Type<?>>, double[]> {

        @Override
        public final double[] compute(
                        final IterableInterval<? extends Type<?>> op,
                        final double[] r) {

                if (r.length != op.numDimensions()) {
                        throw new IllegalArgumentException(
                                        "Number of dimensions in result array do not fit (Centroid)");
                }

                Cursor<? extends Type<?>> c = op.cursor();

                while (c.hasNext()) {
                        c.fwd();
                        for (int i = 0; i < r.length; i++) {
                                r[i] += c.getDoublePosition(i);
                        }
                }

                for (int i = 0; i < r.length; i++) {
                        r[i] /= op.size();
                }

                return r;
        }

        @Override
        public UnaryOutputOperation<IterableInterval<? extends Type<?>>, double[]> copy() {
                return new Centroid();
        }

        @Override
        public double[] createEmptyOutput(IterableInterval<? extends Type<?>> in) {
                return new double[in.numDimensions()];
        }

        @Override
        public double[] compute(IterableInterval<? extends Type<?>> in) {
                return compute(in, createEmptyOutput(in));
        }
}
