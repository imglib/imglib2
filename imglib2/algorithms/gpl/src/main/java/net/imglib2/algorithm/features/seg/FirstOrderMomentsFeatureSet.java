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
 *   20 Sep 2010 (hornm): created
 */
package net.imglib2.algorithm.features.seg;

import net.imglib2.IterableInterval;
import net.imglib2.algorithm.features.FeatureSet;
import net.imglib2.algorithm.features.FeatureTargetListener;
import net.imglib2.algorithm.features.ObjectCalcAndCache;
import net.imglib2.algorithm.features.SharesObjects;
import net.imglib2.ops.operation.iterableinterval.unary.Centroid;
import net.imglib2.type.numeric.RealType;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * 
 * @author dietzc, hornm, schoenenbergerf University of Konstanz
 * @param <T>
 *            image type
 */
public class FirstOrderMomentsFeatureSet<T extends RealType<T>> implements
		FeatureSet, SharesObjects {

	/**
	 * the feature names
	 */
	public static final String[] FEATURES = new String[] { "Min", "Max",
			"Mean", "Geometric Mean", "Sum", "Squares of Sum", "Std Dev",
			"Variance", "Skewness", "Kurtosis", "Quantil 25", "Quantil 50",
			"Quantil 75", "Median absolute deviation (MAD)",
			"WeightedCentroid Dim 1", "WeightedCentroid Dim 2",
			"WeightedCentroid Dim 3", "WeightedCentroid Dim 4",
			"WeightedCentroid Dim 5", "Mass Displacement", };

	private DescriptiveStatistics m_statistics;

	private IterableInterval<T> m_interval;

	private double[] m_weightedCentroid;

	private int m_massDisplacement;

	private ObjectCalcAndCache m_ocac;

	@FeatureTargetListener
	public final void iiUpdated(IterableInterval<T> interval) {
		m_interval = interval;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double value(int id) {

		m_statistics = m_ocac.descriptiveStatistics(m_interval);

		if (id > 12 && id <= 19) {
			// update weighted centroid
			m_weightedCentroid = m_ocac.weightedCentroid(m_interval,
					m_statistics, m_massDisplacement);
		}

		switch (id) {
		case 0:
			return m_statistics.getMin();
		case 1:
			return m_statistics.getMax();
		case 2:
			return m_statistics.getMean();
		case 3:
			return m_statistics.getGeometricMean();
		case 4:
			return m_statistics.getSum();
		case 5:
			return m_statistics.getSumsq();
		case 6:
			return m_statistics.getStandardDeviation();
		case 7:
			return m_statistics.getVariance();
		case 8:
			return m_statistics.getSkewness();
		case 9:
			return m_statistics.getKurtosis();
		case 10:
			return m_statistics.getPercentile(25);
		case 11:
			return m_statistics.getPercentile(50);
		case 12:
			return m_statistics.getPercentile(75);
		case 13:
			double median = m_statistics.getPercentile(50);
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int i = 0; i < m_statistics.getN(); i++) {
				stats.addValue(Math.abs(median - m_statistics.getElement(i)));
			}
			return stats.getPercentile(50);
		case 14:
			return m_weightedCentroid.length > 0 ? m_weightedCentroid[0] : 0;
		case 15:
			return m_weightedCentroid.length > 1 ? m_weightedCentroid[1] : 0;
		case 16:
			return m_weightedCentroid.length > 2 ? m_weightedCentroid[2] : 0;
		case 17:
			return m_weightedCentroid.length > 3 ? m_weightedCentroid[3] : 0;
		case 18:
			return m_weightedCentroid.length > 4 ? m_weightedCentroid[4] : 0;
		case 19:
			m_massDisplacement = 0;
			double[] centroid = new Centroid().compute(m_interval,
					new double[m_interval.numDimensions()]);
			for (int d = 0; d < m_interval.numDimensions(); d++) {
				m_weightedCentroid[d] /= m_statistics.getSum();
				// m_weightedCentroid[d] /= m_interval.size();
				centroid[d] /= m_interval.size();
				m_massDisplacement += Math.pow(m_weightedCentroid[d]
						- centroid[d], 2);
			}

			return Math.sqrt(m_massDisplacement);

		default:
			return Double.NaN;

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name(int id) {
		return FEATURES[id];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enable(int id) {
		// nothing to do

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numFeatures() {
		return FEATURES.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String featureSetId() {
		return "First Order Moments";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getSharedObjectClasses() {
		return new Class[] { ObjectCalcAndCache.class };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSharedObjectInstances(Object[] instances) {
		m_ocac = (ObjectCalcAndCache) instances[0];

	}

}
