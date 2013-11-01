package net.imglib2.ops.features.providers;

import net.imglib2.IterableInterval;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.Source;

public class IterableIntervalProvider<T> extends AbstractFeature<IterableInterval<T>> implements
        Source<IterableInterval<T>> {

    private IterableInterval<T> m_source;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Source: IterableIntervalUpdater";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableIntervalProvider<T> copy() {
        return new IterableIntervalProvider<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IterableInterval<T> recompute() {
        return m_source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final IterableInterval<T> source) {
        m_source = source;
    }
}
