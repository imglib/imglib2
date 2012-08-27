/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.img.binary;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * This class can be used to combine two input {@link Img}'s into an output
 * {@link Img}. A {@link BinaryOperation} is specified which defines the rule
 * by which the input data values are combined.
 * 
 * @author Barry DeZonia
 */
public class ImgCombine
	< U extends RealType< U >, V extends RealType< V >, W extends RealType< W >>
implements BinaryOperation< Img< U >, Img< V >, Img< W > >
{
	// -- instacne variables --
	
	private final BinaryOperation<U,V,W> operation;
	
	// -- constructor --

	/**
	 * Constructs an ImgCombine operation from a {@link BinaryOperation}.

	 * @param operation
	 * 		The BinaryOperation that specifies the rule by which the input data
	 * 		elements are combined into the output elements.
	 */
	public ImgCombine(BinaryOperation<U,V,W> operation) {
		this.operation = operation;
	}
	
	// -- BinaryOperation methods --

	/**
	 * Runs the operation. This operation fills the whole output image region
	 * with the binary combination of the input values. The output region must be
	 * contained within the extents of each of the input regions.
	 */
	@Override
	public Img<W> compute(Img<U> input1, Img<V> input2, Img<W> output)
	{
		long[] position = new long[output.numDimensions()];
		Cursor<W> cursor = output.localizingCursor();
		RandomAccess<U> accessor1 = input1.randomAccess();
		RandomAccess<V> accessor2 = input2.randomAccess();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.localize(position);
			accessor1.setPosition(position);
			accessor2.setPosition(position);
			operation.compute(accessor1.get(), accessor2.get(), cursor.get());
		}
		return output;
	}

	/**
	 * Creates a copy of this binary operation. Useful for parallelization.
	 */
	@Override
	public ImgCombine<U,V,W> copy()
	{
		return new ImgCombine<U,V,W>(operation.copy());
	}

}
