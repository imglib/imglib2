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

package net.imglib2.type.numeric.complex;

import java.util.ArrayList;

import net.imglib2.type.numeric.ComplexType;

/**
 * Complex math calculator modeled after Java Math class.
 * 
 * @author Barry DeZonia
 */
public class ComplexMath {

	// TODO
	// to be future proof this class should calculate in a BigDecimal based
	// complex type.
	  
	// -- constants --

	private static final ComplexDoubleType I = new ComplexDoubleType(0,1);
	private static final ComplexDoubleType MINUS_I = new ComplexDoubleType(0,-1);
	private static final ComplexDoubleType MINUS_I_OVER_TWO =
		new ComplexDoubleType(0, -0.5);
	private static final ComplexDoubleType MINUS_ONE = new ComplexDoubleType(-1,
		0);
	private static final ComplexDoubleType ONE = new ComplexDoubleType(1, 0);
	private static final ComplexDoubleType ONE_HALF = new ComplexDoubleType(0.5,
		0);
	private static final ComplexDoubleType TWO = new ComplexDoubleType(2, 0);
	private static final ComplexDoubleType TWO_I = new ComplexDoubleType(0, 2);

	// -- static variables --

	@SuppressWarnings("synthetic-access")
	private static final TempManager temps = new TempManager();

	private static final Object lock = new Object();

	// -- public methods --

	// TODO - deprecate OPS' ComplexHelper class

	/**
	 * Computes the log of a given complex number and places it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		log(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		// Note - this uses the formula for the log of the principal branch.

		double modulus = getModulus(z);
		double argument = getArgument(z);
		double x = Math.log(modulus);
		double y = getPrincipleArgument(argument);
		result.setComplexNumber(x, y);
	}

	/**
	 * Computes the log base b of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param b The log base
	 * @param result The result
	 */
	public static void logBase(ComplexType<?> z, ComplexType<?> b,
		ComplexType<?> result)
	{
		// No reference

		ComplexDoubleType numer;
		ComplexDoubleType denom;

		synchronized (lock) {
			numer = temps.get();
			denom = temps.get();
		}

		log(z, numer);
		log(b, denom);
		div(numer, denom, result);

		synchronized (lock) {
			temps.free(numer);
			temps.free(denom);
		}
	}

	/**
	 * Computes the exp of a given complex number and places it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		exp(ComplexType<?> z, ComplexType<?> result)
	{
		// Complex Variables and Applications, Brown and Churchill, 7th edition

		double constant = Math.exp(z.getRealDouble());
		double x = constant * Math.cos(z.getImaginaryDouble());
		double y = constant * Math.sin(z.getImaginaryDouble());
		result.setComplexNumber(x, y);
	}

	/**
	 * Computes the square root of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		sqrt(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		double power = 2.0;
		double modulus = getModulus(z);
		double argument = getArgument(z);
		double princpArg = getPrincipleArgument(argument);
		double r = Math.pow(modulus, 1 / power);
		double theta = princpArg / power;
		setPolar(result, r, theta);
	}

	// TODO - test I didn't reverse inputs. Try pow(2,3) ?= 8
	/**
	 * Computes raising a given complex number to a given power and places it in a
	 * result.
	 * 
	 * @param a The input number
	 * @param z The power
	 * @param result The result
	 */
	public static void pow(ComplexType<?> a, ComplexType<?> z,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType logA;
		ComplexDoubleType zLogA;

		synchronized (lock) {
			logA = temps.get();
			zLogA = temps.get();
		}

		log(a, logA);
		mul(z, logA, zLogA);
		exp(zLogA, result);

		synchronized (lock) {
			temps.free(logA);
			temps.free(zLogA);
		}
	}

