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
import net.imglib2.ops.Complex;
import net.imglib2.type.numeric.ComplexType;

/**
 * Defines and runs an assignment of pixels within a region of an
 * Img<ComplexType> with values from a function. Assignments can
 * be conditional and can be aborted.
 *  
 * @author Barry DeZonia
 *
 */
public class ComplexImageAssignment {

	// -- instance variables --
	
	private final Img<? extends ComplexType<?>> image;
	private ImageAssignment<ComplexType<?>, Complex> assigner;
	
	// -- private helpers --
	
	@SuppressWarnings("synthetic-access")
	private class ComplexTranslator implements TypeBridge<ComplexType<?>,Complex> {

		@Override
		public void setPixel(RandomAccess<? extends ComplexType<?>> accessor, Complex value) {
			accessor.get().setComplexNumber(value.getX(), value.getY());
		}

		@Override
		public RandomAccess<? extends ComplexType<?>> randomAccess() {
			return image.randomAccess();
		}

	}
	
	// -- public interface --
	
	/**
	 * General constructor. A working neighborhood is built using negOffs and
	 * posOffs. If they are zero in extent the working neighborhood is a
	 * single pixel. This neighborhood is moved point by point over the Img<?>
	 * and passed to the function for evaluation.
	 * 
	 * @param image - the Img<ComplexType<?>> to assign data values to
	 * @param origin - the origin of the region to assign within the Img<?>
	 * @param span - the extents of the region to assign within the Img<?>
	 * @param func - the function to evaluate at each point of the region
	 * @param negOffs - the extents in the negative direction of the working neighborhood
	 * @param posOffs - the extents in the positive direction of the working neighborhood
	 * 
	 */
	@SuppressWarnings("synthetic-access")
	public ComplexImageAssignment(Img<? extends ComplexType<?>> image, long[] origin, long[] span,
			Function<long[],Complex> func, long[] negOffs, long[] posOffs)
	{
		this.image = image;
		this.assigner =
			new ImageAssignment<ComplexType<?>,Complex>(
					new ComplexTranslator(),
					origin,
					span,
					func,
					negOffs,
					posOffs);
	}

	/**
	 * Constructor for a single point input neighborhood. This neighborhood is
	 * moved point by point over the Img<?> and passed to the function for
	 * evaluation.
	 * 
	 * @param image - the Img<ComplexType<?>> to assign data values to
	 * @param origin - the origin of the region to assign within the Img<?>
	 * @param span - the extents of the region to assign within the Img<?>
	 * @param func - the point function to evaluate at each point of the region
	 * 
	 */
	public ComplexImageAssignment(Img<? extends ComplexType<?>> image, long[] origin, long[] span,
			Function<long[],Complex> func)
	{
		this(image,origin,span,func,new long[span.length],new long[span.length]);
	}
	
	/**
	 * Sets a condition that must be satisfied before each pixel assignment
	 * can take place. The condition is tested at each point in the assignment
	 * region. Should be called after construction but before the call to
	 * assign().
	 */
	public void setCondition(Condition<long[]> condition) {
		assigner.setCondition(condition);
	}
	
	/**
	 * Assign pixels using input variables specified in constructor. Can be
	 * aborted using abort().
	 */
	public void assign() {
		assigner.assign();
	}
	
	/**
	 * Aborts an in progress assignment. If no assignment is running has no effect.
	 */
	public void abort() {
		assigner.abort();
	}
}
