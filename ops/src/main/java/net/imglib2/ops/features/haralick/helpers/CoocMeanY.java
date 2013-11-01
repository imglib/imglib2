package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.type.numeric.real.DoubleType;

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
