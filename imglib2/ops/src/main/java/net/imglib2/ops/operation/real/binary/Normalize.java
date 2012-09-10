package net.imglib2.ops.operation.real.binary;

import net.imglib2.converter.Converter;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Normalize<T extends RealType<T>> implements UnaryOperation<T, T>,
                Converter<T, T> {

        private final double m_minNormVal;
        private final double m_factor;

        private final T m_inMin;
        private final T m_inMax;
        private final T m_maxResVal;
        private final T m_minResVal;

        public Normalize(T min, T max) {

                m_inMin = min;
                m_inMax = max;
                T type = min.createVariable();
                m_minResVal = type.createVariable();
                m_maxResVal = type.createVariable();
                m_minResVal.setReal(m_minResVal.getMinValue());
                m_maxResVal.setReal(m_maxResVal.getMaxValue());

                m_factor = 1 / (max.getRealDouble() - min.getRealDouble())
                                * ((type.getMaxValue() - type.getMinValue()));
                m_minNormVal = type.getMinValue();
        }

        @Override
        public T compute(T input, T output) {
                if (input.compareTo(m_inMin) < 0) {
                        output.set(m_minResVal.copy());
                } else if (input.compareTo(m_inMax) > 0) {
                        output.set(m_maxResVal.copy());
                } else {
                        output.setReal((input.getRealDouble() - m_inMin
                                        .getRealDouble())
                                        * m_factor
                                        + m_minNormVal);
                }
                return output;
        }

        @Override
        public UnaryOperation<T, T> copy() {
                return new Normalize<T>(m_inMin, m_inMax);
        }

        @Override
        public void convert(T input, T output) {
                compute(input, output);
        }

}
