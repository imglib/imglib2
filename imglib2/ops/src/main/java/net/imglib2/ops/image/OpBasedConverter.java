package net.imglib2.ops.image;

import net.imglib2.converter.Converter;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 *
 * @author dietzc
 *
 * @param <T>
 * @param <V>
 */
public class OpBasedConverter<T extends RealType<T>, V extends RealType<V>>
                implements Converter<T, V> {

        private final UnaryOperation<T, V> m_op;

        public OpBasedConverter(UnaryOperation<T, V> op) {
                m_op = op;
        }

        @Override
        public void convert(T input, V output) {
                m_op.compute(input, output);
        }

}
