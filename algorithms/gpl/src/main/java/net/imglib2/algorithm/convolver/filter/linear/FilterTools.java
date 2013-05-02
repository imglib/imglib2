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
 *   16 Dec 2010 (hornm): created
 */
package net.imglib2.algorithm.convolver.filter.linear;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * A collection of static methods in support of two-dimensional filters. TODO:
 * Make Operations?!
 * 
 * 
 * @author Roy Liu, hornm
 */
public final class FilterTools {

	private FilterTools() {
		//
	}

	/**
	 * Creates a point support matrix. The top row consists of <tt>x</tt>
	 * coordinates, and the bottom row consists of <tt>y</tt> coordinates. The
	 * points range in a square where the origin is at the center.
	 * 
	 * @param supportRadius
	 *            the support radius.
	 * @return the point support matrix.
	 */
	final public static Img<DoubleType> createPointSupport(int supportRadius) {

		int support = supportRadius * 2 + 1;

		Img<DoubleType> res = new ArrayImgFactory<DoubleType>().create(
				new long[] { 2, support * support }, new DoubleType());

		Cursor<DoubleType> cur = res.localizingCursor();

		while (cur.hasNext()) {
			cur.fwd();
			if (cur.getLongPosition(0) == 0) {
				cur.get().set(
						(cur.getLongPosition(1) / support) - supportRadius);
			} else {
				cur.get().set(
						(cur.getLongPosition(1) % support) - supportRadius);
			}
		}
		return res;
	}

	/**
	 * Creates the <tt>2&#215;2</tt> rotation matrix <br />
	 * <tt>-cos(theta) -sin(theta)</tt> <br />
	 * <tt>-sin(theta) cos(theta)</tt>. <br />
	 * 
	 * @param theta
	 *            the angle of rotation.
	 * @return the rotation matrix.
	 */
	final public static Img<DoubleType> createRotationMatrix(double theta) {

		Img<DoubleType> res = new ArrayImgFactory<DoubleType>().create(
				new long[] { 2, 2 }, new DoubleType());

		RandomAccess2D<DoubleType> ra = new RandomAccess2D<DoubleType>(res);

		ra.get(0, 0).set((float) -Math.cos(theta));
		ra.get(0, 1).set((float) -Math.sin(theta));
		ra.get(1, 0).set((float) -Math.sin(theta));
		ra.get(1, 1).set((float) Math.cos(theta));

		return res;
	}

	final public static <T extends RealType<T> & NativeType<T>> Img<T> reshapeMatrix(
			long stride, Img<T> vector) {

		long yDim = vector.dimension(0) / stride;

		Img<T> res = new ArrayImgFactory<T>().create(
				new long[] { stride, yDim }, vector.firstElement()
						.createVariable());

		Cursor<T> vecCur = vector.localizingCursor();
		RandomAccess<T> resRA = res.randomAccess();

		while (vecCur.hasNext()) {
			vecCur.fwd();
			resRA.setPosition(vecCur.getLongPosition(0) % stride, 1);
			resRA.setPosition(vecCur.getLongPosition(0) / stride, 0);
			resRA.get().set(vecCur.get());
		}
		return res;

	}

	public static <T extends RealType<T> & NativeType<T>> Img<T> getVector(
			Img<T> src, int[] pos, int vectorDim) {

		Img<T> vector = new ArrayImgFactory<T>().create(new long[] { src
				.dimension(vectorDim) }, src.firstElement().createVariable());

		Cursor<T> vecCur = vector.localizingCursor();
		RandomAccess<T> srcRA = src.randomAccess();

		srcRA.setPosition(pos);
		while (vecCur.hasNext()) {
			vecCur.fwd();
			srcRA.setPosition(vecCur.getLongPosition(0), vectorDim);
			vecCur.get().set(srcRA.get());
		}
		return vector;

	}

	private static class RandomAccess2D<T extends RealType<T>> {

		private final RandomAccess<T> m_ra;

		public RandomAccess2D(Img<T> img) {
			m_ra = img.randomAccess();
		}

		public T get(int row, int col) {
			m_ra.setPosition(col, 1);
			m_ra.setPosition(row, 0);
			return m_ra.get();
		}

	}

	public static <T extends RealType<T>> void print2DMatrix(Img<T> img) {
		if (img.numDimensions() < 2) {
			return;
		}
		RandomAccess<T> ra = img.randomAccess();
		for (int x = 0; x < img.dimension(0); x++) {
			System.out.println("");
			ra.setPosition(x, 0);
			for (int y = 0; y < img.dimension(1); y++) {
				ra.setPosition(y, 1);
				System.out.printf(" %+.4f", ra.get().getRealDouble());
			}
		}
	}

}
