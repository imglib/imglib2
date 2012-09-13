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


package net.imglib2.ops.function.complex;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.util.ComplexHelper;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;


/**
 * 
 * @author Barry DeZonia
 */
public class PolarComplexFunction<INPUT, R extends RealType<R>, C extends ComplexType<C>>
	implements Function<INPUT,C> {

	private final Function<INPUT,R> realFunc1;
	private final Function<INPUT,R> realFunc2;
	private final R real1;
	private final R real2;
	private final C cType;
	
	public PolarComplexFunction(Function<INPUT,R> realFunc1, Function<INPUT,R> realFunc2, C cType) {
		this.cType = cType;
		this.realFunc1 = realFunc1;
		this.realFunc2 = realFunc2;
		this.real1 = realFunc1.createOutput();
		this.real2 = realFunc2.createOutput();
	}
	
	@Override
	public void compute(INPUT input, C value) {
		realFunc1.compute(input, real1);
		realFunc2.compute(input, real2);
		ComplexHelper.setPolar(value, real1.getRealDouble(), real2.getRealDouble());
	}
	
	@Override
	public PolarComplexFunction<INPUT,R,C> copy() {
		return new PolarComplexFunction<INPUT,R,C>(realFunc1.copy(), realFunc2.copy(), cType);
	}

	@Override
	public C createOutput() {
		return cType.createVariable();
	}
}
