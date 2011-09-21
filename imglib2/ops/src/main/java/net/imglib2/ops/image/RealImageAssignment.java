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

package net.imglib2.ops.image;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.Condition;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.type.numeric.RealType;

/**
 * Replacement class for the old OPS' AssignOperation. Assigns the values of
 * a region of an Img<RealType> to values from a function.
 *  
 * @author Barry DeZonia
 *
 */
public class RealImageAssignment {

	private final Img<? extends RealType<?>> image;
	private ImageAssignment<RealType<?>, Real> assigner;
	
	private class RealTranslator implements TypeBridge<RealType<?>,Real> {

		@Override
		public void setPixel(RandomAccess<? extends RealType<?>> accessor, Real value) {
			accessor.get().setReal(value.getReal());
		}

		@Override
		public RandomAccess<? extends RealType<?>> randomAccess() {
			return image.randomAccess();
		}

	}
	
	public RealImageAssignment(Img<? extends RealType<?>> image, long[] origin, long[] span,
			Function<long[],Real> func, long[] negOffs, long[] posOffs)
	{
		this.image = image;
		this.assigner =
			new ImageAssignment<RealType<?>,Real>(
					new RealTranslator(),
					origin,
					span,
					func,
					negOffs,
					posOffs);
	}
	
	public void setCondition(Condition<long[]> condition) {
		assigner.setCondition(condition);
	}
	
	public void assign() {
		assigner.assign();
	}
}
