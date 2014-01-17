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

package net.imglib2.ops.sandbox;

import net.imglib2.IterableInterval;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class RealBogusFunction<IN,OUT extends RealType<OUT>> implements Function<IN,OUT> {

	UnaryOperation<IN,OUT> op;
	
	public RealBogusFunction(UnaryOperation<IN,OUT> op) {
		this.op = op;
	}

	public void evaluate(IterableInterval<IN> interval, OUT output) {
		// note 1: outside how do I move interval and reuse over and over?
		//interval.translate(); // how???;
		// now let's pretend we are an image function that has no input point
		//   how to get value of first element?
		//interval.firstElement();  // this hatches a cursor maybe?? ouch
		//   or do we need middle element? what about even neighborhoods?
		//   and avoiding recalculation?
		//interval.???;
		op.compute(interval.firstElement(), output);
		// interval.
		// impulse function??
	}

	@Override
	public OUT createOutput() {
		return null;
	}

	@Override
	public Function<IN, OUT> copy() {
		return new RealBogusFunction<IN,OUT>(op);
	}

	@Override
	public void compute(IN point, OUT output) {
		// TODO Auto-generated method stub
		
	}

}
