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

import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedAxis;

/**
 * PowerAxis is a {@link CalibratedAxis } that scales raw values by the equation
 * y = a + b*x^c.
 * 
 * @author Barry DeZonia
 */
public class PowerAxis extends AbstractThreeVariableAxis {

	// -- constructors --

	public PowerAxis(double power) {
		this(Axes.unknown(), power);
	}

	public PowerAxis(AxisType type, double power) {
		this(type, null, 0, 1, power);
	}

	public PowerAxis(AxisType type, String unit, double a, double b, double c) {
		super(type);
		setUnit(unit);
		this.a = a;
		this.b = b;
		this.c = c;
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(double rawValue) {
		return a + b * Math.pow(rawValue, c);
	}

	@Override
	public double rawValue(double calibratedValue) {
		return Math.pow(((calibratedValue - a) / b), (1.0 / c));
	}

	@Override
	public String generalEquation() {
		return "y = a + b*x^c";
	}

	@Override
	public String particularEquation() {
		return "y = (" + a + ") + (" + b + ")*x^(" + c + ")";
	}

	@Override
	public boolean update(CalibratedAxis other) {
		if (other instanceof PowerAxis) {
			PowerAxis axis = (PowerAxis) other;
			setType(axis.type());
			setUnit(axis.unit());
			setA(axis.a());
			setB(axis.b());
			setC(axis.c());
			return true;
		}
		if (other instanceof LinearAxis) {
			LinearAxis axis = (LinearAxis) other;
			setType(axis.type());
			setUnit(axis.unit());
			setA(axis.origin());
			setB(axis.scale());
			setC(1);
			return true;
		}
		if (other instanceof PolynomialAxis) {
			PolynomialAxis axis = (PolynomialAxis) other;
			int power = compatiblePolynomial(axis);
			if (power >= 0) {
				setType(axis.type());
				setUnit(axis.unit());
				setA(axis.coeff(0));
				setB(axis.coeff(power));
				setC(power);
				return true;
			}
		}
		return false;
	}

	// -- helpers --

	private int compatiblePolynomial(PolynomialAxis axis) {
		int order = axis.order();
		for (int i = 1; i < order; i++) {
			if (axis.coeff(i) != 0) return -1;
		}
		return order;
	}
}