	/**
	 * Computes the sine of a given complex number and places it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		sin(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType IZ;
		ComplexDoubleType minusIZ;
		ComplexDoubleType expIZ;
		ComplexDoubleType expMinusIZ;
		ComplexDoubleType diff;

		synchronized (lock) {
			IZ = temps.get();
			minusIZ = temps.get();
			expIZ = temps.get();
			expMinusIZ = temps.get();
			diff = temps.get();
		}

		mul(z, I, IZ);
		mul(z, MINUS_I, minusIZ);
		exp(IZ, expIZ);
		exp(minusIZ, expMinusIZ);
		sub(expIZ, expMinusIZ, diff);
		div(diff, TWO_I, result);

		synchronized (lock) {
			temps.free(IZ);
			temps.free(minusIZ);
			temps.free(expIZ);
			temps.free(expMinusIZ);
			temps.free(diff);
		}
	}

	/**
	 * Computes the cosine of a given complex number and places it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		cos(ComplexType<?> z, ComplexType<?> result)
 {
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType IZ;
		ComplexDoubleType minusIZ;
		ComplexDoubleType expIZ;
		ComplexDoubleType expMinusIZ;
		ComplexDoubleType sum;

		synchronized (lock) {
			IZ = temps.get();
			minusIZ = temps.get();
			expIZ = temps.get();
			expMinusIZ = temps.get();
			sum = temps.get();
		}

		mul(z, I, IZ);
		mul(z, MINUS_I, minusIZ);
		exp(IZ, expIZ);
		exp(minusIZ, expMinusIZ);
		add(expIZ, expMinusIZ, sum);
		div(sum, TWO, result);

		synchronized (lock) {
			temps.free(IZ);
			temps.free(minusIZ);
			temps.free(expIZ);
			temps.free(expMinusIZ);
			temps.free(sum);
		}
	}

	/**
	 * Computes the tangent of a given complex number and places it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		tan(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType sin;
		ComplexDoubleType cos;

		synchronized (lock) {
			sin = temps.get();
			cos = temps.get();
		}

		sin(z, sin);
		cos(z, cos);
		div(sin, cos, result);

		synchronized (lock) {
			temps.free(sin);
			temps.free(cos);
		}
	}

	/**
	 * Computes the inverse sine of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		asin(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType iz;
		ComplexDoubleType zSquared;
		ComplexDoubleType miniSum;
		ComplexDoubleType root;
		ComplexDoubleType sum;
		ComplexDoubleType logSum;

		synchronized (lock) {
			iz = temps.get();
			zSquared = temps.get();
			miniSum = temps.get();
			root = temps.get();
			sum = temps.get();
			logSum = temps.get();
		}

		mul(I, z, iz);
		mul(z, z, zSquared);
		sub(ONE, zSquared, miniSum);
		pow(miniSum, ONE_HALF, root);
		add(iz, root, sum);
		log(sum, logSum);
		mul(MINUS_I, logSum, result);

		synchronized (lock) {
			temps.free(iz);
			temps.free(zSquared);
			temps.free(miniSum);
			temps.free(root);
			temps.free(sum);
			temps.free(logSum);
		}
	}

	/**
	 * Computes the inverse cosine of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		acos(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType zSquared;
		ComplexDoubleType miniSum;
		ComplexDoubleType root;
		ComplexDoubleType sum;
		ComplexDoubleType logSum;

		synchronized (lock) {
			zSquared = temps.get();
			miniSum = temps.get();
			root = temps.get();
			sum = temps.get();
			logSum = temps.get();
		}

		mul(z, z, zSquared);
		sub(zSquared, ONE, miniSum);
		pow(miniSum, ONE_HALF, root);
		add(z, root, sum);
		log(sum, logSum);
		mul(MINUS_I, logSum, result);

		synchronized (lock) {
			temps.free(zSquared);
			temps.free(miniSum);
			temps.free(root);
			temps.free(sum);
			temps.free(logSum);
		}
	}

	/**
	 * Computes the inverse tangent of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		atan(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType iz;
		ComplexDoubleType sum;
		ComplexDoubleType diff;
		ComplexDoubleType quotient;
		ComplexDoubleType log;

		synchronized (lock) {
			iz = temps.get();
			sum = temps.get();
			diff = temps.get();
			quotient = temps.get();
			log = temps.get();
		}

		mul(I, z, iz);
		add(ONE, iz, sum);
		sub(ONE, iz, diff);
		div(sum, diff, quotient);
		log(quotient, log);
		mul(MINUS_I_OVER_TWO, log, result);

		synchronized (lock) {
			temps.free(iz);
			temps.free(sum);
			temps.free(diff);
			temps.free(quotient);
			temps.free(log);
		}
	}

	/**
	 * Computes the hyperbolic sine of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		sinh(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType minusZ;
		ComplexDoubleType expZ;
		ComplexDoubleType expMinusZ;
		ComplexDoubleType diff;

		synchronized (lock) {
			minusZ = temps.get();
			expZ = temps.get();
			expMinusZ = temps.get();
			diff = temps.get();
		}

		exp(z, expZ);
		mul(z, MINUS_ONE, minusZ);
		exp(minusZ, expMinusZ);
		sub(expZ, expMinusZ, diff);
		div(diff, TWO, result);

		synchronized (lock) {
			temps.free(minusZ);
			temps.free(expZ);
			temps.free(expMinusZ);
			temps.free(diff);
		}
	}

	/**
	 * Computes the hyperbolic cosine of a given complex number and places it in a
	 * result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		cosh(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType minusZ;
		ComplexDoubleType expZ;
		ComplexDoubleType expMinusZ;
		ComplexDoubleType sum;

		synchronized (lock) {
			minusZ = temps.get();
			expZ = temps.get();
			expMinusZ = temps.get();
			sum = temps.get();
		}

		exp(z, expZ);
		mul(z, MINUS_ONE, minusZ);
		exp(minusZ, expMinusZ);
		add(expZ, expMinusZ, sum);
		div(sum, TWO, result);

		synchronized (lock) {
			temps.free(minusZ);
			temps.free(expZ);
			temps.free(expMinusZ);
			temps.free(sum);
		}
	}

	/**
	 * Computes the hyperbolic tangent of a given complex number and places it in
	 * a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void
		tanh(ComplexType<?> z, ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType sinh;
		ComplexDoubleType cosh;

		synchronized (lock) {
			sinh = temps.get();
			cosh = temps.get();
		}

		sinh(z, sinh);
		cosh(z, cosh);
		div(sinh, cosh, result);

		synchronized (lock) {
			temps.free(sinh);
			temps.free(cosh);
		}
	}

	/**
	 * Computes the inverse hyperbolic sine of a given complex number and places
	 * it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void asinh(ComplexType<?> z,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType zSquared;
		ComplexDoubleType miniSum;
		ComplexDoubleType root;
		ComplexDoubleType sum;

		synchronized (lock) {
			zSquared = temps.get();
			miniSum = temps.get();
			root = temps.get();
			sum = temps.get();
		}

		mul(z, z, zSquared);
		add(zSquared, ONE, miniSum);
		pow(miniSum, ONE_HALF, root);
		add(z, root, sum);
		log(sum, result);

		synchronized (lock) {
			temps.free(zSquared);
			temps.free(miniSum);
			temps.free(root);
			temps.free(sum);
		}
	}

	/**
	 * Computes the inverse hyperbolic cosine of a given complex number and places
	 * it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void acosh(ComplexType<?> z,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType zSquared;
		ComplexDoubleType miniSum;
		ComplexDoubleType root;
		ComplexDoubleType sum;

		synchronized (lock) {
			zSquared = temps.get();
			miniSum = temps.get();
			root = temps.get();
			sum = temps.get();
		}

		mul(z, z, zSquared);
		sub(zSquared, ONE, miniSum);
		pow(miniSum, ONE_HALF, root);
		add(z, root, sum);
		log(sum, result);

		synchronized (lock) {
			temps.free(zSquared);
			temps.free(miniSum);
			temps.free(root);
			temps.free(sum);
		}
	}

	/**
	 * Computes the inverse hyperbolic tangent of a given complex number and
	 * places it in a result.
	 * 
	 * @param z The input number
	 * @param result The result
	 */
	public static void atanh(ComplexType<?> z,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		ComplexDoubleType sum;
		ComplexDoubleType diff;
		ComplexDoubleType quotient;
		ComplexDoubleType log;

		synchronized (lock) {
			sum = temps.get();
			diff = temps.get();
			quotient = temps.get();
			log = temps.get();
		}

		add(ONE, z, sum);
		sub(ONE, z, diff);
		div(sum, diff, quotient);
		log(quotient, log);
		mul(ONE_HALF, log, result);

		synchronized (lock) {
			temps.free(sum);
			temps.free(diff);
			temps.free(quotient);
			temps.free(log);
		}
	}

