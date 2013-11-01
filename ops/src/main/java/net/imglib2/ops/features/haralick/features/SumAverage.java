package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.helpers.CoocPXPlusY;
import net.imglib2.type.numeric.real.DoubleType;

public class SumAverage extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private CoocPXPlusY coocPXPlusY;

    @RequiredFeature
    private HaralickCoocMatrix<?> cooc;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Sum Average";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SumAverage copy() {
        return new SumAverage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        double[] pxplusy = coocPXPlusY.get();
        int numGrayLevels = cooc.getNrGrayLevels();

        double res = 0;
        for (int i = 2; i <= 2 * numGrayLevels; i++) {
            res += i * pxplusy[i];
        }

        return new DoubleType(res);
    }

}
