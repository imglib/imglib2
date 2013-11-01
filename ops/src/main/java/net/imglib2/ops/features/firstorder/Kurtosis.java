package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.firstorder.moments.Moment4AboutMean;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;

public class Kurtosis<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private StdDeviation<T> m_stdDev = new StdDeviation<T>();

    @RequiredFeature
    private Moment4AboutMean<T> m_moment4 = new Moment4AboutMean<T>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Kurtosis Feature";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Kurtosis<T> copy() {
        return new Kurtosis<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        final double std = m_stdDev.get().get();
        final double moment4 = m_moment4.get().get();

        // no Kurtosis in case std = 0
        if (std != 0) {
            return new DoubleType((moment4) / (std * std * std * std));
        } else {
            return new DoubleType(0);
        }
    }

}
