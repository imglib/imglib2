/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.meta;

import net.imglib2.meta.axis.DefaultLinearAxis;

/**
 * Simple, default {@link CalibratedAxis} implementation.
 * 
 * @author Curtis Rueden
 * @deprecated Use {@link DefaultLinearAxis}, or one of the axis types from the
 *             {@link net.imglib2.meta.axis} package, instead.
 */
@Deprecated
public class DefaultCalibratedAxis extends DefaultTypedAxis implements
	CalibratedAxis
{

	private String unit;
	private double cal;

	public DefaultCalibratedAxis() {
		this(Axes.unknown());
	}

	public DefaultCalibratedAxis(final AxisType type) {
		this(type, null, Double.NaN);
	}

	public DefaultCalibratedAxis(final AxisType type, final String unit,
		final double cal)
	{
		super(type);
		setUnit(unit);
		setCalibration(cal);
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibration() {
		return cal;
	}

	@Override
	public void setCalibration(final double cal) {
		this.cal = cal;
	}

	@Override
	public String unit() {
		return unit;
	}

	@Override
	public void setUnit(final String unit) {
		this.unit = unit;
	}

	@Override
	public double calibratedValue(final double rawValue) {
		return rawValue * calibration();
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return calibratedValue / calibration();
	}

	@Override
	public String generalEquation() {
		return "y = a*x";
	}

	@Override
	public String particularEquation() {
		return "y = " + calibration() + "*x";
	}

	@Override
	public double averageScale(final double rawValue1, final double rawValue2) {
		return calibration();
	}

	@Override
	public DefaultCalibratedAxis copy() {
		return new DefaultCalibratedAxis(type(), unit(), calibration());
	}

}
