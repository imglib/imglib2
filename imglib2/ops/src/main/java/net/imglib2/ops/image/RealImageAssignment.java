/*

Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld.
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
import net.imglib2.ops.DiscreteIterator;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.type.numeric.RealType;

// In old AssignOperation could do many things
// - set conditions on each input and output image
//     Now this can be done by creating a complex Condition
// - set regions of input and output
//     Now this can be done by creating a complex Condition
// - interrupt from another thread
//     still to do
// - observe the iteration
//     still to do
// regions in same image could be handled by a translation function that
//   transforms from one space to another
// regions in different images can also be handled this way
//   a translation function takes a function and a coord transform
// now also these regions, if shape compatible, can be composed into a N+1
//   dimensional space and handled as one dataset

/**
 * Replacement class for the old OPS' AssignOperation. Assigns the values of
 * a region of an Img<RealType> to values from a function.
 *  
 * @author Barry DeZonia
 *
 */
public class RealImageAssignment {

	private RandomAccess<? extends RealType<?>> accessor;
	private DiscreteNeigh neigh;
	private Function<DiscreteNeigh,Real> function;
	private Condition<DiscreteNeigh> condition;
	
	public RealImageAssignment(Img<? extends RealType<?>> image, DiscreteNeigh neigh,
			Function<DiscreteNeigh,Real> function)
	{
		this.accessor = image.randomAccess();
		this.neigh = neigh;
		this.function = function;
		this.condition = null;
	}
	
	public void setCondition(Condition<DiscreteNeigh> condition) {
		this.condition = condition;
	}
	
	// TODO
	// - add listeners (like progress indicators, stat collectors, etc.)
	// - make interruptible
	
	public void assign() {
		DiscreteNeigh region = neigh.duplicate();
		Real output = function.createVariable();
		DiscreteIterator iter = neigh.getIterator();
		while (iter.hasNext()) {
			iter.fwd();
			region.moveTo(iter.getPosition());
			boolean proceed = (condition == null) || (condition.isTrue(region));
			if (proceed) {
				function.evaluate(region, output);
				accessor.setPosition(iter.getPosition());
				accessor.get().setReal(output.getReal());
			}
		}
		
	}

}
