/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.transform.integer;

import net.imglib2.transform.Transform;

/**
 * Mixed transform allows to express common integer view transformations such as
 * translation, rotation, rotoinversion, and projection.
 *
 * <p>
 * It transform a n-dimensional source vector to a m-dimensional target vector,
 * and can be represented as a <em>m+1</em> &times; <em>n+1</em> homogeneous
 * matrix. The mixed transform can be decomposed as follows:
 * </p>
 * <ol>
 * <li>project down (discard some components of the source vector)</li>
 * <li>component permutation</li>
 * <li>component inversion</li>
 * <li>project up (add zero components in the target vector)</li>
 * <li>translation</li>
 * </ol>
 *
 * @author Tobias Pietzsch
 */
public interface Mixed extends Transform, BoundingBoxTransform
{
	/**
	 * Get the translation. Translation is added to the target vector after
	 * applying permutation, projection, inversion operations.
	 *
	 * @param translation
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getTranslation( final long[] translation );

	/**
	 * Get the d-th component of translation (see
	 * {@link #getTranslation(long[])}).
	 *
	 * @param d
	 */
	public long getTranslation( final int d );

	/**
	 * Get a boolean array indicating which target dimensions are _not_ taken
	 * from source dimensions.
	 *
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, the result will be [false, false,
	 * true]
	 * </p>
	 *
	 * @param zero
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getComponentZero( final boolean[] zero );

	/**
	 * Get the d-th component of zeroing vector (see {@link
	 * #getComponentZero(boolean[])}).
	 *
	 * @param d
	 */
	public boolean getComponentZero( final int d );

	/**
	 * Get an array indicating for each target dimensions from which source
	 * dimension it is taken.
	 *
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, the result will be [0, 1, x].
	 * Here, the value of x is undefined because the third target dimension does
	 * not correspond to any source dimension. See {@link #getComponentZero(boolean[])}.
	 * </p>
	 *
	 * @param component
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getComponentMapping( final int[] component );

	/**
	 * Get the source dimension which is mapped to the d-th target dimension
	 * (see {@link #getComponentMapping(int[])}).
	 *
	 * @param d
	 */
	public int getComponentMapping( final int d );

	/**
	 * Get an array indicating for each target component, whether the source
	 * component it is taken from should be inverted.
	 *
	 * <p>
	 * For instance, if rotating a 2D (x,y) coordinates by 180 degrees will map
	 * it to (-x,-y). In this case, the result will be [true, true].
	 * </p>
	 *
	 * @param invert
	 *            array of size at least the target dimension to store the
	 *            result.
	 */
	public void getComponentInversion( final boolean[] invert );

	/**
	 * Get the d-th component of inversion vector (see {@link
	 * #getComponentInversion(boolean[])}).
	 *
	 * @param d
	 */
	public boolean getComponentInversion( final int d );

	/**
	 * Get the matrix that transforms homogeneous source points to homogeneous
	 * target points. For testing purposes.
	 */
	public double[][] getMatrix();
}
