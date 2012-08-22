/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   17 Nov 2011 (hornm): created
 */
package net.imglib2.ops.operation.img.unary;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 *
 * @author hornm, University of Konstanz
 */
public class ImgRotate2D<T extends Type<T> & Comparable<T>> implements
                UnaryOutputOperation<Img<T>, Img<T>> {

        private final double m_angle;

        private final int m_dimIdx1;

        private final int m_dimIdx2;

        private final boolean m_keepSize;

        private final T m_outOfBoundsType;

        private final long[] m_center;

        /**
         * @param angle
         *                the angle (radian)
         * @param dimIdx1
         *                the dimension index to rotate
         * @param dimIdx2
         *                the second dimension to rotate
         * @param keepSize
         *                if true, the result image will have the same size as
         *                the source image, if false, the result is a 2D image
         *                with changed dimension sizes
         * @param outOfBoundsType
         *                the value too be used for undefined pixels (i.e. pixel
         *                positions in the result image which are not existent
         *                in the source image)
         * @param center
         *                the rotation center, if <code>null</code> the image
         *                center will be used
         */
        public ImgRotate2D(double angle, int dimIdx1, int dimIdx2,
                        boolean keepSize, T outOfBoundsType, long[] center) {
                m_angle = angle;
                m_dimIdx1 = dimIdx1;
                m_dimIdx2 = dimIdx2;
                m_keepSize = keepSize;
                m_outOfBoundsType = outOfBoundsType;
                m_center = center;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Img<T> createEmptyOutput(Img<T> op) {
                if (m_keepSize) {
                        return op.factory().create(
                                        op,
                                        op.randomAccess().get()
                                                        .createVariable());
                } else {

                        // rotate all for egde points and take the maximum
                        // coordinate to
                        // determine the new image size
                        long[] min = new long[op.numDimensions()];
                        long[] max = new long[op.numDimensions()];

                        op.min(min);
                        op.max(max);
                        min[m_dimIdx1] = min[m_dimIdx2] = Long.MAX_VALUE;
                        max[m_dimIdx1] = max[m_dimIdx2] = Long.MIN_VALUE;

                        double[] center = calcCenter(op);

                        double x;
                        double y;

                        for (Long orgX : new long[] { 0, op.max(m_dimIdx1) }) {
                                for (Long orgY : new long[] { 0,
                                                op.max(m_dimIdx2) }) {
                                        x = (orgX - center[m_dimIdx1])
                                                        * Math.cos(m_angle)
                                                        - (orgY - center[m_dimIdx2])
                                                        * Math.sin(m_angle);

                                        y = (orgX - center[m_dimIdx1])
                                                        * Math.sin(m_angle)
                                                        + (orgY - center[m_dimIdx2])
                                                        * Math.cos(m_angle);
                                        min[m_dimIdx1] = (int) Math
                                                        .round(Math.min(x,
                                                                        min[m_dimIdx1]));
                                        min[m_dimIdx2] = (int) Math
                                                        .round(Math.min(y,
                                                                        min[m_dimIdx2]));
                                        max[m_dimIdx1] = (int) Math
                                                        .round(Math.max(x,
                                                                        max[m_dimIdx1]));
                                        max[m_dimIdx2] = (int) Math
                                                        .round(Math.max(y,
                                                                        max[m_dimIdx2]));
                                }
                        }

                        long[] dims = new long[min.length];
                        for (int i = 0; i < dims.length; i++) {
                                dims[i] = max[i] - min[i];
                        }

                        return op.factory().create(
                                        new FinalInterval(dims),
                                        op.randomAccess().get()
                                                        .createVariable());

                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Img<T> compute(Img<T> op, Img<T> r) {
                RandomAccess<T> srcRA = Views
                                .extendValue(op, m_outOfBoundsType)
                                .randomAccess();

                Cursor<T> resCur = r.localizingCursor();

                double[] srcCenter = calcCenter(op);
                double[] resCenter = new double[op.numDimensions()];
                for (int i = 0; i < resCenter.length; i++) {
                        resCenter[i] = srcCenter[i]
                                        * (r.dimension(i) / (double) op
                                                        .dimension(i));
                }

                while (resCur.hasNext()) {
                        resCur.fwd();

                        double x = (resCur.getDoublePosition(m_dimIdx1) - resCenter[m_dimIdx1])
                                        * Math.cos(m_angle)
                                        - (resCur.getDoublePosition(m_dimIdx2) - resCenter[m_dimIdx2])
                                        * Math.sin(m_angle)
                                        + srcCenter[m_dimIdx1];
                        double y = (resCur.getDoublePosition(m_dimIdx1) - resCenter[m_dimIdx1])
                                        * Math.sin(m_angle)
                                        + (resCur.getDoublePosition(m_dimIdx2) - resCenter[m_dimIdx2])
                                        * Math.cos(m_angle)
                                        + srcCenter[m_dimIdx2];

                        srcRA.setPosition((int) Math.round(x), m_dimIdx1);
                        srcRA.setPosition((int) Math.round(y), m_dimIdx2);
                        for (int i = 0; i < op.numDimensions(); i++) {
                                if (i != m_dimIdx1 && i != m_dimIdx2) {
                                        srcRA.setPosition(resCur
                                                        .getIntPosition(i), i);
                                }
                        }

                        resCur.get().set(srcRA.get());

                }
                return r;

        }

        private double[] calcCenter(Interval interval) {
                double[] center = new double[interval.numDimensions()];
                if (m_center == null) {
                        for (int i = 0; i < center.length; i++) {
                                center[i] = interval.dimension(i) / 2.0;
                        }
                } else {
                        for (int i = 0; i < center.length; i++) {
                                center[i] = m_center[i];
                        }
                }

                return center;
        }

        @Override
        public UnaryOutputOperation<Img<T>, Img<T>> copy() {
                return new ImgRotate2D<T>(m_angle, m_dimIdx1, m_dimIdx2,
                                m_keepSize, m_outOfBoundsType, m_center);
        }

        @Override
        public Img<T> compute(Img<T> arg0) {
                return compute(arg0, createEmptyOutput(arg0));
        }

}
