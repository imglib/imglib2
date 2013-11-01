package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.geometric.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;
import org.knime.knip.core.features.firstorder.sums.SumOfInverses;

public class HarmonicMean<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private SumOfInverses<T> m_inverseSum;

    @RequiredFeature
    private Area m_area = new Area();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Harmonic Mean";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HarmonicMean<T> copy() {
        return new HarmonicMean<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        return new DoubleType(m_area.get().get() / m_inverseSum.get().get());
    }
}
