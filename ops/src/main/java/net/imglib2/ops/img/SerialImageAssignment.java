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

package net.imglib2.ops.img;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.input.InputIterator;
import net.imglib2.type.numeric.ComplexType;

/**
 * <P>
 * An image assignment class that does not use parallel processing. Certain
 * algorithms are more easily handled by avoiding automatic parallelization.
 * 
 * <P>
 * Image assignment is the process of assigning pixels into a given Img<?>
 * using a passed in Function<?,?> for computing values. The region to fill is
 * provided via an InputIterator<?>. Assignment can be constrained to only apply
 * to input that matches a user specified Condition<?>.
 * 
 * @author Barry DeZonia
 *
 * @param <U>
 * @param <V>
 * @param <INPUT>
 */
public class SerialImageAssignment<U extends ComplexType<U>,V extends ComplexType<V>,INPUT> {

	private final RandomAccessible<U> img;
	private final Function<INPUT,V> function;
	private final InputIterator<INPUT> iter;
	private final Condition<INPUT> condition;

	/**
	 * SerialImageAssignment constructor
	 * 
	 * @param img
	 * 	The RandomAccess<?> to assign output values to
	 * @param function
	 * 	The Function<?,?> to obtain values from
	 * @param iter
	 * 	The InputIterator<?> that defines the input/output regions to assign
	 * @param condition
	 * 	The optional Condition<?> that can further constrain which values to fill
	 */
	public SerialImageAssignment(RandomAccessible<U> img, Function<INPUT,V> function,
		InputIterator<INPUT> iter, Condition<INPUT> condition)
	{
		this.img = img;
		this.function = function;
		this.iter = iter;
		this.condition = condition;
	}
	
	/**
	 * Conditionally assigns pixels in the output region.
	 */
	public void assign() {
		final RandomAccess<U> accessor = img.randomAccess();
		final V output = function.createOutput();
		INPUT input = null;
		while (iter.hasNext()) {
			input = iter.next(input);
			boolean proceed = (condition == null) || (condition.isTrue(input));
			if (proceed) {
				function.compute(input, output);
				accessor.setPosition(iter.getCurrentPoint());
				accessor.get().setReal(output.getRealDouble());
				accessor.get().setImaginary(output.getImaginaryDouble());
				// FIXME
				// Note - for real datasets this imaginary assignment may waste cpu
				// cycles. Perhaps it can get optimized away by the JIT. But maybe not
				// since the type is not really known because this class is really
				// constructed from a raw type. We'd need to test how the JIT handles
				// this situation. Note that in past incarnations this class used
				// assigner classes. The complex version set R & I but the real
				// version just set R. We could adopt that approach once again.
			}
		}
	}
}
