package net.imglib2.ops.features.firstorder;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Min<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private GetIterableInterval<T> interval;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Minimum";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Min<T> copy() {
        return new Min<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        double min = Double.MAX_VALUE;

        Iterator<T> it = interval.get().iterator();
        while (it.hasNext()) {
            double val = it.next().getRealDouble();
            min = val < min ? val : min;
        }

        return new DoubleType(min);
    }
}
