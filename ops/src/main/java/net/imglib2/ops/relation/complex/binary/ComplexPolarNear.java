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

package net.imglib2.ops.relation.complex.binary;

import net.imglib2.ops.relation.BinaryRelation;
import net.imglib2.type.numeric.ComplexType;

/**
 * Returns true if two complex numbers are near each other in a polar (r,theta)
 * sense. The tolerances can be specified in the constructor.
 * 
 * @author Barry DeZonia
 */
public final class ComplexPolarNear<T extends ComplexType<T>,U extends ComplexType<U>>
	implements BinaryRelation<T,U>
{
	// -- constants --
	
	private static final double TWO_PI = 2 * Math.PI;
	
	// -- instance variables --
	
	private final double rTol;
	private final double thetaTol;

	// -- constructors --
	
	public ComplexPolarNear() {
		rTol = 0.000001;
		thetaTol = 0.000001;
	}
	
	public ComplexPolarNear(double rTol, double tTol) {
		this.rTol = rTol;
		this.thetaTol = tTol;
	}
	
	// -- BinaryRelation methods --
	
	@Override
	public boolean holds(T val1, U val2) {
		if (Math.abs(val1.getPowerDouble() - val2.getPowerDouble()) > rTol) return false;
		double theta1 = val1.getPhaseDouble();
		double theta2 = val2.getPhaseDouble();
		while ((theta1-theta2) > TWO_PI) theta2 += TWO_PI;
		while ((theta1-theta2) < -TWO_PI) theta1 += TWO_PI;
		// now theta1 and theta2 are within 2 pi of each other
		if (Math.abs(theta1-theta2) <= thetaTol) return true;
		return Math.abs(theta1-theta2) > (TWO_PI - thetaTol);
	}

	@Override
	public ComplexPolarNear<T,U> copy() {
		return new ComplexPolarNear<T,U>(rTol, thetaTol);
	}
}
