package net.imglib2.ops.features.firstorder.moments;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Moment2AboutMean<T extends RealType<T>> extends AbstractFeature<DoubleType> {

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
        return "Moment 2 About Mean";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Moment2AboutMean<T> copy() {
        return new Moment2AboutMean<T>();
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
            res += val * val;
        }

        return new DoubleType(res / area);
    }
}
