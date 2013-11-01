package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.type.numeric.real.DoubleType;

import org.knime.knip.core.features.AbstractFeature;
import org.knime.knip.core.features.RequiredFeature;

public class CoocStdX extends AbstractFeature<DoubleType> {

    @RequiredFeature
    private CoocPX coocPX = new CoocPX();

    @RequiredFeature
    private CoocMeanX coocMeanX = new CoocMeanX();

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Helper CoocStdX";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoocStdX copy() {
        return new CoocStdX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {
        double res = 0;

        double meanx = coocMeanX.get().get();
        double[] px = coocPX.get();

        for (int i = 0; i < px.length; i++) {
            res += (i - meanx) * (i - meanx) * px[i];
        }

        res = Math.sqrt(res);

        return new DoubleType(res);
    }

}
