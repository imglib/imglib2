package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;

public class CoocPX extends AbstractFeature<double[]> {

    @RequiredFeature
    private HaralickCoocMatrix<?> cooc;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Helper CoocPX";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoocPX copy() {
        return new CoocPX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double[] recompute() {
        int nrGrayLevels = cooc.getNrGrayLevels();
        CooccurrenceMatrix matrix = cooc.get();

        double[] px = new double[nrGrayLevels];
        for (int i = 0; i < nrGrayLevels; i++) {
            for (int j = 0; j < nrGrayLevels; j++) {
                px[i] += matrix.getValueAt(i, j);
            }
        }

        return px;
    }

}
