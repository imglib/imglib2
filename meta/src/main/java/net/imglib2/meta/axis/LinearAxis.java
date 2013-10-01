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

package net.imglib2.meta.axis;

import net.imglib2.meta.AbstractCalibratedAxis;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedAxis;

/**
 * LinearAxis is a {@link CalibratedAxis} that scales coordinates along the axis
 * in a linear fashion. Slope and intercept are configurable. Calibrated values
 * calculated from equation y = a + b *x.
 * 
 * @author Barry DeZonia
 */
public class LinearAxis extends AbstractCalibratedAxis {

	// -- fields --

	private double scale, origin;

	// -- constructors --

	/**
	 * Construct a default LinearAxis. Axis type is unknown, unit is null, slope
	 * is 1.0 and intercept is 0.0.
	 */
	public LinearAxis() {
		this(Axes.unknown());
	}

	/**
	 * Construct a LinearAxis of specified scale. Axis type is unknown, unit is
	 * null, slope is the specified scale and intercept is 0.0.
	 */
	public LinearAxis(double scale) {
		this(Axes.unknown(), scale);
	}

	/**
	 * Construct a LinearAxis of specified scale and origin. Axis type is unknown,
	 * unit is null, slope is specified scale and intercept is specified origin.
	 */
	public LinearAxis(double scale, double origin) {
		this(Axes.unknown(), scale, origin);
	}

	/**
	 * Construct a LinearAxis of specified type. Axis type is as specified, unit
	 * is null, slope is 1.0 and intercept is 0.0.
	 */
	public LinearAxis(AxisType type) {
		this(type, 1, 0);
	}

	/**
	 * Construct a LinearAxis of specified type and scale. Axis type is as
	 * specified, unit is null, slope is the specified scale and intercept is 0.0.
	 */
	public LinearAxis(AxisType type, double scale) {
		this(type, scale, 0);
	}

	/**
	 * Construct a LinearAxis of specified type, scale, and origin. Axis type is
	 * as specified, unit is null, slope is the specified scale and intercept is
	 * specified origin.
	 */
	public LinearAxis(AxisType type, double scale, double origin) {
		super(type);
		this.scale = scale;
		this.origin = origin;
	}

	/**
	 * Construct a LinearAxis of specified type and unit. Axis type is as
	 * specified, unit is as specified, slope is 1.0 and intercept is 0.0.
	 */
	public LinearAxis(AxisType type, String unit) {
		this(type, unit, 1, 0);
	}

	/**
	 * Construct a LinearAxis of specified type, unit, and scale. Axis type is as
	 * specified, unit is as specified, slope is the specified scale and intercept
	 * is 0.0.
	 */
	public LinearAxis(AxisType type, String unit, double scale) {
		this(type, unit, scale, 0);
	}

	/**
	 * Construct a LinearAxis of specified type, unit, scale, and origin. Axis
	 * type is as specified, unit is as specified, slope is the specified scale
	 * and intercept is the specified origin.
	 */
	public LinearAxis(AxisType type, String unit, double scale, double origin) {
		super(type);
		setUnit(unit);
		this.scale = scale;
		this.origin = origin;
	}

	// -- static helpers --

	/**
	 * Returns the slope of the line connecting two points.
	 */
	public static double slope(double x1, double y1, double x2, double y2) {
		return (y2 - y1) / (x2 - x1);
	}

	/**
	 * Returns the y intercept of the line connecting two points.
	 */
	public static double intercept(double x1, double y1, double x2, double y2) {
		return (y1 + y2 + (((y1 - y2) * (x1 + x2)) / (x2 - x1))) / 2;
	}

	// -- setters/getters --

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double scale() {
		return scale;
	}

	public void setOrigin(double origin) {
		this.origin = origin;
	}

	public double origin() {
		return origin;
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(double rawValue) {
		return scale * rawValue + origin;
	}

	@Override
	public double rawValue(double calibratedValue) {
		return (calibratedValue - origin) / scale;
	}

	@Override
	public String generalEquation() {
		return "y = a + b*x";
	}

	@Override
	public String particularEquation() {
		return "y = (" + origin + ") + (" + scale + ") * x";
	}

	@Override
	public boolean update(CalibratedAxis other) {
		if (other instanceof LinearAxis) {
			LinearAxis axis = (LinearAxis) other;
			setType(axis.type());
			setUnit(axis.unit());
			setOrigin(axis.origin());
			setScale(axis.scale());
			return true;
		}
		if (other instanceof PolynomialAxis) {
			PolynomialAxis axis = (PolynomialAxis) other;
			if (axis.order() == 2 && axis.coeff(2) == 0) {
				setType(axis.type());
				setUnit(axis.unit());
				setOrigin(axis.coeff(0));
				setScale(axis.coeff(1));
				return true;
			}
		}
		if (other instanceof PowerAxis) {
			PowerAxis axis = (PowerAxis) other;
			if (axis.c() == 0 || axis.c() == 1) {
				setType(axis.type());
				setUnit(axis.unit());
				if (axis.c() == 0) {
					setOrigin(axis.a() + axis.b());
					setScale(0);
				}
				else { // axis.c() == 1
					setOrigin(axis.a());
					setScale(axis.b());
				}
				return true;
			}
		}
		return false;
	}
}
