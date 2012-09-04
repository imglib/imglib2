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
 *   5 Oct 2011 (hornm): created
 */
package net.imglib2.ops.util.metadata;

import java.util.Arrays;

import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedSpace;

/**
 * 
 * @author hornm, University of Konstanz
 */
public class CalibratedSpaceImpl implements CalibratedSpace {

	private final AxisType[] m_axes;

	private final double[] m_cal;

	public CalibratedSpaceImpl(int numDims) {
		m_axes = new AxisType[numDims];
		Arrays.fill(m_axes, Axes.UNKNOWN);
		m_cal = new double[numDims];
	}

	public CalibratedSpaceImpl(String... axisLabels) {
		m_axes = new AxisType[axisLabels.length];
		for (int i = 0; i < m_axes.length; i++) {
			m_axes[i] = Axes.get(axisLabels[i]);
		}
		m_cal = new double[axisLabels.length];
	}

	public CalibratedSpaceImpl(AxisType[] axes, double[] calibration) {
		m_axes = axes;
		m_cal = calibration;
	}

	public CalibratedSpaceImpl(CalibratedSpace axes) {
		m_axes = new AxisType[axes.numDimensions()];
		m_cal = new double[axes.numDimensions()];
		axes.axes(m_axes);
		axes.calibration(m_cal);

	}

	public CalibratedSpaceImpl(String[] axisLabels, double[] calibration) {
		m_axes = new AxisType[axisLabels.length];
		m_cal = calibration.clone();

		for (int d = 0; d < axisLabels.length; d++) {
			m_axes[d] = Axes.get(axisLabels[d]);
		}
	}

	@Override
	public int getAxisIndex(final AxisType axis) {
		for (int i = 0; i < m_axes.length; i++) {
			if (m_axes[i].getLabel().equals(axis.getLabel()))
				return i;
		}
		return -1;
	}

	@Override
	public AxisType axis(final int d) {
		return m_axes[d];
	}

	@Override
	public void axes(final AxisType[] target) {
		for (int i = 0; i < m_axes.length; i++)
			target[i] = m_axes[i];
	}

	@Override
	public void setAxis(final AxisType axis, final int d) {
		m_axes[d] = axis;
	}

	@Override
	public double calibration(final int d) {
		return m_cal[d];
	}

	@Override
	public void calibration(final double[] target) {
		for (int i = 0; i < target.length; i++)
			target[i] = m_cal[i];
	}

	@Override
	public void setCalibration(final double value, final int d) {
		m_cal[d] = value;
	}

	@Override
	public int numDimensions() {
		return m_axes.length;
	}

	@Override
	public void calibration(float[] cal) {
		for (int d = 0; d < cal.length; d++)
			cal[d] = (float) m_cal[d];

	}

	@Override
	public void setCalibration(double[] cal) {
		for (int d = 0; d < cal.length; d++)
			m_cal[d] = cal[d];

	}

	@Override
	public void setCalibration(float[] cal) {
		for (int d = 0; d < cal.length; d++)
			m_cal[d] = cal[d];
	}

}
