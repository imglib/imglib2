package net.imglib2.algorithm.features.seg;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

/*
 * This file is part of Lire.
 *
 * Lire is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General  License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Lire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lire; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Copyright statement:
 * --------------------
 * (c) 2008 by Marko Keuschnig & Christian Penz for the original code
 * (c) 2008 by Mathias Lux (mathias@juggle.at) for the Lire integration code
 * http://www.juggle.at, http://caliph-emir.sourceforge.net
 */

/**
 * Implementation of (three) Tamura features done by Marko Keuschnig & Christian
 * Penz<br>
 * Changes by
 * <ul>
 * <li>Ankit Jain (jankit87@gmail.com): histogram length in set string
 * <li>shen72@users.sourceforge.net: bugfixes in math (casting and brackets)
 * <li>Christian Dietz, University of Konstanz -> Integrated to KNIME / ImgLib</li>
 * </ul>
 * Date: 28.05.2008 Time: 11:52:03
 *
 * TODO: More imglibish implementation
 *
 * @author Mathias Lux, mathias@juggle.at
 */
public class Tamura<T extends RealType<T>> {

        private int[][] m_greyValues;

        private double[] m_directionality;

        private int m_numPix;

        private double m_mean;

        private final String[] m_enabledFeatureNames;

        private final int m_dimX;

        private final int m_dimY;

        private static final double[][] filterH = { { -1, 0, 1 }, { -1, 0, 1 },
                        { -1, 0, 1 } };

        private static final double[][] filterV = { { -1, -1, -1 },
                        { 0, 0, 0 }, { 1, 1, 1 } };

        public Tamura(int dimX, int dimY, String[] enabledFeatureNames) {
                m_enabledFeatureNames = enabledFeatureNames;
                m_dimX = dimX;
                m_dimY = dimY;
        }

        /**
         * @return
         */
        private double coarseness() {
                double result = 0;

                for (int i = 1; i < m_greyValues.length - 1; i++) {
                        for (int j = 1; j < m_greyValues[i].length - 1; j++) {
                                result = result
                                                + Math.pow(2,
                                                                this.sizeLeadDiffValue(
                                                                                i,
                                                                                j));
                        }
                }

                result = (1.0 / m_numPix) * result;
                return result;
        }

        /**
         * 1. For every point(x, y) calculate the average over neighborhoods.
         *
         * @param x
         * @param y
         * @return
         */
        private final double averageOverNeighborhoods(int x, int y, int k) {
                double result = 0, border;
                border = Math.pow(2, 2 * k);
                int x0 = 0, y0 = 0;

                for (int i = 0; i < border; i++) {
                        for (int j = 0; j < border; j++) {
                                x0 = x - (int) Math.pow(2, k - 1) + i;
                                y0 = y - (int) Math.pow(2, k - 1) + j;
                                if (x0 < 0)
                                        x0 = 0;
                                if (y0 < 0)
                                        y0 = 0;
                                if (x0 >= m_greyValues.length)
                                        x0 = m_greyValues.length - 1;
                                if (y0 >= m_greyValues[0].length)
                                        y0 = m_greyValues[0].length - 1;

                                result = result + m_greyValues[x0][y0];
                        }
                }
                result = (1 / Math.pow(2, 2 * k)) * result;
                return result;
        }

        /**
         * 2. For every point (x, y) calculate differences between the not
         * overlapping neighborhoods on opposite sides of the point in
         * horizontal direction.
         *
         * @param x
         * @param y
         * @return
         */
        private final double differencesBetweenNeighborhoodsHorizontal(int x,
                        int y, int k) {
                double result = 0;
                result = Math.abs(this.averageOverNeighborhoods(
                                x + (int) Math.pow(2, k - 1), y, k)
                                - this.averageOverNeighborhoods(
                                                x - (int) Math.pow(2, k - 1),
                                                y, k));
                return result;
        }

        /**
         * 2. For every point (x, y) calculate differences between the not
         * overlapping neighborhoods on opposite sides of the point in vertical
         * direction.
         *
         * @param x
         * @param y
         * @return
         */
        private final double differencesBetweenNeighborhoodsVertical(int x,
                        int y, int k) {
                double result = 0;
                result = Math.abs(this.averageOverNeighborhoods(x, y
                                + (int) Math.pow(2, k - 1), k)
                                - this.averageOverNeighborhoods(x, y
                                                - (int) Math.pow(2, k - 1), k));
                return result;
        }

