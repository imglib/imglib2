package net.imglib2.ops.features.firstorder;

import java.util.Iterator;

import net.imglib2.ops.features.providers.IterableIntervalProvider;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;

public class Max<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private IterableIntervalProvider<T> interval = new IterableIntervalProvider<T>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Maximum";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Max<T> copy() {
        return new Max<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        double max = Double.MIN_VALUE;

        Iterator<T> it = interval.get().iterator();
        while (it.hasNext()) {
            double val = it.next().getRealDouble();
            max = val > max ? val : max;
        }

        return new DoubleType(max);
    }
}
