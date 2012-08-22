package net.imglib2.ops.operation.iterable.unary;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

public class Fill<T extends Type<T>, ITER extends Iterable<T>> implements
                UnaryOperation<T, ITER> {

        /**
         * @param in
         *                the value
         *
         * @param out
         *                Is filled with the value.
         */
        @Override
        public ITER compute(final T in, final ITER out) {
                for (final T t : out) {
                        t.set(in);
                }
                return out;
        }

        @Override
        public UnaryOperation<T, ITER> copy() {
                return new Fill<T, ITER>();
        }
}