        /**
         * 3. At each point (x, y) select the size leading to the highest
         * difference value.
         *
         * @param x
         * @param y
         * @return
         */
        private final int sizeLeadDiffValue(int x, int y) {
                double result = 0, tmp;
                int maxK = 1;

                for (int k = 0; k < 3; k++) {
                        tmp = Math.max(this
                                        .differencesBetweenNeighborhoodsHorizontal(
                                                        x, y, k),
                                        this.differencesBetweenNeighborhoodsVertical(
                                                        x, y, k));
                        if (result < tmp) {
                                maxK = k;
                                result = tmp;
                        }
                }
                return maxK;
        }

        /**
         * Picture Quality.
         *
         * @return
         */
        private final double contrast() {
                double result = 0, sigma, my4 = 0, alpha4 = 0;

                sigma = this.calculateSigma();

                for (int x = 0; x < m_greyValues.length; x++) {
                        for (int y = 0; y < m_greyValues[x].length; y++) {
                                my4 = my4
                                                + Math.pow(m_greyValues[x][y]
                                                                - m_mean, 4);
                        }
                }

                alpha4 = my4 / (Math.pow(sigma, 4));
                result = sigma / (Math.pow(alpha4, 0.25));
                return result;
        }

        /**
         * @param mean
         * @return
         */
        private final double calculateSigma() {
                double result = 0;

                for (int x = 0; x < m_greyValues.length; x++) {
                        for (int y = 0; y < m_greyValues[x].length; y++) {
                                result = result
                                                + Math.pow(m_greyValues[x][y]
                                                                - m_mean, 2);

                        }
                }
                result = result / m_numPix;
                return Math.sqrt(result);
        }

        /**
         * @return
         */
        private final double[] directionality() {
                double[] histogram = new double[16];
                double maxResult = 3;
                double binWindow = maxResult / (histogram.length - 1);
                int bin = -1;
                for (int x = 1; x < m_greyValues.length - 1; x++) {
                        for (int y = 1; y < m_greyValues[x].length - 1; y++) {
                                bin = (int) ((Math.PI / 2 + Math.atan(this
                                                .calculateDeltaV(x, y)
                                                / this.calculateDeltaH(x, y))) / binWindow);
                                histogram[bin]++;
                        }
                }
                return histogram;
        }

        /**
         * @return
         */
        private final double calculateDeltaH(int x, int y) {
                double result = 0;

                for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                                result = result
                                                + m_greyValues[x - 1 + i][y - 1
                                                                + j]
                                                * filterH[i][j];
                        }
                }

                return result;
        }

        /**
         * @param x
         * @param y
         * @return
         */
        private final double calculateDeltaV(int x, int y) {
                double result = 0;

                for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                                result = result
                                                + m_greyValues[x - 1 + i][y - 1
                                                                + j]
                                                * filterV[i][j];
                        }
                }
                return result;
        }

        /**
         * @param img
         * @param s
         * @return
         */
        public final double[] updateROI(IterableInterval<T> interval) {

                Cursor<T> cursor = interval.localizingCursor();
                int minVal = (int) interval.firstElement().getMinValue();

                m_greyValues = new int[(int) interval.dimension(m_dimX)][(int) interval
                                .dimension(m_dimY)];
                m_numPix = (int) interval.size();

                while (cursor.hasNext()) {
                        cursor.fwd();

                        int x = (int) (cursor.getIntPosition(m_dimX) - interval
                                        .min(m_dimX));
                        int y = (int) (cursor.getIntPosition(m_dimY) - interval
                                        .min(m_dimY));

                        m_greyValues[x][y] = (int) cursor.get().getRealDouble()
                                        - minVal;

                        m_mean += m_greyValues[x][y];
                }

                m_mean /= m_numPix;

                double[] histogram = new double[18];

                if (isEnabled(TamuraFeatureSet.FEATURES[0]))
                        histogram[0] = this.coarseness();

                if (isEnabled(TamuraFeatureSet.FEATURES[1]))
                        histogram[1] = this.contrast();

                if (isEnabled(TamuraFeatureSet.FEATURES[2])
                                || isEnabled(TamuraFeatureSet.FEATURES[3])
                                || isEnabled(TamuraFeatureSet.FEATURES[4])
                                || isEnabled(TamuraFeatureSet.FEATURES[5])) {

                        m_directionality = this.directionality();

                        for (int i = 2; i < histogram.length; i++) {
                                histogram[i] = m_directionality[i - 2];
                        }
                }

                return histogram;
        }

        public boolean isEnabled(String featureName) {
                for (String s : m_enabledFeatureNames) {
                        if (s.equals(featureName))
                                return true;
                }
                return false;
        }
}
