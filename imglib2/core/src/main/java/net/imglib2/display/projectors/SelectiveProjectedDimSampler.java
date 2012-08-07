package net.imglib2.display.projectors;

import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

public class SelectiveProjectedDimSampler<T> implements
                ProjectedDimSamplerImpl<T> {

        private final long[] m_projectedPositions;
        private final int m_projectionDimension;

        private RandomAccess<T> m_source;

        private int m_selectedIndex = 0;

        public SelectiveProjectedDimSampler(int projectionDimension,
                        long[] projectedPositions) {
                m_projectedPositions = projectedPositions;
                m_projectionDimension = projectionDimension;
        }

        @Override
        public void jumpFwd(long steps) {
                m_selectedIndex += steps;
        }

        @Override
        public void fwd() {
                m_selectedIndex++;
        }

        @Override
        public void reset() {
                m_selectedIndex = 0;
        }

        @Override
        public boolean hasNext() {
                return (m_selectedIndex < m_projectedPositions.length);
        }

        @Override
        public T get() {
                m_source.setPosition(m_selectedIndex, m_projectionDimension);
                return m_source.get();
        }

        @Override
        public Sampler<T> copy() {
                m_source.setPosition(m_selectedIndex, m_projectionDimension);
                return m_source.copy();
        }

        @Override
        public void setRandomAccess(RandomAccess<T> srcAccess) {
                m_source = srcAccess;
        }
}
