/**
 * <p>
 * Copyright (C) 2008 Roy Liu, The Regents of the University of California <br />
 * All rights reserved.
 * </p>
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * </p>
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.</li>
 * <li>Neither the name of the author nor the names of any contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.</li>
 * </ul>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </p>
 */
package net.imglib2.algorithm.convolver.filter.linear;

import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.list.ListImg;
import net.imglib2.ops.img.BinaryOperationAssignment;
import net.imglib2.ops.img.UnaryConstantRightAssignment;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.operation.complex.unary.ComplexExp;
import net.imglib2.ops.operation.iterableinterval.unary.IterableIntervalCopy;
import net.imglib2.ops.operation.real.binary.CombineToComplex;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.binary.RealMultiply;
import net.imglib2.ops.operation.real.binary.RealPower;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * An implementation of circular gabor filters.
 * 
 * @author Roy Liu, hornm
 */
public class GaborCircular extends ArrayImg<ComplexDoubleType, DoubleAccess> {

	public GaborCircular(int supportRadius, //
			double scale, //
			double frequency) {

		super(new DoubleArray(ArrayImgFactory.numEntitiesRangeCheck(new long[] {
				supportRadius * 2 + 1, supportRadius * 2 + 1 }, 2)),
				new long[] { supportRadius * 2 + 1, supportRadius * 2 + 1 }, 2);

		// create a Type that is linked to the container
		final ComplexDoubleType linkedType = new ComplexDoubleType(this);

		// pass it to the NativeContainer
		setLinkedType(linkedType);

		// create rotated point matrices
		Img<DoubleType> ptsMatrix = FilterTools
				.createPointSupport(supportRadius);

		Img<DoubleType> ptsY = FilterTools.reshapeMatrix(supportRadius * 2 + 1,
				FilterTools.getVector(ptsMatrix, new int[] { 0, 0 }, 1));
		Img<DoubleType> ptsX = FilterTools.reshapeMatrix(supportRadius * 2 + 1,
				FilterTools.getVector(ptsMatrix, new int[] { 1, 0 }, 1));

		final double k = scale * supportRadius;

		final double a = -4.0f / (k * k);

		Img<DoubleType> ptsRe = ptsX.factory().create(ptsX, new DoubleType());

		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealPower<DoubleType, DoubleType, DoubleType>()).compute(
				ptsX, new DoubleType(2.0d), ptsRe);

		Img<DoubleType> tmp = ptsRe.factory().create(ptsRe, new DoubleType());

		new IterableIntervalCopy<DoubleType>().compute(ptsRe, tmp);

		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealPower<DoubleType, DoubleType, DoubleType>()).compute(
				tmp, new DoubleType(2.0f), tmp);
		new BinaryOperationAssignment<DoubleType, DoubleType, DoubleType>(
				new RealAdd<DoubleType, DoubleType, DoubleType>()).compute(
				ptsRe, tmp, ptsRe);
		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealMultiply<DoubleType, DoubleType, DoubleType>())
				.compute(ptsRe, new DoubleType(a), ptsRe);

		final double b = 2.0f * Math.PI * frequency / k;

		Img<DoubleType> ptsIm = ptsX.factory().create(ptsX, new DoubleType());

		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealPower<DoubleType, DoubleType, DoubleType>()).compute(
				ptsX, new DoubleType(2.0d), ptsIm);

		new IterableIntervalCopy<DoubleType>().compute(ptsY, tmp);

		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealPower<DoubleType, DoubleType, DoubleType>()).compute(
				tmp, new DoubleType(2.0f), tmp);

		new BinaryOperationAssignment<DoubleType, DoubleType, DoubleType>(
				new RealAdd<DoubleType, DoubleType, DoubleType>()).compute(
				ptsIm, tmp, ptsIm);

		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealPower<DoubleType, DoubleType, DoubleType>()).compute(
				ptsIm, new DoubleType(0.5d), ptsIm);

		new UnaryConstantRightAssignment<DoubleType, DoubleType, DoubleType>(
				new RealMultiply<DoubleType, DoubleType, DoubleType>())
				.compute(ptsIm, new DoubleType(b), ptsIm);

		new BinaryOperationAssignment<DoubleType, DoubleType, ComplexDoubleType>(
				new CombineToComplex<DoubleType, DoubleType, ComplexDoubleType>())
				.compute(ptsRe, ptsIm, this);

		new UnaryOperationAssignment<ComplexDoubleType, ComplexDoubleType>(
				new ComplexExp<ComplexDoubleType, ComplexDoubleType>())
				.compute(this, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		if (f.numDimensions() != this.numDimensions())
			return false;

		if (ArrayImg.class.isInstance(f) || ListImg.class.isInstance(f)) {
			final Interval a = (Interval) f;
			for (int d = 0; d < n; ++d)
				if (dimension[d] != a.dimension(d))
					return false;

			return true;
		}

		return false;
	}

	// public static void main(String[] args) {
	// GaborCircular g = new GaborCircular(20, 1, 1);
	//
	// Img<DoubleType> gReal = new ImgMap<ComplexDoubleType, DoubleType>(
	// new GetReal<ComplexDoubleType, DoubleType>(new DoubleType()))
	// .compute(g);
	//
	// Img<DoubleType> gImg = new ImgMap<ComplexDoubleType, DoubleType>(
	// new GetComplex<ComplexDoubleType, DoubleType>(new DoubleType()))
	// .compute(g);
	//
	// ImgNormalize<DoubleType> n = new ImgNormalize<DoubleType>(0);
	//
	// n.manipulate(gReal);
	// n.manipulate(gImg);
	//
	// AWTImageTools.showInFrame(gReal, "real", 3);
	// AWTImageTools.showInFrame(gImg, "img", 3);
	//
	// }
}
