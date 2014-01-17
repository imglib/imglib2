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

package net.imglib2.ops.example;

import net.imglib2.img.Img;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.complex.ComplexImageFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.img.ImageAssignment;
import net.imglib2.ops.input.InputIteratorFactory;
import net.imglib2.ops.input.PointInputIteratorFactory;
import net.imglib2.ops.operation.real.unary.RealSqr;
import net.imglib2.ops.operation.real.unary.RealUnaryOperation;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * TODO
 *
 */
public class ExampleMisc {

	private static class CoordIsOdd implements Condition<long[]> {

		@Override
		public boolean isTrue(long[] val) {
			for (int i = 0; i < val.length; i++) {
				if (val[i] % 2 == 0) return false;
			}
			return true;
		}

		@Override
		public CoordIsOdd copy() {
			return new CoordIsOdd();
		}
		
	}
	
	static void basicAssignmentSequence() {
		Img<UnsignedByteType> inputImg = null; // image 100x200
		Img<UnsignedByteType> outputImg = null; // image 100x200

		// sub region for assignment
		long[] origin = new long[] { 0, 0 };
		long[] span = new long[] { 50, 40 };

		RealUnaryOperation<DoubleType,DoubleType> op =
				new RealSqr<DoubleType, DoubleType>();

		Function<long[], DoubleType> imageFunc =
				new ComplexImageFunction<UnsignedByteType,DoubleType>(
						inputImg, new DoubleType());

		Function<long[], DoubleType> func =
			new GeneralUnaryFunction<long[],DoubleType,DoubleType>(
					imageFunc, op, new DoubleType());

		@SuppressWarnings("synthetic-access")
		Condition<long[]> condition = new CoordIsOdd();

		InputIteratorFactory<long[]> factory = new PointInputIteratorFactory();
		ImageAssignment<UnsignedByteType,DoubleType,long[]> assigner =
				new ImageAssignment<UnsignedByteType,DoubleType,long[]>(
				outputImg, origin, span, func, condition, factory);

		assigner.assign(); // processed in parallel

		assigner.abort(); // if desired
	}

	public static void main(String[] args) {

		RealType<?> r = new UnsignedByteType();
		ComplexType<?> c = new ComplexDoubleType();
		
		System.out.println(r.getClass()+" is a RealType : "+(r instanceof RealType<?>));
		System.out.println(r.getClass()+" is a ComplexType : "+(r instanceof ComplexType<?>));
		System.out.println(c.getClass()+" is a RealType : "+(c instanceof RealType<?>));
		System.out.println(c.getClass()+" is a ComplexType : "+(c instanceof ComplexType<?>));
	}
}
