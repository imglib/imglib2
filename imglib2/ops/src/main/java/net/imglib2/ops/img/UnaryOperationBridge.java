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

package net.imglib2.ops.img;

import net.imglib2.ops.operation.UnaryOutputOperation;

/**
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class UnaryOperationBridge<A, B, C> implements
		UnaryOutputOperation<A, C> {

	private final UnaryOutputOperation<A, B> first;

	private final UnaryOutputOperation<B, C> second;

	// Some tmp variables
	private B buf;

	private A currentInput;

	public UnaryOperationBridge(UnaryOutputOperation<A, B> first,
			UnaryOutputOperation<B, C> second) {
		this.first = first;
		this.second = second;
		this.buf = null;
		this.currentInput = null;
	}

	@Override
	public C compute(A input, C output) {
		return second.compute(first.compute(input, getBuf(input)), output);
	}

	private B getBuf(A input) {
		if (buf == null && input != currentInput) {
			currentInput = input;
			buf = first.bufferFactory().instantiate(input);
		}
		return buf;
	}

	@Override
	public UnaryOutputOperation<A, C> copy() {
		return new UnaryOperationBridge<A, B, C>(first.copy(), second.copy());
	}

	@Override
	public UnaryObjectFactory<A, C> bufferFactory() {
		return new UnaryObjectFactory<A, C>() {

			@Override
			public C instantiate(A a) {
				return second.bufferFactory().instantiate(buf);
			}
		};
	}

};
