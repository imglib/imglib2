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

package net.imglib2.ops.function.general;

import net.imglib2.ops.function.Function;

/**
 * A {@link Function} that maps real coordinates from one space to another via a
 * matrix transformation.
 * 
 * @author Barry DeZonia
 */
public class ContinuousTransformationFunction implements
	Function<double[], double[]>
{

	// -- fields --

	private final double[][] matrix;
	private final int mrows;
	private final int mcols;

	// -- constructor --

	/**
	 * Constructor that requires the transformation matrix as input. The matrix is
	 * of dimension r x c. r matches the number of components in an input point. c
	 * matches the number of components in an output point.
	 * 
	 * @param matrix
	 */
	public ContinuousTransformationFunction(double[][] matrix) {
		this.matrix = matrix;
		this.mrows = matrix.length;
		this.mcols = matrix[0].length;
	}

	// -- accessors --

	/**
	 * Returns the transformation matrix of this instance.
	 */
	public double[][] matrix() {
		return matrix;
	}

	/**
	 * Returns the number of rows in the transformation matrix of this instance.
	 * Also is the number of components in an input point.
	 */
	public int rows() {
		return mrows;
	}

	/**
	 * Returns the number of cols in transformation matrix of this instance. Also
	 * is the number of components in an output point.
	 */
	public int cols() {
		return mcols;
	}

	// -- Function methods --

	@Override
	public void compute(double[] input, double[] output) {
		// input point should be r x 1 transpose (thus 1 x r)
		// matrix should be r x c
		// output should be 1 x c
		if (input.length != mrows) {
			throw new IllegalArgumentException(
				"Input point size and matrix shape are incompatible.");
		}
		if (output.length != mcols) {
			throw new IllegalArgumentException(
				"Output point size and matrix shape are incompatible.");
		}
		for (int c = 0; c < mcols; c++) {
			double sum = 0;
			for (int r = 0; r < mrows; r++) {
				sum += input[r] * matrix[r][c];
			}
			output[c] = sum;
		}
	}

	@Override
	public double[] createOutput() {
		return new double[mcols];
	}

	@Override
	public ContinuousTransformationFunction copy() {
		return new ContinuousTransformationFunction(matrix);
	}

}
