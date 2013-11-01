package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;

public class CoocMeanY extends AbstractFeature<DoubleType> {

    // for symmetric cooccurence matrices stdx = stdy
    @RequiredFeature
    private CoocMeanX coocMeanX = new CoocMeanX();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Helper CoocMeanY";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoocMeanY copy() {
        return new CoocMeanY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        return coocMeanX.get();
    }

}
