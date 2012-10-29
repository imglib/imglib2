/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.util.metadata;

import java.util.Arrays;

import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedSpace;

/**
 * @author Martin Horn (University of Konstanz)
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
