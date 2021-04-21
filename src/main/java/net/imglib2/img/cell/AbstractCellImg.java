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

package net.imglib2.img.cell;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

/**
 * Abstract superclass for {@link Img} types that divide their underlying data
 * into cells.
 *
 * @author Mark Hiner
 * @author Tobias Pietzsch
 */
public abstract class AbstractCellImg<
				T extends NativeType< T >,
				A,
				C extends Cell< A >,
				I extends RandomAccessible< C > & IterableInterval< C > >
		extends AbstractNativeImg< T, A >
{
	protected final CellGrid grid;

	protected final I cells;

	public AbstractCellImg( final CellGrid grid, final I imgOfCells, final Fraction entitiesPerPixel )
	{
		super( grid.getImgDimensions(), entitiesPerPixel );
		this.grid = grid;
		this.cells = imgOfCells;
	}

	/**
	 * This interface is implemented by all samplers on the {@link AbstractCellImg}. It
	 * allows to ask for the cell the sampler is currently in.
	 */
	public interface CellImgSampler< C >
	{
		/**
		 * @return the cell the sampler is currently in.
		 */
		public C getCell();
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public A update( final Object cursor )
	{
		// directly get data?
		return ( ( CellImgSampler< C > ) cursor ).getCell().getData();
	}

	@Override
	public CellCursor< T, C > cursor()
	{
		return new CellCursor<>( this );
	}

	@Override
	public CellLocalizingCursor< T, C > localizingCursor()
	{
		return new CellLocalizingCursor<>( this );
	}

	@Override
	public CellRandomAccess< T, C > randomAccess()
	{
		return new CellRandomAccess<>( this );
	}

	@Override
	public CellIterationOrder iterationOrder()
	{
		return new CellIterationOrder( this );
	}

	/**
	 * Get the underlying image of cells which gives access to the individual
	 * {@link Cell}s through Cursors and RandomAccesses.
	 *
	 * @return the image of cells.
	 */
	public I getCells()
	{
		return cells;
	}

	/**
	 * Get the {@link CellGrid} which describes the layout of the
	 * {@link AbstractCellImg}. The grid provides the dimensions of the image, the
	 * number of cells in each dimension, and the dimensions of individual
	 * cells.
	 *
	 * @return the cell grid layout.
	 */
	public CellGrid getCellGrid()
	{
		return grid;
	}

	protected void copyDataTo( final AbstractCellImg< T, ?, ?, ? > copy )
	{
		final CellCursor< T, C > source = this.cursor();
		final CellCursor< T, ? > target = copy.cursor();

		while ( source.hasNext() )
			target.next().set( source.next() );
	}
}
