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

import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.metadata.unary.CopyCalibratedSpace;
import net.imglib2.ops.operation.metadata.unary.CopyImageMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyNamed;
import net.imglib2.ops.operation.metadata.unary.CopySourced;
import net.imglib2.type.Type;

/**
 * Reduces the dimensions of an image by removing all dimensions having only one
 * pixel.
 * 
 * @author hornm, dietzc University of Konstanz
 */
public class ImgPlusRemove1Dims<T extends Type<T>> implements
                UnaryOutputOperation<ImgPlus<T>, ImgPlus<T>> {

        /**
         * {@inheritDoc}
         */
        @Override
        public ImgPlus<T> createEmptyOutput(ImgPlus<T> op) {

                BitSet isLength1 = new BitSet(op.numDimensions());
                for (int d = 0; d < op.numDimensions(); d++) {
                        if (op.dimension(d) == 1) {
                                isLength1.set(d);
                        }
                }

                long[] min = new long[op.numDimensions()
                                - isLength1.cardinality()];
                long[] max = new long[min.length];

                int d = 0;
                for (int i = 0; i < op.numDimensions(); i++) {
                        if (!isLength1.get(i)) {
                                max[d] = op.dimension(i) - 1;
                                d++;
                        }
                }
                Img<T> res = op.factory().create(new FinalInterval(min, max),
                                op.firstElement().createVariable());
                return new ImgPlus<T>(res);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public ImgPlus<T> compute(ImgPlus<T> op, ImgPlus<T> r) {
                Cursor<T> srcCur = op.localizingCursor();
                RandomAccess<T> resRA = r.randomAccess();

                new CopyMetadata(new CopyNamed(), new CopyImageMetadata(),
                                new CopySourced(), new CopyCalibratedSpace(r))
                                .compute(op, r);

                BitSet isLength1 = new BitSet(op.numDimensions());
                for (int d = 0; d < op.numDimensions(); d++) {
                        if (op.dimension(d) == 1) {
                                isLength1.set(d);
                        }
                }

                int d;
                while (srcCur.hasNext()) {
                        srcCur.fwd();
                        d = 0;
                        for (int i = 0; i < op.numDimensions(); i++) {
                                if (!isLength1.get(i)) {
                                        resRA.setPosition(srcCur
                                                        .getLongPosition(i), d);
                                        d++;
                                }
                        }
                        resRA.get().set(srcCur.get());

                }
                return r;
        }

        @Override
        public UnaryOutputOperation<ImgPlus<T>, ImgPlus<T>> copy() {
                return new ImgPlusRemove1Dims<T>();
        }

        @Override
        public ImgPlus<T> compute(ImgPlus<T> op) {
                return compute(op, createEmptyOutput(op));
        }

}
