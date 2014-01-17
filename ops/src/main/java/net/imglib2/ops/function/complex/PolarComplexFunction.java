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

package net.imglib2.ops.function.complex;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.util.ComplexHelper;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;

/**
 * This {@link Function} class weds two input Functions of {@link RealType}
 * into a Function of {@link ComplexType}. The separate real values are
 * treated as inputs to a complex number of a polar (r,theta) type.
 * 
 * @author Barry DeZonia
 */
public class PolarComplexFunction<
		INPUT,
		R1 extends RealType<R1>,
		R2 extends RealType<R2>,
		C extends ComplexType<C>>
	implements Function<INPUT,C>
{
	// -- instance variables --
	
	private final Function<INPUT,R1> realFunc1;
	private final Function<INPUT,R2> realFunc2;
	private final R1 real1;
	private final R2 real2;
	private final C cType;
	
	// -- constructor --
	
	public PolarComplexFunction(
			Function<INPUT,R1> realFunc1, Function<INPUT,R2> realFunc2, C cType)
	{
		this.cType = cType;
		this.realFunc1 = realFunc1;
		this.realFunc2 = realFunc2;
		this.real1 = realFunc1.createOutput();
		this.real2 = realFunc2.createOutput();
	}
	
	// -- Function methods --
	
	@Override
	public void compute(INPUT input, C value) {
		realFunc1.compute(input, real1);
		realFunc2.compute(input, real2);
		ComplexHelper.setPolar(
				value, real1.getRealDouble(), real2.getRealDouble());
	}
	
	@Override
	public PolarComplexFunction<INPUT,R1,R2,C> copy() {
		return new PolarComplexFunction<INPUT,R1,R2,C>(
				realFunc1.copy(), realFunc2.copy(), cType.copy());
	}

	@Override
	public C createOutput() {
		return cType.createVariable();
	}
}