	/**
	 * Computes the addition of two complex numbers and places it in a result
	 * 
	 * @param z1 The first number
	 * @param z2 The second number
	 * @param result The result
	 */
	public static void add(ComplexType<?> z1, ComplexType<?> z2,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006
		result.setReal(z1.getRealDouble() + z2.getRealDouble());

		result.setImaginary(z1.getImaginaryDouble() + z2.getImaginaryDouble());
	}

	/**
	 * Computes the subtraction of two complex numbers and places it in a result
	 * 
	 * @param z1 The first number
	 * @param z2 The second number
	 * @param result The result
	 */
	public static void sub(ComplexType<?> z1, ComplexType<?> z2,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		result.setReal(z1.getRealDouble() - z2.getRealDouble());
		result.setImaginary(z1.getImaginaryDouble() - z2.getImaginaryDouble());
	}

	/**
	 * Computes the multiplication of two complex numbers and places it in a
	 * result
	 * 
	 * @param z1 The first number
	 * @param z2 The second number
	 * @param result The result
	 */
	public static void mul(ComplexType<?> z1, ComplexType<?> z2,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		double x =
			z1.getRealDouble() * z2.getRealDouble() - z1.getImaginaryDouble() *
				z2.getImaginaryDouble();
		double y =
			z1.getImaginaryDouble() * z2.getRealDouble() + z1.getRealDouble() *
				z2.getImaginaryDouble();

		result.setComplexNumber(x, y);
	}

