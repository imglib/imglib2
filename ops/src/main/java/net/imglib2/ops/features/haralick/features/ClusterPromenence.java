package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.helpers.CoocStdX;
import net.imglib2.type.numeric.real.DoubleType;

//cluster promenence (from cellcognition)
// https://github.com/CellCognition/cecog/blob/master/csrc/include/cecog/features.hxx#L479
public class ClusterPromenence extends AbstractFeature<DoubleType> {

    @RequiredFeature
    HaralickCoocMatrix<?> cooc;

    @RequiredFeature
    CoocStdX coocStdX;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Cluster Promenence";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterPromenence copy() {
        return new ClusterPromenence();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DoubleType recompute() {

        final int nrGrayLevels = cooc.getNrGrayLevels();
        final double stdx = coocStdX.get().get();
        final CooccurrenceMatrix matrix = cooc.get();

        double res = 0;
        for (int j = 0; j < nrGrayLevels; j++) {
            res += Math.pow(2 * j - 2 * stdx, 4) * matrix.getValueAt(j, j);
            for (int i = j + 1; i < nrGrayLevels; i++) {
                res += 2 * Math.pow((i + j - 2 * stdx), 4) * matrix.getValueAt(i, j);
            }
        }
        return new DoubleType(res);
    }

}
