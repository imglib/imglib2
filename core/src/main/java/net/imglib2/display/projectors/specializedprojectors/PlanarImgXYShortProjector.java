package net.imglib2.display.projectors.specializedprojectors;

import net.imglib2.display.projectors.Abstract2DProjector;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.numeric.integer.GenericShortType;
import net.imglib2.util.IntervalIndexer;

/**
 * Fast implementation of a {@link Abstract2DProjector} that selects a 2D data plain from an ShortType PlanarImg. The map method implements
 * a normalization function. The resulting image is a ShortType ArrayImg. * 
 *  
 * @author zinsmaie
 *
 * @param <A>
 * @param <B>
 */
public class PlanarImgXYShortProjector<A extends GenericShortType<A>, B extends GenericShortType<B>>
                extends Abstract2DProjector<A, B> {

        private final PlanarImg<A, ShortArray> source;

        private final short[] targetArray;

        private final double min;

        private final double normalizationFactor;

        private final boolean isSigned;

        private final long[] dims;

        public PlanarImgXYShortProjector(PlanarImg<A, ShortArray> source,
                        ArrayImg<B, ShortArray> target,
                        double normalizationFactor, double min, boolean isSigned) {
                super(source.numDimensions());

                this.isSigned = isSigned;
                this.targetArray = target.update(null).getCurrentStorageArray();
                this.normalizationFactor = normalizationFactor;
                this.min = min;
                this.dims = new long[numDimensions];
                source.dimensions(dims);

                this.source = source;
        }

        @Override
        public void map() {

                double minCopy = min;
                int offset = 0;

                // positioning for every call to map because the plane index is
                // position dependent
                int planeIndex;
                if (position.length > 2) {
                        long[] tmpPos = new long[position.length - 2];
                        long[] tmpDim = new long[position.length - 2];
                        for (int i = 0; i < tmpDim.length; i++) {
                                tmpPos[i] = position[i + 2];
                                tmpDim[i] = source.dimension(i + 2);
                        }
                        planeIndex = (int) IntervalIndexer.positionToIndex(
                                        tmpPos, tmpDim);
                } else {
                        planeIndex = 0;
                }

                short[] sourceArray = source.update(
                                new PlanarImgContainerSamplerImpl(planeIndex))
                                .getCurrentStorageArray();

                System.arraycopy(sourceArray, offset, targetArray, 0,
                                targetArray.length);

                if (isSigned) {
                        for (int i = 0; i < targetArray.length; i++) {
                                targetArray[i] = (short) (targetArray[i] - 0x8000);
                        }
                        minCopy += 0x8000;
                }
                if (normalizationFactor != 1) {
                        int max = 2 * Short.MAX_VALUE + 1;
                        for (int i = 0; i < targetArray.length; i++) {
                                targetArray[i] = (short) Math
                                                .min(max,
                                                                Math.max(0,
                                                                                (Math.round((((short) (targetArray[i] + 0x8000)) + 0x8000 - minCopy)
                                                                                                * normalizationFactor))));

                        }
                }
        }

}
