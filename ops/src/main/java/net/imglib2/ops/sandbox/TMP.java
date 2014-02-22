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

import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class TMP {

	public <V extends ComplexType<V>> void complex(V arg) {
		//
	}
	
	public <T extends RealType<T>> void real(T arg) {
		complex(arg);
	}
	
	public <V extends ComplexType<V>> void complexOp(ComplexOp<V> op) {
		
	}
	
	public <T extends RealType<T>> void realOp(RealOp<T> op) {
		complexOp(op);
	}
	
	public class ComplexOp<V extends ComplexType<V>> { }
	
	public class RealOp<T extends RealType<T>> extends ComplexOp<T> { }

	private class Assignment<K extends ComplexType<K>> {
		ComplexOp<K> op;
		public Assignment(ComplexOp<K> op) {
			this.op = op;
		}
		public void run() {}
	}
	
	public static void main(String[] args) {
		TMP tmp = new TMP();
		
		ComplexOp<ComplexDoubleType> cOp = null;
		
		Assignment<ComplexDoubleType> c = tmp.new Assignment<ComplexDoubleType>(cOp);
		c.run();
		
		RealOp<FloatType> rOp = null;
		
		Assignment<FloatType> r = tmp.new Assignment<FloatType>(rOp);
		r.run();
	}
}
