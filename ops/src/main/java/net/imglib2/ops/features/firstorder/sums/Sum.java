package net.imglib2.ops.features.firstorder.sums;

import java.util.Iterator;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;
import org.knime.knip.core.features.provider.IterableIntervalProvider;


public class Sum<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private IterableIntervalProvider<T> interval;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Sum";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sum<T> copy() {
        return new Sum<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleType recompute() {
        double result = 0.0f;

        Iterator<T> it = interval.get().iterator();
        while (it.hasNext()) {
            result += it.next().getRealDouble();
        }

        return new DoubleType(result);
    }

}
