package net.imglib2.algorithm.convolver.filter.linear;

import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;

/**
 * Creates n-dimensional gaussian kernel.
 *
 * @author Stephan Sellien, University of Konstanz
 *
 */
public final class Gaussian extends ArrayImg<DoubleType, DoubleAccess> {

        /**
         * Factory method to make calculations before super() possible.
         *
         * @param sigma
         *                sigma parameter
         * @param nrDimensions
         *                number of dimensions
         * @return a new {@link Gaussian} with given parameters.
         */
        public static Gaussian create(final double sigma, final int nrDimensions) {
                final int size = Math.max(3, (2 * (int) (3 * sigma + 0.5) + 1));
                long[] dims = new long[nrDimensions];
                for (int d = 0; d < nrDimensions; d++) {
                        dims[d] = size;
                }
                return new Gaussian(sigma, dims);
        }

        /**
         * Creates a gaussian kernel of given size.
         *
         * @param sigma
         *                sigma
         */
        private Gaussian(final double sigma, final long[] dims) {
                super(new DoubleArray(ArrayImgFactory.numEntitiesRangeCheck(
                                dims, 1)), dims, 1);
                int nrDimensions = dims.length;

                double[] kernel = Util
                                .createGaussianKernel1DDouble(sigma, true);
                // create a Type that is linked to the container
                final DoubleType linkedType = new DoubleType(this);

                // pass it to the native container
                setLinkedType(linkedType);

                Cursor<DoubleType> cursor = localizingCursor();
                while (cursor.hasNext()) {
                        cursor.fwd();
                        double result = 1.0f;
                        for (int d = 0; d < nrDimensions; d++) {
                                result *= kernel[cursor.getIntPosition(d)];
                        }
                        cursor.get().set(result);
                }

        }

}
