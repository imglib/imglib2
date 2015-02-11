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
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.util.Fraction;

/**
 * Implementation of {@link Cells} that keeps array of {@link AbstractCell}s as
 * an iterable grid of cells. The grid is accessed via the {@link #cells()}
 * method which derived classes must implement to return the cells as a
 * {@link RandomAccessibleInterval} and {@link IterableInterval} with minimum at
 * 0 and flat iteration order.
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractCells< A, C extends AbstractCell< A >, I extends RandomAccessibleInterval< C > & IterableInterval< C > > implements Cells< A, C >
{
	protected final Fraction entitiesPerPixel;

	protected final int n;

	protected final long[] dimensions;

	protected final int[] cellDimensions;

	protected final long[] numCells;

	protected final int[] borderSize;

	public AbstractCells( final Fraction entitiesPerPixel, final long[] dimensions, final int[] cellDimensions )
	{
		this.entitiesPerPixel = entitiesPerPixel;
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.cellDimensions = cellDimensions.clone();

		numCells = new long[ n ];
		borderSize = new int[ n ];

		for ( int d = 0; d < n; ++d )
		{
			numCells[ d ] = ( dimensions[ d ] - 1 ) / cellDimensions[ d ] + 1;
			borderSize[ d ] = ( int ) ( dimensions[ d ] - ( numCells[ d ] - 1 ) * cellDimensions[ d ] );
		}
	}

	/**
	 * Get the {@link Img} containing the cells. Derived classes must implement
	 * this to return the cells as a {@link RandomAccessibleInterval} and
	 * {@link IterableInterval} with minimum at 0 and flat iteration order.
	 * 
	 * @return the {@link Img} containing the cells.
	 */
	protected abstract I cells();

	/**
	 * From the position of a cell in the {@link #cells()} grid, compute the
	 * image position of the first pixel of the cell (the offset of the cell in
	 * image coordinates) and the dimensions of the cell. The dimensions will be
	 * the standard {@link #cellDimensions} unless the cell is at the border of
	 * the image in which case it might be truncated.
	 * 
	 * @param cellGridPosition
	 *            grid coordinates of the cell.
	 * @param cellMin
	 *            offset of the cell in image coordinates are written here.
	 * @param cellDims
	 *            dimensions of the cell are written here.
	 */
	protected void getCellDimensions( final long[] cellGridPosition, final long[] cellMin, final int[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			cellDims[ d ] = ( ( cellGridPosition[ d ] + 1 == numCells[ d ] ) ? borderSize[ d ] : cellDimensions[ d ] );
			cellMin[ d ] = cellGridPosition[ d ] * cellDimensions[ d ];
		}
	}

	@Override
	public RandomAccess< C > randomAccess()
	{
		return cells().randomAccess();
	}

	@Override
	public Cursor< C > cursor()
	{
		return cells().cursor();
	}

	@Override
	public Cursor< C > localizingCursor()
	{
		return cells().localizingCursor();
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public void dimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = dimensions[ i ];
	}

	@Override
	public long dimension( final int d )
	{
		return dimensions[ d ];
	}

	@Override
	public void cellDimensions( final int[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = cellDimensions[ i ];
	}

	@Override
	public int cellDimension( final int d )
	{
		return cellDimensions[ d ];
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return entitiesPerPixel;
	}
}
