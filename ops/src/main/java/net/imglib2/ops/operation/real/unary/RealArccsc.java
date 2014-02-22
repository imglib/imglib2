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

package net.imglib2.ops.operation.real.unary;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

//verified formula with Mathworld's definition for Inverse Cosecant

/**
 * Sets the real component of an output real number to the inverse cosecant
 * of the real component of an input real number.
 * 
 * @author Barry DeZonia
 */
public final class RealArccsc<I extends RealType<I>, O extends RealType<O>>
	implements RealUnaryOperation<I,O>
{
	private static final RealArccos<DoubleType,DoubleType> acos =
			new RealArccos<DoubleType,DoubleType>();
	private DoubleType angle = new DoubleType();
	private DoubleType tmp = new DoubleType();

	@Override
	public O compute(I x, O output) {
		double xt = x.getRealDouble();
		if ((xt > -1) && (xt < 1))
			throw new IllegalArgumentException("arccsc(x) : x out of range");
		else if (xt == -1)
			output.setReal(-Math.PI / 2);
		else if (xt == 1)
			output.setReal(Math.PI / 2);
		else {
			tmp.setReal(Math.sqrt(xt * xt - 1) / xt);
			acos.compute(tmp, angle);
			output.setReal(angle.getRealDouble());
		}
		return output;
	}

	@Override
	public RealArccsc<I,O> copy() {
		return new RealArccsc<I,O>();
	}
}
