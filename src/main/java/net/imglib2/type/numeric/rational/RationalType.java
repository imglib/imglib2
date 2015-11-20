/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.type.numeric.rational;

import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.real.AbstractRealType;

/**
 * A rational number (numerator over denominator).
 *
 * @author Curtis Rueden
 */
public class RationalType<I extends IntegerType<I>> extends
	AbstractRealType<RationalType<I>>
{

	/** Components of the rational's fractional representation. */
	private I numer, denom;

	/** Constructs a rational number. */
	public RationalType(final I numer, final I denom) {
		this.numer = numer;
		this.denom = denom;
	}

	// -- RationalType methods --

	/** Gets this number's numerator. */
	public I getNumerator() {
		return numer;
	}

	/** Gets this number's denominator. */
	public I getDenominator() {
		return denom;
	}

	/** Sets this number's numerator. */
	public void setNumerator(final I numer) {
		this.numer = numer;
	}

	/** Sets this number's denominator. */
	public void setDenominator(final I denom) {
		this.denom = denom;
	}

	// -- RationalType methods --

	/** Reduces this rational's fraction to lowest terms. */
	public void reduce() {
		final I gcd = gcd(numer, denom);
		numer.div(gcd);
		denom.div(gcd);
	}

	// -- Object methods --

	@Override
	public int hashCode() {
		return numer.getInteger() ^ denom.getInteger();
	}

	@Override
	public String toString() {
		return numer + "/" + denom;
	}

	// -- RealType methods --

	@Override
	public void inc() {
		numer.add(denom);
	}

	@Override
	public void dec() {
		numer.sub(denom);
	}

	@Override
	public double getMaxValue() {
		return numer.getMaxValue();
	}

	@Override
	public double getMinValue() {
		// NB: There is insufficient information to fully compute the min value
		// from the constituent types. There are a few cases with negative vs.
		// positive ranges. 
		final double nMin = numer.getMinValue();
		final double dMin = denom.getMinValue();
		final double nMax = numer.getMaxValue();
		final double dMax = denom.getMaxValue();
		// [Nn, 0] / [Xd,-1] =
		// [Nn, 0] / [-1, 0) =
		// [Nn, 0] / (0,  1] =
		// [Nn, 0] / [1, Xd] =
		// [0, Xn] / [Nd,-1] =
		// [0, Xn] / [-1, 0) =
		// [0, Xn] / (0,  1] =
		// [0, Xn] / [1, Xd] =
		if (nMin < 0) {
			if (dMax > 0) return nMin / dMax;
		}
		else {
			return nMin;
		}
		return -Double.MAX_VALUE;
	}

	@Override
	public double getMinIncrement() {
		// FIXME
		return 0;
	}

	@Override
	public int getBitsPerPixel() {
		return numer.getBitsPerPixel() + denom.getBitsPerPixel();
	}

	// -- ComplexType methods --

	@Override
	public double getRealDouble() {
		return numer.getRealDouble() / denom.getRealDouble();
	}

	@Override
	public float getRealFloat() {
		return (float) getRealDouble();
	}

	@Override
	public void setReal(final float f) {
		setReal((double) f);
	}

	@Override
	public void setReal(final double f) {
		// FIXME: Port https://github.com/clord/fraction to Java.
		numer.setReal(f);
		denom.setOne();
	}

	// -- Type methods --

	@Override
	public RationalType<I> createVariable() {
		return copy();
	}

	@Override
	public RationalType<I> copy() {
		return new RationalType<I>(numer, denom);
	}

	@Override
	public void set(final RationalType<I> c) {
		numer.set(c.numer);
		denom.set(c.denom);
	}

	// -- MulFloatingPoint methods --

	@Override
	public void mul(final float c) {
		final RationalType<I> rt = createVariable();
		rt.setReal(c);
		mul(rt);
		
	}

	@Override
	public void mul(final double c) {
		final RationalType<I> rt = createVariable();
		rt.setReal(c);
		mul(rt);
	}

	// -- Add methods --

	@Override
	public void add(final RationalType<I> c) {
		// n/d + c.n/c.d
		// = (n*c.d)/(d*c.d) + (c.n*d)/(c.d*d)
		// = (n*c.d + c.n*d)/(d*c.d)
		numer.mul(c.denom);
		final I tmp = c.numer.copy();
		tmp.mul(denom);
		numer.add(tmp);
		denom.mul(c.denom);
	}

	// -- Div methods --

	@Override
	public void div(final RationalType<I> c) {
		// (n/d) / (c.n/c.d) = (n/d) * (c.d/c.n)
		numer.mul(c.denom);
		denom.mul(c.numer);
	}

	// -- Mul methods --

	@Override
	public void mul(final RationalType<I> c) {
		numer.mul(c.numer);
		denom.mul(c.denom);
	}

	// -- Sub methods --

	@Override
	public void sub(final RationalType<I> c) {
		// n/d - c.n/c.d
		// = (n*c.d)/(d*c.d) - (c.n*d)/(c.d*d)
		// = (n*c.d - c.n*d)/(d*c.d)
		numer.mul(c.denom);
		final I tmp = c.numer.copy();
		tmp.mul(denom);
		numer.sub(tmp);
		denom.mul(c.denom);
	}


	// -- Comparable methods --

	@Override
	public int compareTo(final RationalType<I> q) {
		final I t1 = numer.copy();
		t1.mul(q.denom);
		final I t2 = q.numer.copy();
		t2.mul(denom);
		return t1.compareTo(t2);
	}

	// -- Helper methods --

	/** Recursive Euclidean algorithm for the greatest common divisor. */
	private I gcd(final I p, final I q) {
		if (q.getIntegerLong() == 0) return p;
		return gcd(q, mod(p, q));
	}

	private I mod(final I p, final I q) {
		// shave off the remainder
		final I tmp = p.copy();
		tmp.div(q);
		tmp.mul(q);
		// take the difference
		final I mod = p.copy();
		mod.sub(tmp);
		return mod;
	}

}
