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

package net.imglib2.ops.function.real;

import net.imglib2.ops.function.Function;
import net.imglib2.type.numeric.RealType;

// TODO - could make this relative to a point rather than the origin. This would
// be more general. However at construction time you might not know the number
// of dimensions the input indices will take in the compute() method. This makes
// specifying a point tricky. If a point is supported later it should be in
// double coords for consistency with RealDistanceFromPointFunction

/**
* Computes the angle (in radians) the given long[] coordinate makes with any two
* axes and the origin. The two axes of interest are specified in the constructor.
* 
* @author Barry DeZonia
*
*/
public class RealAngleFromOriginFunction<T extends RealType<T>>
	implements Function<long[],T>
{
	// -- instance variables --
	
	private final T var;
	private final int axisU;
	private final int axisV;
	
	// -- constructor --
	
	public RealAngleFromOriginFunction(int axisU, int axisV, T var) {
		this.var = var.createVariable();
		this.axisU = axisU;
		this.axisV = axisV;
	}
	
	// -- Function methods --
	
	@Override
	public void compute(long[] input, T output) {
		double du = input[axisU];
		double dv = input[axisV];
		output.setReal(Math.atan2(dv, du));
	}

	@Override
	public T createOutput() {
		return var.createVariable();
	}

	@Override
	public RealAngleFromOriginFunction<T> copy() {
		return new RealAngleFromOriginFunction<T>(axisU, axisV, var);
	}

}

