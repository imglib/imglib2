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

package net.imglib2.realtransform;

/**
 * An <em>n</em>-dimensional affine transformation whose <em>n</em>&times;(
 * <em>n</em>+1) affine transformation matrix can be set via row and column
 * index and from double arrays.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface AffineSet
{
	/**
	 * Set a field of the <em>n</em>&times;(<em>n</em>+1) affine transformation
	 * matrix.
	 * 
	 * @param value
	 * @param row
	 * @param column
	 * @return
	 */
	public void set( final double value, final int row, final int column );

	/**
	 * Set the <em>n</em>&times;(<em>n</em>+1) affine transformation matrix with
	 * row packed double values.
	 * 
	 * @param values
	 * @param row
	 * @param column
	 * @return
	 */
	public void set( final double... values );

	/**
	 * Set the <em>n</em>&times;(<em>n</em>+1) affine transformation matrix with
	 * double values from a [row][column] addressed array.
	 * 
	 * @param values
	 * @param row
	 * @param column
	 * @return
	 */
	public void set( final double[][] values );
}
