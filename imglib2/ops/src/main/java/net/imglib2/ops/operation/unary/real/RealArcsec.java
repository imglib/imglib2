/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 */

package net.imglib2.ops.operation.unary.real;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

//verified formula with Mathworld's definition for Inverse Secant

/**
 * Sets the real component of an output real number to the inverse secant of
 * the real component of an input real number.
 * 
 * @author Barry DeZonia
 * 
 */
public final class RealArcsec<I extends RealType<I>, O extends RealType<O>>
	implements RealUnaryOperation<I,O>
{
	private final RealArcsin<DoubleType,DoubleType> asin =
			new RealArcsin<DoubleType,DoubleType>();
	private DoubleType angle = new DoubleType();
	private DoubleType tmp = new DoubleType();

	@Override
	public O compute(I x, O output) {
		double xt = x.getRealDouble();
		if ((xt > -1) && (xt < 1))
			throw new IllegalArgumentException("arcsec(x) : x out of range");
		else if (xt == -1)
			output.setReal(Math.PI);
		else if (xt == 1)
			output.setReal(0);
		else { // |x| > 1
			tmp.setReal(Math.sqrt(xt * xt - 1) / xt);
			asin.compute(tmp, angle);
			double value = angle.getRealDouble();
			if (xt < -1)
				value += Math.PI;
			output.setReal(value);
		}
		return output;
	}

	@Override
	public RealArcsec<I,O> copy() {
		return new RealArcsec<I,O>();
	}

}
