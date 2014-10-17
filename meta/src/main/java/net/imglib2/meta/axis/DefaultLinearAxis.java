/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.meta.axis;

import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;

/**
 * Default implementation of {@link LinearAxis}.
 * 
 * @author Barry DeZonia
 */
public class DefaultLinearAxis extends Variable2Axis implements LinearAxis {

	// -- constructors --

	/**
	 * Construct a default LinearAxis. Axis type is unknown, unit is null, slope
	 * is 1.0 and intercept is 0.0.
	 */
	public DefaultLinearAxis() {
		this(Axes.unknown());
	}

	/**
	 * Construct a LinearAxis of specified scale. Axis type is unknown, unit is
	 * null, slope is the specified scale and intercept is 0.0.
	 */
	public DefaultLinearAxis(final double scale) {
		this(Axes.unknown(), scale);
	}

	/**
	 * Construct a LinearAxis of specified scale and origin. Axis type is unknown,
	 * unit is null, slope is specified scale and intercept is specified origin.
	 */
	public DefaultLinearAxis(final double scale, final double origin) {
		this(Axes.unknown(), scale, origin);
	}

	/**
	 * Construct a LinearAxis of specified type. Axis type is as specified, unit
	 * is null, slope is 1.0 and intercept is 0.0.
	 */
	public DefaultLinearAxis(final AxisType type) {
		this(type, 1, 0);
	}

	/**
	 * Construct a LinearAxis of specified type and scale. Axis type is as
	 * specified, unit is null, slope is the specified scale and intercept is 0.0.
	 */
	public DefaultLinearAxis(final AxisType type, final double scale) {
		this(type, scale, 0);
	}

	/**
	 * Construct a LinearAxis of specified type, scale, and origin. Axis type is
	 * as specified, unit is null, slope is the specified scale and intercept is
	 * specified origin.
	 */
	public DefaultLinearAxis(final AxisType type, final double scale, final double origin)
	{
		super(type);
		setScale(scale);
		setOrigin(origin);
	}

	/**
	 * Construct a LinearAxis of specified type and unit. Axis type is as
	 * specified, unit is as specified, slope is 1.0 and intercept is 0.0.
	 */
	public DefaultLinearAxis(final AxisType type, final String unit) {
		this(type, unit, 1, 0);
	}

	/**
	 * Construct a LinearAxis of specified type, unit, and scale. Axis type is as
	 * specified, unit is as specified, slope is the specified scale and intercept
	 * is 0.0.
	 */
	public DefaultLinearAxis(final AxisType type, final String unit, final double scale)
	{
		this(type, unit, scale, 0);
	}

	/**
	 * Construct a LinearAxis of specified type, unit, scale, and origin. Axis
	 * type is as specified, unit is as specified, slope is the specified scale
	 * and intercept is the specified origin.
	 */
	public DefaultLinearAxis(final AxisType type, final String unit, final double scale,
		final double origin)
	{
		super(type);
		setUnit(unit);
		setScale(scale);
		setOrigin(origin);
	}

	// -- static helpers --

	/**
	 * Returns the slope of the line connecting two points.
	 */
	public static double slope(final double x1, final double y1, final double x2,
		final double y2)
	{
		return (y2 - y1) / (x2 - x1);
	}

	/**
	 * Returns the y intercept of the line connecting two points.
	 */
	public static double intercept(final double x1, final double y1,
		final double x2, final double y2)
	{
		return (y1 + y2 + (y1 - y2) * (x1 + x2) / (x2 - x1)) / 2;
	}

	// -- setters/getters --

	@Override
	public void setScale(final double scale) {
		setB(scale);
	}

	@Override
	public double scale() {
		return b();
	}

	@Override
	public void setOrigin(final double origin) {
		setA(origin);
	}

	@Override
	public double origin() {
		return a();
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		return scale() * rawValue + origin();
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return (calibratedValue - origin()) / scale();
	}

	@Override
	public String generalEquation() {
		return "y = a + b*x";
	}

	@Override
	public DefaultLinearAxis copy() {
		return new DefaultLinearAxis(type(), unit(), scale(), origin());
	}

}
