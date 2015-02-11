/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.img.cell;

import net.imglib2.Cursor;
import net.imglib2.EuclideanSpace;
import net.imglib2.RandomAccess;
import net.imglib2.util.Fraction;

/**
 * An array of {@link AbstractCell}s to store the data of a {@link CellImg}.
 * Implementations may either keep all cells in memory, or cache them on demand.
 * 
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface Cells< A, C extends AbstractCell< A > > extends EuclideanSpace
{
	/**
	 * Write the number of pixels in each dimension into long[]. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 * 
	 * @param dimensions
	 */
	public void dimensions( long[] dimensions );

	/**
	 * Get the number of pixels in a given dimension <em>d</em>. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 * 
	 * @param d
	 */
	public long dimension( int d );

	/**
	 * Write the number of pixels in a standard cell in each dimension into
	 * long[]. Cells on the max border of the image may be cut off and have
	 * different dimensions.
	 * 
	 * @param dimensions
	 */
	public void cellDimensions( int[] dimensions );

	/**
	 * Get the number of pixels in a standard cell in a given dimension
	 * <em>d</em>. Cells on the max border of the image may be cut off and have
	 * different dimensions.
	 * 
	 * @param d
	 */
	public int cellDimension( int d );

	public Fraction getEntitiesPerPixel();

	/**
	 * Get a {@link RandomAccess} on the cells array.
	 * 
	 * @return a {@link RandomAccess} on the cells array.
	 */
	public RandomAccess< C > randomAccess();

	/**
	 * Get a {@link Cursor} on the cells array.
	 * 
	 * @return a {@link Cursor} on the cells array.
	 */
	public Cursor< C > cursor();

	/**
	 * Get a localizing {@link Cursor} on the cells array.
	 * 
	 * @return a localizing {@link Cursor} on the cells array.
	 */
	public Cursor< C > localizingCursor();
}
