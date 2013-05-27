package net.imglib2.display.projectors.dimsamplers;

import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

/**
 * allows stepwise access to a preselected projected dimension. 
 * 
 * @author zinsmaie
 *
 * @param <T>
 */
public class IntervalProjectedDimSampler<T> implements
                ProjectedDimSamplerImpl<T> {

        private final int m_projectionDimension;
        private final long m_startPosition;
        private final long m_endPosition;

        private RandomAccess<T> m_source;

        public IntervalProjectedDimSampler(int projectionDimension,
                        long startPosition, long endPosition) {

                m_projectionDimension = projectionDimension;
                m_startPosition = startPosition;
                m_endPosition = endPosition;
        }

        @Override
        public void jumpFwd(long steps) {
                for (int i = 0; i < steps; i++) {
                        fwd();
                }
        }

        @Override
        public void fwd() {
                m_source.fwd(m_projectionDimension);
        }

        @Override
        public void reset() {
                m_source.setPosition(m_startPosition, m_projectionDimension);
        }

        @Override
        public boolean hasNext() {
                return (m_source.getLongPosition(m_projectionDimension) <= m_endPosition);
        }

        @Override
        public T get() {
                return m_source.get();
        }

        @Override
        public Sampler<T> copy() {
                return m_source.copy();
        }

        @Override
        public void setRandomAccess(RandomAccess<T> srcAccess) {
                m_source = srcAccess;
        }

}
