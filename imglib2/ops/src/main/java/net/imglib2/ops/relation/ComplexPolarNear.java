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

package net.imglib2.ops.relation;

import net.imglib2.ops.BinaryRelation;
import net.imglib2.ops.ComplexHelper;
import net.imglib2.type.numeric.ComplexType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public final class ComplexPolarNear<T extends ComplexType<T>,U extends ComplexType<U>>
	implements BinaryRelation<T,U>
{
	private final double rTol;
	private final double thetaTol;

	public ComplexPolarNear() {
		rTol = 0.000001;
		thetaTol = 0.000001;
	}
	
	public ComplexPolarNear(double rTol, double tTol) {
		this.rTol = rTol;
		this.thetaTol = tTol;
	}
	
	@Override
	public boolean holds(T val1, U val2) {
		if (Math.abs(val1.getRealDouble() - val2.getRealDouble()) > rTol) return false;
		double theta1 = ComplexHelper.getArgument(val1);
		double theta2 = ComplexHelper.getArgument(val2);
		if (Math.abs(theta1-theta2) < thetaTol) return true;
		// what about angles that are separated by near 2 pi?
		if (Math.abs(theta1-theta2) < 2*Math.PI - thetaTol) return true;
		return false;
	}

	@Override
	public ComplexPolarNear<T,U> copy() {
		return new ComplexPolarNear<T,U>(rTol, thetaTol);
	}
}
