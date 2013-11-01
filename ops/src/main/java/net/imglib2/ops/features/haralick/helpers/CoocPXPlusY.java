package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;

public class CoocPXPlusY extends AbstractFeature<double[]> {

    @RequiredFeature
    private HaralickCoocMatrix<?> cooc;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Helper CoocPXPlusY";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoocPXPlusY copy() {
        return new CoocPXPlusY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double[] recompute() {
        int nrGrayLevels = cooc.getNrGrayLevels();
        CooccurrenceMatrix matrix = cooc.get();

        double[] pxplusy = new double[2 * nrGrayLevels + 1];

        for (int k = 2; k <= 2 * nrGrayLevels; k++) {
            for (int i = 0; i < nrGrayLevels; i++) {
                for (int j = 0; j < nrGrayLevels; j++) {
                    if ((i + 1) + (j + 1) == k) {
                        pxplusy[k] += matrix.getValueAt(i, j);
                    }
                }
            }
        }

        return pxplusy;
    }

}
