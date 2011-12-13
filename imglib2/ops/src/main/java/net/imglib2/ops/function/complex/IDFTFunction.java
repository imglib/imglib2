/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.function.complex;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.Complex;
import net.imglib2.ops.ComplexOutput;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.operation.binary.complex.ComplexAdd;
import net.imglib2.ops.operation.binary.complex.ComplexMultiply;
import net.imglib2.ops.operation.unary.complex.ComplexExp;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

// example implementation of a Inverse Discrete Fourier Transform function
//   - textbook definitions and thus SLOW

/**
 * 
 * @author Barry DeZonia
 *
 */
public class IDFTFunction extends ComplexOutput implements Function<long[],Complex> {

	// -- static class variables --
	
	private static final ComplexAdd adder = new ComplexAdd();
	private static final ComplexExp exper = new ComplexExp();
	private static final ComplexMultiply multiplier = new ComplexMultiply();
	private static final Complex TWO_PI_I = Complex.createCartesian(0, 2*Math.PI);

	// -- instance variables --
	
	private Function<long[],Complex> freqFunction;
	private long[] span;
	private long[] negOffs;
	private long[] posOffs;
	private DiscreteNeigh neighborhood;
	private ComplexImageFunction dataArray;

	// -- temporary per instance working variables --
	private final Complex constant = new Complex();
	private final Complex expVal = new Complex();
	private final Complex funcVal = new Complex();
	private final Complex spatialExponent = new Complex();
	
	// -- constructor --
	
	public IDFTFunction(Function<long[],Complex> freqFunction, long[] span, long[] negOffs, long[] posOffs) {
		if (span.length != 2)
			throw new IllegalArgumentException("IDFTFunction is only designed for two dimensional functions");
		this.freqFunction = freqFunction;
		this.span = span.clone();
		this.negOffs = negOffs.clone();
		this.posOffs = posOffs.clone();
		this.neighborhood = new DiscreteNeigh(span.clone(), this.negOffs, this.posOffs);
		this.dataArray = createDataArray();
	}
	
	// -- public interface --
	
	@Override
	public void
		evaluate(Neighborhood<long[]> neigh, long[] point, Complex output)
	{
		dataArray.evaluate(neigh, point, output);
	}

	@Override
	public DFTFunction copy() {
		return new DFTFunction(freqFunction.copy(),span,negOffs,posOffs);
	}

	// -- private helpers --
	
	// TODO - use a ComplexImageAssignment here instead? Speed. Elegance?

	// NOTE - may be centered over 0,0 instead of over M/2, N/2
	
	private ComplexImageFunction createDataArray() {
		// TODO - this factory is always an array in memory with corresponding limitations
		final ImgFactory<ComplexDoubleType> imgFactory = new ArrayImgFactory<ComplexDoubleType>();
		final Img<ComplexDoubleType> img = imgFactory.create(span, new ComplexDoubleType());
		final RandomAccess<ComplexDoubleType> oAccessor = img.randomAccess();
		final long[] iPosition = new long[2];
		final long[] oPosition = new long[2];
		final Complex sum = new Complex();
		final Complex uvTerm = new Complex(); 
		for (int ou = 0; ou < span[0]; ou++) {
			oPosition[0] = ou;
			for (int ov = 0; ov < span[1]; ov++) {
				oPosition[1] = ov;
				sum.setCartesian(0, 0);
				for (int iu = 0; iu < span[0]; iu++) {
					iPosition[0] = iu;
					for (int iv = 0; iv < span[1]; iv++) {
						iPosition[1] = iv;
						calcTermAtPoint(oPosition, iPosition, uvTerm);
						adder.compute(sum, uvTerm, sum);
					}
				}
				sum.setCartesian(
					sum.getX() / (span[0]*span[1]),
					sum.getY() / (span[0]*span[1]));
				oAccessor.setPosition(oPosition);
				oAccessor.get().setComplexNumber(sum.getX(), sum.getY());
			}
		}
		return new ComplexImageFunction(img);
	}
	
	private void calcTermAtPoint(long[] oPosition, long[] iPosition, Complex uvTerm) {
		neighborhood.moveTo(iPosition);
		freqFunction.evaluate(neighborhood, iPosition, funcVal);
		double val = ((double)oPosition[0]) * iPosition[0] / span[0];
		val += ((double)oPosition[1]) * iPosition[1] / span[1];
		spatialExponent.setCartesian(val, 0);
		multiplier.compute(TWO_PI_I, spatialExponent, constant);
		exper.compute(constant, expVal);
		multiplier.compute(funcVal, expVal, uvTerm);
	}
}
