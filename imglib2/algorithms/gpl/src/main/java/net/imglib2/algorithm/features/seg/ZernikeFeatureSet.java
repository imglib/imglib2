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
 *   22 Sep 2010 (hornm): created
 */
package net.imglib2.algorithm.features.seg;

import net.imglib2.IterableInterval;
import net.imglib2.algorithm.features.FeatureSet;
import net.imglib2.algorithm.features.FeatureTargetListener;
import net.imglib2.algorithm.features.zernike.ZernikeFeatureComputer;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author hornm, dietzc University of Konstanz
 * @param <T>
 *            image type
 */
public class ZernikeFeatureSet<T extends RealType<T>> implements FeatureSet {

	private ZernikeFeatureComputer<T> m_comp = null;

	private final String[] m_names;

	private final int m_order;

	private IterableInterval<T> m_interval;

	private final boolean m_calcMagnitude;

	/**
	 * @param order
	 */
	public ZernikeFeatureSet(int order, boolean calcMagnitude) {
		super();
		m_order = order;
		m_calcMagnitude = calcMagnitude;
		int countFeatures = ZernikeFeatureComputer.countZernikeMoments(order);
		if (!calcMagnitude) {
			countFeatures *= 2;
		}
		m_names = new String[countFeatures];
		for (int j = 0; j < countFeatures; ++j) {
			int newOrder = ZernikeFeatureComputer.giveZernikeOrder(order, j);
			int rep = ZernikeFeatureComputer.giveZernikeRepetition(order, j);

			if (!calcMagnitude) {
				m_names[j] = "ZernikeReal[order=" + newOrder + ";rep=" + rep
						+ "]";
				j++;
				m_names[j++] = "ZernikeComplex[order=" + newOrder + ";rep="
						+ rep + "]";
			} else {
				m_names[j] = "ZernikeMagn[order=" + newOrder + ";rep=" + rep
						+ "]";
			}
		}

	}

	@FeatureTargetListener
	public final void iiUpdated(IterableInterval<T> interval) {
		m_interval = interval;
		m_comp = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double value(int id) {

		if (m_calcMagnitude) {
			// gives the magnitued of the result
			ZernikeFeatureComputer.Complex res = getComplexFeatureValue(id);
			return Math.sqrt(res.getImaginary() * res.getImaginary()
					+ res.getReal() * res.getReal());
		} else {
			// TODO: cache the complex result!!
			ZernikeFeatureComputer.Complex res = getComplexFeatureValue(id / 2);
			if (id % 2 == 0) {
				return res.getReal();
			} else {
				return res.getImaginary();
			}
		}
	}

	/**
	 * @return the complex feature value
	 */
	public ZernikeFeatureComputer.Complex getComplexFeatureValue(int id) {
		if (m_comp == null) {
			m_comp = new ZernikeFeatureComputer<T>(m_interval);
		}
		ZernikeFeatureComputer.Complex result = m_comp.computeZernikeMoment(
				ZernikeFeatureComputer.giveZernikeOrder(m_order, id),
				ZernikeFeatureComputer.giveZernikeRepetition(m_order, id));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name(int id) {
		return m_names[id];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enable(int id) {
		// nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numFeatures() {
		return m_names.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String featureSetId() {
		return "Zernike Feature Factory";
	}

}
