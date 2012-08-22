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
 *   12 May 2011 (hornm): created
 */
package net.imglib2.ops.operation.imgplus.unary;

import java.util.Arrays;
import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.Type;

/**
 * Extends the image by new dimensions, if they don't exist yet.
 * 
 * @author hornm, dietzc University of Konstanz
 */
public class ImgPlusExtendDims<T extends Type<T>> implements
                UnaryOutputOperation<ImgPlus<T>, ImgPlus<T>> {

        private final String[] m_newDimensions;

        BitSet m_isNewDim;

        /**
         * @param newDimensions
         * @param fillDimension
         *                if true, the newly added dimensions will be filled
         *                with a copy of the existing ones
         */
        public ImgPlusExtendDims(String... newDimensions) {
                m_newDimensions = newDimensions;
                m_isNewDim = new BitSet(newDimensions.length);

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ImgPlus<T> createEmptyOutput(ImgPlus<T> op) {

                AxisType[] axes = new AxisType[op.numDimensions()];
                op.axes(axes);
                m_isNewDim.clear();
                for (int d = 0; d < m_newDimensions.length; d++) {
                        for (int a = 0; a < axes.length; a++) {
                                if (!axes[a].getLabel().equals(
                                                m_newDimensions[d])) {
                                        m_isNewDim.set(d);
                                }
                        }
                }
                long[] newDims = new long[op.numDimensions()
                                + m_isNewDim.cardinality()];
                Arrays.fill(newDims, 1);
                for (int i = 0; i < op.numDimensions(); i++) {
                        newDims[i] = op.dimension(i);
                }
                return new ImgPlus<T>(op.factory().create(newDims,
                                op.firstElement().createVariable()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ImgPlus<T> compute(ImgPlus<T> op, ImgPlus<T> r) {
                Cursor<T> srcCur = op.localizingCursor();
                RandomAccess<T> resRA = r.randomAccess();

                // TODO: Copy metadata!
                r.setName(op.getName());

                for (int d = 0; d < op.numDimensions(); d++) {
                        r.setAxis(Axes.get(op.axis(d).getLabel()), d);
                }

                int d = op.numDimensions();
                for (int i = 0; i < m_newDimensions.length; i++) {
                        if (m_isNewDim.get(i)) {
                                r.setAxis(Axes.get(m_newDimensions[i]), d);
                                d++;
                        }
                }

                while (srcCur.hasNext()) {
                        srcCur.fwd();
                        for (int i = 0; i < op.numDimensions(); i++) {
                                resRA.setPosition(srcCur.getLongPosition(i), i);

                        }
                        resRA.get().set(srcCur.get());
                }

                return r;

        }

        @Override
        public UnaryOutputOperation<ImgPlus<T>, ImgPlus<T>> copy() {
                return new ImgPlusExtendDims<T>(m_newDimensions);
        }

        @Override
        public ImgPlus<T> compute(ImgPlus<T> in) {
                return compute(in, createEmptyOutput(in));
        }

}
