package net.imglib2.display.projectors;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.numeric.integer.GenericByteType;
import net.imglib2.util.IntervalIndexer;

public class PlanarImgXYByteProjector<A extends GenericByteType<A>, B extends GenericByteType<B>>
                extends Abstract2DProjector<A, B> {

        private final PlanarImg<A, ByteArray> source;

        private final byte[] targetArray;

        private final double min;

        private final double normalizationFactor;

        private final boolean isSigned;

        private final long[] dims;

        public PlanarImgXYByteProjector(PlanarImg<A, ByteArray> source,
                        ArrayImg<B, ByteArray> target,
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

                byte[] sourceArray = source.update(
                                new PlanarImgContainerSamplerImpl(planeIndex))
                                .getCurrentStorageArray();

                System.arraycopy(sourceArray, offset, targetArray, 0,
                                targetArray.length);

                if (isSigned) {
                        for (int i = 0; i < targetArray.length; i++) {
                                targetArray[i] = (byte) (targetArray[i] - 0x80);
                        }
                        minCopy += 0x80;
                }
                if (normalizationFactor != 1) {
                        int max = 2 * Byte.MAX_VALUE + 1;
                        for (int i = 0; i < targetArray.length; i++) {
                                targetArray[i] = (byte) Math
                                                .min(max,
                                                                Math.max(0,
                                                                                (Math.round((((byte) (targetArray[i] + 0x80)) + 0x80 - minCopy)
                                                                                                * normalizationFactor))));

                        }
                }
        }

}
