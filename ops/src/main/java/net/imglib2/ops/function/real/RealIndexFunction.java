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
import net.imglib2.type.numeric.real.DoubleType;

/**
* RealIndexFunction is a {@link Function} that returns one of the coordinates
* of an input coordinate long[]. The coordinate to select is specified in the
* constructor.
* 
* @author Barry DeZonia
*
*/
public class RealIndexFunction implements Function<long[],DoubleType> {

	// -- instance variables -- 
	
	private final int position;
	
	// -- constructor --
	
	/**
	 * Constructor. Takes the position of the axis of interest within the input
	 * long[] coordinate.
	 * 
	 * @param position
	 * The axis of interest (ranging from 0 to max-1).
	 */
	public RealIndexFunction(int position) {
		this.position = position;
	}
	
	// -- Function methods --

	/**
	 * Computes the output value from the input. In this case the output contains
	 * the value of the input coordinate at the axis position of interest.
	 */
	@Override
	public void compute(long[] input, DoubleType output) {
		output.setReal(input[position]);
	}

	@Override
	public DoubleType createOutput() {
		return new DoubleType();
	}

	@Override
	public RealIndexFunction copy() {
		return new RealIndexFunction(position);
	}
	
}