	/**
	 * Computes the division of two complex numbers and places it in a result
	 * 
	 * @param z1 The first number
	 * @param z2 The second number
	 * @param result The result
	 */
	public static void div(ComplexType<?> z1, ComplexType<?> z2,
		ComplexType<?> result)
	{
		// Handbook of Mathematics and Computational Science, Harris & Stocker,
		// Springer, 2006

		double denom =
			z2.getRealDouble() * z2.getRealDouble() + z2.getImaginaryDouble() *
				z2.getImaginaryDouble();
		double x =
			(z1.getRealDouble() * z2.getRealDouble() + z1.getImaginaryDouble() *
				z2.getImaginaryDouble()) /
				denom;
		double y =
			(z1.getImaginaryDouble() * z2.getRealDouble() - z1.getRealDouble() *
				z2.getImaginaryDouble()) /
				denom;
		result.setComplexNumber(x, y);
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
		double x = z.getRealDouble();
		double y = z.getImaginaryDouble();
		return x * x + y * y;
	}

	/**
	 * Gets the argument (angle, theta, etc.) of a given complex number
	 */
	public static double getArgument(ComplexType<?> z) {
		double x = z.getRealDouble();
		double y = z.getImaginaryDouble();
		double theta;
		if (x == 0) {
			if (y > 0) theta = Math.PI / 2;
			else if (y < 0) theta = -Math.PI / 2;
			else // y == 0 : theta indeterminate
			theta = Double.NaN;
		}
		else if (y == 0) {
			if (x > 0) theta = 0;
			else // (x < 0)
			theta = Math.PI;
		}
		else // x && y both != 0
		theta = Math.atan2(y, x);

		return theta;
	}

	/**
	 * Normalizes an angle in radians to -Math.PI < angle <= Math.PI
	 */
	public static double getPrincipleArgument(double angle) {
		double arg = angle;
		while (arg <= -Math.PI)
			arg += 2 * Math.PI;
		while (arg > Math.PI)
			arg -= 2 * Math.PI;
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

	// -- helpers --

	private static class TempManager {

		private ArrayList<ComplexDoubleType> freeList =
			new ArrayList<ComplexDoubleType>();

		public ComplexDoubleType get() {
			ComplexDoubleType tmp;
			if (freeList.isEmpty()) {
				tmp = new ComplexDoubleType();
			}
			else {
				tmp = freeList.remove(freeList.size() - 1);
			}
			return tmp;
		}

		public void free(ComplexDoubleType tmp) {
			freeList.add(tmp);
		}
	}
}
