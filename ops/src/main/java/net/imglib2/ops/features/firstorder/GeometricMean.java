package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.geometric.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;
import org.knime.knip.core.features.firstorder.sums.SumOfLogs;

public class GeometricMean<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private SumOfLogs<T> m_logSum;

    @RequiredFeature
    private Area m_area;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Geometric Mean";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeometricMean<T> copy() {
        return new GeometricMean<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {

        double logSum = m_logSum.get().get();
        double area = m_area.get().get();

        return new DoubleType(Math.exp(logSum / area));
    }
}
