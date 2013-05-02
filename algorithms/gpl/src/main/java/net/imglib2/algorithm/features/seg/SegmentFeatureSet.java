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

import java.util.ArrayList;
import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.algorithm.features.FeatureSet;
import net.imglib2.algorithm.features.FeatureTargetListener;
import net.imglib2.algorithm.features.ObjectCalcAndCache;
import net.imglib2.algorithm.features.SharesObjects;
import net.imglib2.img.Img;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.ops.operation.Operations;
import net.imglib2.ops.operation.iterableinterval.unary.CalculateDiameter;
import net.imglib2.ops.operation.randomaccessibleinterval.unary.ConvexHull2D;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * 
 * Calculates features iterable intervals (size, centroid, weighted centroid,
 * circularity...)
 * 
 * @author hornm, dietzc, University of Konstanz
 */
public class SegmentFeatureSet implements FeatureSet, SharesObjects {

	private double[] m_centroid = null;

	/**
	 * feature names
	 */
	public final String[] FEATURES;

	private final ExtractOutlineImg m_outlineOp;

	private IterableInterval<BitType> m_interval;

	private Img<BitType> m_outline;

	private final CalculatePerimeter m_calculatePerimeter;

	private final ConvexHull2D<Img<BitType>> m_convexityOp;

	private final CalculateDiameter m_calculateDiameter;

	private double m_perimeter;

	private double m_solidity;

	private double m_circularity;

	private double m_diameter;

	private final BitSet m_enabled = new BitSet();

	private ObjectCalcAndCache m_ocac;

	private CalibratedSpace m_cs;

	private final AxisType[] m_defaultAxis;

	// private CalibratedSpace m_cs;

	/**
	 * @param target
	 */
	public SegmentFeatureSet(AxisType[] defaultAxes) {
		super();
		m_calculatePerimeter = new CalculatePerimeter();
		m_outlineOp = new ExtractOutlineImg(false);
		m_convexityOp = new ConvexHull2D<Img<BitType>>(0, 1, false);
		m_calculateDiameter = new CalculateDiameter();
		m_defaultAxis = defaultAxes;
		FEATURES = getFeatures(defaultAxes);
	}

	public static String[] getFeatures(AxisType[] defaultAxes) {

		ArrayList<String> features = new ArrayList<String>();

		for (AxisType type : defaultAxes) {
			features.add("Centroid " + type.getLabel());
		}

		features.add("Num Pix");
		features.add("Circularity");
		features.add("Perimeter");
		features.add("Convexity");
		features.add("Extend");
		features.add("Diameter");

		for (AxisType type : defaultAxes) {
			features.add("Dimension " + type.getLabel());
		}

		return features.toArray(new String[features.size()]);
	}

	@FeatureTargetListener
	public void calibratedSpaceUpdated(CalibratedSpace cs) {
		m_cs = cs;
	}

	@FeatureTargetListener
	public void iiUpdated(IterableInterval<BitType> interval) {
		m_interval = interval;
		m_centroid = null;

		int activeDims = 0;
		for (int d = 0; d < interval.numDimensions(); d++) {
			if (interval.dimension(d) > 1) {
				activeDims++;
			}
		}

		if (m_enabled.get(m_defaultAxis.length + 1)
				|| m_enabled.get(m_defaultAxis.length + 2)
				|| m_enabled.get(m_defaultAxis.length + 3)
				|| m_enabled.get(m_defaultAxis.length + 5)) {
			if (activeDims > 2) {
				m_solidity = Double.NaN;
				m_perimeter = Double.NaN;
				m_circularity = Double.NaN;
				m_diameter = Double.NaN;
			} else {

				final Img<BitType> bitMask = m_ocac.binaryMask2D(interval);
				m_outline = m_outlineOp.compute(bitMask, bitMask.factory()
						.create(bitMask, new BitType()));
				m_perimeter = Operations.compute(m_calculatePerimeter,
						m_outline).get();

				if (m_enabled.get(1 + m_defaultAxis.length)
						|| m_enabled.get(3 + m_defaultAxis.length)) {
					m_convexityOp.compute(bitMask, bitMask);
					final Cursor<BitType> convexBitMaskCursor = bitMask
							.cursor();

					double ctr = 0;
					while (convexBitMaskCursor.hasNext()) {
						convexBitMaskCursor.fwd();
						ctr += convexBitMaskCursor.get().get() ? 1 : 0;
					}

					m_circularity = (4d * Math.PI * m_interval.size())
							/ Math.pow(m_perimeter, 2);
					m_solidity = interval.size() / ctr;
				}

				if (m_enabled.get(5 + m_defaultAxis.length)) {
					m_diameter = m_calculateDiameter.compute(m_outline,
							new DoubleType()).get();
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double value(int id) {

		if (id < m_defaultAxis.length) {
			int idx = m_cs.getAxisIndex(m_defaultAxis[id]);
			if (idx != -1) {
				m_centroid = m_ocac.centroid(m_interval);
				return m_centroid[idx];
			} else {
				return 0;
			}
		}

		if (id > m_defaultAxis.length + 5) {
			int idx = m_cs.getAxisIndex(m_defaultAxis[id - m_defaultAxis.length
					- 6]);
			if (idx != -1) {
				return m_interval.dimension(idx);
			} else {
				return 0;
			}
		}

		id -= m_defaultAxis.length;
		switch (id) {
		case 0:
			return m_interval.size();
		case 1:
			return m_circularity;
		case 2:
			return m_perimeter;
		case 3:
			return m_solidity;
		case 4:
			double numPixBB = 1;
			for (int d = 0; d < m_interval.numDimensions(); d++) {
				numPixBB *= m_interval.dimension(d);
			}
			return m_interval.size() / numPixBB;

		case 5:
			return m_diameter;
		default:
			return 0;
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
		m_enabled.set(id);

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
		return "Segment Feature Factory";
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
