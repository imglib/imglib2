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
public class SumOfInverses<T extends RealType<T>> extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private IterableIntervalProvider<T> interval;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Sum of Inverses";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SumOfInverses<T> copy() {
        return new SumOfInverses<T>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        Iterator<T> it = interval.get().iterator();
        double result = 0.0;

        while (it.hasNext()) {
            result += (1 / it.next().getRealDouble());
        }
        return new DoubleType(result);
    }

}
