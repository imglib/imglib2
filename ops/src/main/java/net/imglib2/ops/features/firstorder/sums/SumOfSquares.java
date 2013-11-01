package net.imglib2.ops.features.firstorder.sums;

import java.util.Iterator;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;
import org.knime.knip.core.features.provider.IterableIntervalProvider;

/**
 *
 * @author graumanna
 */
public class SumOfSquares<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private IterableIntervalProvider<T> interval = new IterableIntervalProvider<T>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Sum of Squares";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SumOfSquares<T> copy() {
        return new SumOfSquares<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        Iterator<T> it = interval.get().iterator();
        double result = 0.0;

        while (it.hasNext()) {
            final double val = it.next().getRealDouble();
            result += (val * val);
        }
        return new DoubleType(result);
    }

}
