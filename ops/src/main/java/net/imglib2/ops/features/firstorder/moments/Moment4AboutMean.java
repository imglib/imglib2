package net.imglib2.ops.features.firstorder.moments;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Moment4AboutMean<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private GetIterableInterval<T> interval;

    @RequiredFeature
    private Mean<T> m_mean;

    @RequiredFeature
    private Area m_area;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Moment 4 about mean";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Moment4AboutMean<T> copy() {
        return new Moment4AboutMean<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        final double mean = m_mean.get().get();
        final double area = m_area.get().get();
        double res = 0.0;

        Iterator<T> it = interval.get().iterator();
        while (it.hasNext()) {
            final double val = it.next().getRealDouble() - mean;
            res += val * val * val * val;
        }

        return new DoubleType(res / area);
    }
}
