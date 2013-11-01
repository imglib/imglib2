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
public class SumOfLogs<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private IterableIntervalProvider<T> interval;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Sum of logs";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SumOfLogs<T> copy() {
        return new SumOfLogs<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        Iterator<T> it = interval.get().iterator();
        double result = 0.0;

        while (it.hasNext()) {
            result += Math.log(it.next().getRealDouble());
        }
        return new DoubleType(result);
    }

}
