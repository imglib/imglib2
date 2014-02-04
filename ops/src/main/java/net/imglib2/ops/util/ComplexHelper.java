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

package net.imglib2.ops.util;

import net.imglib2.type.numeric.ComplexType;

/**
 * Static utility class for implementing methods common to complex types.
 * These should make their way into Imglib classes when possible.
 * 
 * @author Barry DeZonia
 */
public class ComplexHelper {
	
	private ComplexHelper() {
		// utility class - do not instantiate
	}
	
	/**
	 * Gets the modulus (magnitude, radius, r, etc.) of a given complex number
	 */
	public static double getModulus(ComplexType<?> z) {
		return Math.sqrt(getModulus2(z));
	}

	/**
	 * Gets the square of the modulus (magnitude, radius, r, etc.) of a given
	 * complex number
	 */
	public static double getModulus2(ComplexType<?> z) {
		return
			z.getRealDouble()*z.getRealDouble() +
			z.getImaginaryDouble()*z.getImaginaryDouble();
	}
	
	/**
	 * Gets the argument (angle, theta, etc.) of a given complex number
	 */
	public static double getArgument(ComplexType<?> z) {
		double x = z.getRealDouble();
		double y = z.getImaginaryDouble();
		double theta;
		if (x == 0) {
			if (y > 0)
				theta = Math.PI / 2;
			else if (y < 0)
				theta = -Math.PI / 2;
			else // y == 0 : theta indeterminate
				theta = Double.NaN;
		}
		else if (y == 0) {
			if (x > 0)
				theta = 0;
			else // (x < 0)
				theta = Math.PI;
		}
		else // x && y both != 0
			theta = Math.atan2(y,x);
		
		return theta;
	}

	/**
	 * Normalizes an angle in radians to -Math.PI < angle <= Math.PI
	 */
	public static double getPrincipleArgument(double angle) {
		double arg = angle;
		while (arg <= -Math.PI) arg += 2*Math.PI;
		while (arg > Math.PI) arg -= 2*Math.PI;
		return arg;
	}

	/**
	 * Sets the value of a complex number to a given (r,theta) combination
	 */
	public static void setPolar(ComplexType<?> z, double r, double theta) {
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		z.setComplexNumber(x, y);
	}
}
