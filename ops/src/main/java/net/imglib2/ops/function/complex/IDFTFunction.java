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

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.operation.complex.binary.ComplexAdd;
import net.imglib2.ops.operation.complex.binary.ComplexMultiply;
import net.imglib2.ops.operation.complex.unary.ComplexExp;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

// example implementation of a Inverse Discrete Fourier Transform function
//   - textbook definitions and thus SLOW

/**
 * A {@link Function} that represents the Inverse Discrete Fourier Transform
 * (spatial domain) of another (frequency) Function.
 * 
 * @author Barry DeZonia
 */
public class IDFTFunction<T extends ComplexType<T>> implements
		Function<long[], T> {

	// -- instance variables --

	private final Function<long[], T> freqFunction;
	private final long[] span;
	private final ImgFactory<ComplexDoubleType> imgFactory;
	private final ComplexImageFunction<ComplexDoubleType,ComplexDoubleType> dataArray;

	// -- temporary per instance working variables --
	private final ComplexAdd<T,T,T> adder;
	private final ComplexExp<T,T> exper;
	private final ComplexMultiply<T,T,T> multiplier;

	private final T TWO_PI_I;

	private final T constant;
	private final T expVal;
	private final T funcVal;
	private final T spatialExponent;

	private final ComplexDoubleType tmp;

	// -- constructor --

	/**
	 * Creates an IDFTFunction from inputs.
	 * 
	 * @param factory
	 * The factory that is used when creating internal data representations
	 * @param freqFunction
	 * The function in frequency space that is to be sampled
	 * @param span
	 * The dimensions for the internal spatial image
	 */
	public IDFTFunction(
		ImgFactory<ComplexDoubleType> factory,
		Function<long[], T> freqFunction,
		long[] span)
	{
		if (span.length != 2)
			throw new IllegalArgumentException(
				"IDFTFunction is only designed for two dimensional functions");

		this.tmp = new ComplexDoubleType();

		this.freqFunction = freqFunction;
		this.span = span.clone();
		this.imgFactory = factory;

		adder = new ComplexAdd<T,T,T>();
		exper = new ComplexExp<T,T>();
		multiplier = new ComplexMultiply<T,T,T>();

		TWO_PI_I = freqFunction.createOutput();

		constant = freqFunction.createOutput();
		expVal = freqFunction.createOutput();
		funcVal = freqFunction.createOutput();
		spatialExponent = freqFunction.createOutput();

		TWO_PI_I.setComplexNumber(0, 2 * Math.PI);

		this.dataArray = createDataArray();
	}

	// -- public interface --

	@Override
	public void compute(long[] point, T output) {
		dataArray.compute(point, tmp);
		output.setComplexNumber(tmp.getRealDouble(), tmp.getImaginaryDouble());
	}

	@Override
	public IDFTFunction<T> copy() {
		return new IDFTFunction<T>(
				imgFactory, freqFunction.copy(), span.clone());
	}

	@Override
	public T createOutput() {
		return freqFunction.createOutput();
	}

	// -- private helpers --

	// TODO - use a ComplexImageAssignment here instead? Speed. Elegance?

	// NOTE - may be centered over 0,0 instead of over M/2, N/2

	private ComplexImageFunction<ComplexDoubleType,ComplexDoubleType>
	createDataArray()
	{
		final Img<ComplexDoubleType> img = imgFactory.create(span,
				new ComplexDoubleType());
		final RandomAccess<ComplexDoubleType> oAccessor = img.randomAccess();
		final long[] iPosition = new long[2];
		final long[] oPosition = new long[2];
		final T sum = freqFunction.createOutput();
		final T uvTerm = freqFunction.createOutput();
		for (int ou = 0; ou < span[0]; ou++) {
			oPosition[0] = ou;
			for (int ov = 0; ov < span[1]; ov++) {
				oPosition[1] = ov;
				sum.setZero();
				for (int iu = 0; iu < span[0]; iu++) {
					iPosition[0] = iu;
					for (int iv = 0; iv < span[1]; iv++) {
						iPosition[1] = iv;
						calcTermAtPoint(oPosition, iPosition, uvTerm);
						adder.compute(sum, uvTerm, sum);
					}
				}
				sum.setComplexNumber(sum.getRealDouble() / (span[0] * span[1]),
						sum.getImaginaryDouble() / (span[0] * span[1]));
				oAccessor.setPosition(oPosition);
				oAccessor.get().setComplexNumber(sum.getRealDouble(),
						sum.getImaginaryDouble());
			}
		}
		return new ComplexImageFunction<ComplexDoubleType,ComplexDoubleType>(
				img, new ComplexDoubleType());
	}

	private void calcTermAtPoint(long[] oPosition, long[] iPosition, T uvTerm) {
		freqFunction.compute(iPosition, funcVal);
		double val = ((double) oPosition[0]) * iPosition[0] / span[0];
		val += ((double) oPosition[1]) * iPosition[1] / span[1];
		spatialExponent.setComplexNumber(val, 0);
		multiplier.compute(TWO_PI_I, spatialExponent, constant);
		exper.compute(constant, expVal);
		multiplier.compute(funcVal, expVal, uvTerm);
	}
}
