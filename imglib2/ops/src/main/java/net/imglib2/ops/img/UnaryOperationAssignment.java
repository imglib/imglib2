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
 * AND ANY EXPRESS OR IMPLIED WARRANTIES,  IterableInterval< T >CLUD IterableInterval< T >G, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IterableInterval< T > NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT,  IterableInterval< T >DIRECT,  IterableInterval< T >CIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES ( IterableInterval< T >CLUD IterableInterval< T >G, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUS IterableInterval< T >ESS
 *  IterableInterval< T >TERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER  IterableInterval< T >
 * CONTRACT, STRICT LIABILITY, OR TORT ( IterableInterval< T >CLUD IterableInterval< T >G NEGLIGENCE OR OTHERWISE)
 * ARIS IterableInterval< T >G  IterableInterval< T > ANY WAY  IterableInterval< V > OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.img;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.util.Util;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public class UnaryOperationAssignment<T, V> implements
		UnaryOperation<IterableInterval<T>, IterableInterval<V>> {

	private final UnaryOperation<T, V> op;

	public UnaryOperationAssignment(final UnaryOperation<T, V> op) {
		this.op = op;
	}

	public UnaryOperation<T, V> op() {
		return op;
	}

	@Override
	public IterableInterval<V> compute(IterableInterval<T> input,
			IterableInterval<V> output) {
		if (!Util.equalIterationOrder(input, output)) {
			throw new IllegalArgumentException("Incompatible IterationOrders");
		}

		final Cursor<T> inCursor = input.cursor();
		final Cursor<V> outCursor = output.cursor();
		while (inCursor.hasNext()) {
			inCursor.fwd();
			outCursor.fwd();
			op.compute(inCursor.get(), outCursor.get());
		}

		return output;
	}

	@Override
	public UnaryOperation<IterableInterval<T>, IterableInterval<V>> copy() {
		return new UnaryOperationAssignment<T, V>(op.copy());
	}
}
