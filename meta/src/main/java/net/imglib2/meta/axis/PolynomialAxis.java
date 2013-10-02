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
 * PolynomialAxis is a {@link CalibratedAxis} that scale nonlinearly as a
 * polynomial of degree >= 2. Calibrated values are calculated using the
 * equation {@code y = a + b*x + c*x^2 + ...}.
 * 
 * @author Barry DeZonia
 */
public class PolynomialAxis extends AbstractCalibratedAxis {

	// -- constants --

	private static final String[] VARS = new String[] { "a", "b", "c", "d", "e",
		"f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
		"u", "v", "w", /* skipping x and y */"z" };

	// -- fields --

	private double[] coeffs;

	// -- constructors --

	public PolynomialAxis() {
		this(Axes.unknown(), null, 0, 0, 1);
	}

	public PolynomialAxis(final AxisType type, final String unit,
		final double... coeffs)
	{
		super(type);
		setUnit(unit);
		this.coeffs = coeffs;
		if (coeffs.length < 3) {
			throw new IllegalArgumentException(
				"polynomial axis requires at least 3 coefficients");
		}
		if (coeffs.length > VARS.length) {
			throw new IllegalArgumentException("polynomial axis limited to " +
				VARS.length + " coefficients");
		}
	}

	// -- getters --

	public int order() {
		resize(2);
		return coeffs.length - 1;
	}

	public double coeff(final int i) {
		resize(2);
		if (i < coeffs.length) return coeffs[i];
		return 0;
	}

	// -- setters --

	public void setCoeff(final int i, final double v) {
		if (i >= VARS.length) {
			throw new IllegalArgumentException("polynomial axis limited to " +
				VARS.length + " coefficients");
		}
		if (i >= coeffs.length) resize(i);
		coeffs[i] = v;
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		double term = rawValue;
		double result = coeffs[0];
		for (int i = 1; i < coeffs.length; i++) {
			result += coeffs[i] * term;
			term *= rawValue;
		}
		return result;
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return Double.NaN; // in general polynomial equations are not 1 to 1
	}

	@Override
	public String generalEquation() {
		// String that looks like y = a + b*x + c*x^2 + ...
		int v = 0;
		final StringBuilder builder = new StringBuilder();
		builder.append("y = ");
		for (int i = 0; i < coeffs.length; i++) {
			if (coeffs[i] == 0) continue; // skip terms if possible
			if (i != 0) builder.append(" + ");
			builder.append(VARS[v++]);
			if (i != 0) {
				builder.append("*x");
				if (i > 1) {
					builder.append("^");
					builder.append(i);
				}
			}
		}
		return builder.toString();
	}

	@Override
	public String particularEquation() {
		// String that looks like y = (4.0) + (2.7)*x + (8.9)*x^2 + ...
		final StringBuilder builder = new StringBuilder();
		builder.append("y = (");
		for (int i = 0; i < coeffs.length; i++) {
			if (coeffs[i] == 0) continue; // skip terms if possible
			if (i != 0) builder.append(" + (");
			builder.append(coeffs[i]);
			builder.append(")");
			if (i != 0) {
				builder.append("*x");
				if (i > 1) {
					builder.append("^");
					builder.append(i);
				}
			}
		}
		return builder.toString();
	}

	@Override
	public PolynomialAxis copy() {
		final PolynomialAxis axis = new PolynomialAxis(type(), unit());
		for (int i = 0; i <= order(); i++) {
			axis.setCoeff(i, coeff(i));
		}
		return axis;
	}

	// -- helpers --

	private void resize(final int smallestValidIndex) {
		int lastValid = coeffs.length - 1;
		if (smallestValidIndex >= coeffs.length) {
			lastValid = smallestValidIndex;
		}
		else {
			for (int i = coeffs.length - 1; i > smallestValidIndex; i--) {
				if (coeffs[i] == 0) lastValid--;
				else break;
			}
		}
		if (lastValid != coeffs.length - 1) {
			final double[] newCoeffs = new double[lastValid + 1];
			for (int i = 0; i < newCoeffs.length; i++) {
				newCoeffs[i] = (i >= coeffs.length) ? 0 : coeffs[i];
			}
			coeffs = newCoeffs;
		}
	}

}
