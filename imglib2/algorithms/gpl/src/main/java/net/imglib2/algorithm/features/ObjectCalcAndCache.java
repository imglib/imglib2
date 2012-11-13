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
 *   Apr 12, 2012 (hornm): created
 */
package net.imglib2.algorithm.features;

import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.operation.SubsetOperations;
import net.imglib2.ops.operation.iterableinterval.unary.MakeCooccurrenceMatrix;
import net.imglib2.ops.operation.subset.views.ImgView;
import net.imglib2.ops.operation.subset.views.SubsetViews;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * 
 * Utility class which calculates caches commonly used objects.
 * 
 * @author hornm, University of Konstanz
 */
public class ObjectCalcAndCache {

	public ObjectCalcAndCache() {
	}

	private Img<BitType> binaryMask;
	private IterableInterval<BitType> bmIterableInterval;

	public Img<BitType> binaryMask(IterableInterval<BitType> ii) {
		if (bmIterableInterval != ii) {
			bmIterableInterval = ii;
			binaryMask = new ArrayImgFactory<BitType>().create(ii,
					new BitType());
			RandomAccess<BitType> maskRA = binaryMask.randomAccess();

			Cursor<BitType> cur = ii.localizingCursor();
			while (cur.hasNext()) {
				cur.fwd();
				for (int d = 0; d < cur.numDimensions(); d++) {
					maskRA.setPosition(cur.getLongPosition(d) - ii.min(d), d);
				}
				maskRA.get().set(true);

			}
		}
		return binaryMask;

	}

	private Img<BitType> binaryMask2D;
	private IterableInterval<BitType> bm2dIterableInterval;

	public Img<BitType> binaryMask2D(final IterableInterval<BitType> ii) {
		if (bm2dIterableInterval != ii) {
			bm2dIterableInterval = ii;
			final long[] dims = new long[ii.numDimensions()];
			ii.dimensions(dims);
			for (int i = 0; i < 2; i++) {
				dims[i] += 2;
			}
			final Img<BitType> mask = new ArrayImgFactory<BitType>().create(
					dims, new BitType());
			final RandomAccess<BitType> maskRA = mask.randomAccess();
			final Cursor<BitType> cur = ii.localizingCursor();
			while (cur.hasNext()) {
				cur.fwd();
				for (int d = 0; d < 2; d++) {
					maskRA.setPosition(cur.getLongPosition(d) - ii.min(d) + 1,
							d);
				}
				maskRA.get().set(true);
			}
			binaryMask2D = new ImgView<BitType>(SubsetOperations.subsetview(
					mask, new FinalInterval(dims)), mask.factory());
		}
		return binaryMask2D;
	}

	private final DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
	private IterableInterval<? extends RealType<?>> dsIterableInterval;

	public <T extends RealType<T>> DescriptiveStatistics descriptiveStatistics(
			IterableInterval<T> ii) {

		if (dsIterableInterval != ii) {
			dsIterableInterval = ii;
			descriptiveStatistics.clear();
			Cursor<T> c = ii.cursor();

			while (c.hasNext()) {
				c.fwd();

				descriptiveStatistics.addValue(c.get().getRealDouble());
			}
		}
		return descriptiveStatistics;

	}

	private double[] centroid;
	private IterableInterval<? extends RealType<?>> cIterableInterval;

	public <T extends RealType<T>> double[] centroid(IterableInterval<T> ii) {
		if (cIterableInterval != ii) {
			cIterableInterval = ii;
			final Cursor<T> c = ii.cursor();
			centroid = new double[ii.numDimensions()];

			long count = 0;
			while (c.hasNext()) {
				c.fwd();
				for (int i = 0; i < centroid.length; i++) {
					centroid[i] += c.getDoublePosition(i);
				}
				count++;
			}

			for (int i = 0; i < centroid.length; i++) {
				centroid[i] /= count;
			}
		}
		return centroid;
	}

	private double[] weightedCentroid;
	private IterableInterval<? extends RealType<?>> wcIterableInterval;

	public <T extends RealType<T>> double[] weightedCentroid(
			IterableInterval<T> ii, DescriptiveStatistics ds,
			int massDisplacement) {

		if (wcIterableInterval != ii) {
			wcIterableInterval = ii;
			weightedCentroid = new double[ii.numDimensions()];
			double[] centroid = new double[ii.numDimensions()];

			Cursor<T> c = ii.localizingCursor();

			long[] pos = new long[c.numDimensions()];
			while (c.hasNext()) {
				c.fwd();
				c.localize(pos);

				double val = c.get().getRealDouble();

				for (int d = 0; d < ii.numDimensions(); d++) {
					weightedCentroid[d] += pos[d] * (val / ds.getSum());
					centroid[d] += pos[d];
				}
			}

			massDisplacement = 0;
			for (int d = 0; d < ii.numDimensions(); d++) {
				// m_weightedCentroid[d] /= m_interval.size();
				centroid[d] /= ii.size();
				massDisplacement += Math.pow(weightedCentroid[d] - centroid[d],
						2);
			}

		}
		return weightedCentroid;

	}

	// private IterableInterval<BitType> sIterableInterval;
	// private Signature signature;
	//
	// public Signature signature(IterableInterval<BitType> ii, int
	// samplingRate) {
	//
	// if (sIterableInterval != ii) {
	// sIterableInterval = ii;
	//
	// double[] centroid = centroid(ii);
	// long[] pos = new long[centroid.length];
	// for (int i = 0; i < pos.length; i++) {
	// pos[i] = Math.round(centroid[i]);
	// }
	//
	// signature = new Signature(binaryMask(ii), pos, samplingRate);
	// }
	//
	// return signature;
	//
	// }

	private CooccurrenceMatrix coocMatrix;
	private IterableInterval<? extends RealType<?>> coocII;
	private int coocDist;
	private int coocGrayLevels;
	private MatrixOrientation coocOrientation;
	private BitSet coocBitSet;

	public <T extends RealType<T>> CooccurrenceMatrix cooccurenceMatrix(
			IterableInterval<T> ii, int dimX, int dimY, int distance,
			int nrGrayLevels, MatrixOrientation matrixOrientation,
			BitSet features) {
		if (coocII != ii || coocDist != distance
				|| coocGrayLevels != nrGrayLevels
				|| coocOrientation != matrixOrientation
				|| !features.equals(coocBitSet)) {
			MakeCooccurrenceMatrix<T> matrixOp = new MakeCooccurrenceMatrix<T>(
					dimX, dimY, distance, nrGrayLevels, matrixOrientation,
					features);
			if (coocMatrix == null || coocGrayLevels != nrGrayLevels) {
				// matrix still null or size must change
				coocMatrix = new CooccurrenceMatrix(nrGrayLevels);
			}
			matrixOp.compute(ii, coocMatrix);
			coocII = ii;
			coocDist = distance;
			coocGrayLevels = nrGrayLevels;
			coocOrientation = matrixOrientation;
			coocBitSet = features;
		}
		return coocMatrix;
	}

}
