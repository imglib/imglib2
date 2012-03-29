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


package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.BinaryRelation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Christian Dietz
 */
public class BinaryRelationAssigment<T extends RealType<T>, V extends RealType<V>>
		implements
		BinaryOperation<IterableInterval<T>, IterableInterval<V>, IterableInterval<BitType>> {

	private BinaryRelation<T, V> m_rel;

	private ImgFactory<BitType> m_fac;

	public BinaryRelationAssigment(ImgFactory<BitType> fac,
			BinaryRelation<T, V> rel) {
		m_rel = rel;
		m_fac = fac;
	}

	@Override
	public IterableInterval<BitType> compute(IterableInterval<T> input1,
			IterableInterval<V> input2, IterableInterval<BitType> output) {
		if (!IterationOrderUtil.equalIterationOrder(input1, input2)
				|| !IterationOrderUtil.equalInterval(input1, input2)
				|| !IterationOrderUtil.equalIterationOrder(input1, output)) {
			throw new IllegalArgumentException("Intervals are not compatible");
		}

		Cursor<T> inCursor1 = input1.cursor();
		Cursor<V> inCursor2 = input2.cursor();

		Cursor<BitType> outCursor = output.cursor();

		while (outCursor.hasNext()) {
			outCursor.fwd();
			inCursor1.fwd();
			inCursor2.fwd();

			outCursor.get().set(m_rel.holds(inCursor1.get(), inCursor2.get()));
		}
		return output;

	}

	@Override
	public BinaryOperation<IterableInterval<T>, IterableInterval<V>, IterableInterval<BitType>> copy() {
		return new BinaryRelationAssigment<T, V>(m_fac, m_rel.copy());
	}

}
