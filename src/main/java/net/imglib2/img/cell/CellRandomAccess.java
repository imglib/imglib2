/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * {@link RandomAccess} on a {@link AbstractCellImg}.
 *
 * The boundaries of the current cell are cached, so that position changes
 * within the same cell have minimal overhead.
 *
 * @author Tobias Pietzsch
 */
public class CellRandomAccess< T extends NativeType< T >, C extends Cell< ? > >
		extends AbstractLocalizable
		implements RandomAccess< T >, AbstractCellImg.CellImgSampler< C >
{
	protected final T type;

	protected final Index i;

	protected final CellGrid grid;

	protected final RandomAccess< C > randomAccessOnCells;

	protected final int[] cellDims;

	protected final long[] dimensions;

	protected int[] currentCellSteps;

	protected long[] currentCellMin;

	protected long[] currentCellMax;

	protected boolean isOutOfBounds;

	protected final long[] oobCellMin;

	protected final long[] oobCellMax;

	/**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
	protected int index;

	protected CellRandomAccess( final CellRandomAccess< T, C > randomAccess )
	{
		super( randomAccess.numDimensions() );

		type = randomAccess.type.duplicateTypeOnSameNativeImg();
		i = type.index();
		grid = randomAccess.grid;
		randomAccessOnCells = randomAccess.randomAccessOnCells.copyRandomAccess();

		randomAccess.localize( position );

		cellDims = randomAccess.cellDims;
		dimensions = randomAccess.dimensions;
		currentCellSteps = randomAccess.currentCellSteps;
		currentCellMin = randomAccess.currentCellMin;
		currentCellMax = randomAccess.currentCellMax;

		isOutOfBounds = randomAccess.isOutOfBounds;
		oobCellMin = randomAccess.oobCellMin;
		oobCellMax = randomAccess.oobCellMax;

		index = randomAccess.index;
		if ( !isOutOfBounds )
			type.updateContainer( this );
		i.set( index );
	}

	public CellRandomAccess( final AbstractCellImg< T, ?, C, ? > img )
	{
		super( img.numDimensions() );

		type = img.createLinkedType();
		i = type.index();
		grid = img.getCellGrid();
		randomAccessOnCells = img.getCells().randomAccess();

		cellDims = new int[ n ];
		dimensions = new long[ n ];
		img.getCellGrid().cellDimensions( cellDims );
		img.getCellGrid().imgDimensions( dimensions );

		isOutOfBounds = false;
		oobCellMin = new long[ n ];
		oobCellMax = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			oobCellMin[ d ] = Long.MAX_VALUE;
			oobCellMax[ d ] = Long.MIN_VALUE;
		}

		img.getCellGrid().getCellPosition( position, randomAccessOnCells );
		updatePosition( false );
	}

	@Override
	public C getCell()
	{
		return randomAccessOnCells.get();
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public CellRandomAccess< T, C > copy()
	{
		return new CellRandomAccess<>( this );
	}

	@Override
	public CellRandomAccess< T, C > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public void fwd( final int d )
	{
		index += currentCellSteps[ d ];
		if ( ++position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.fwd( d );
			updatePosition( position[ d ] >= dimensions[ d ] );
		}
		i.set( index );
	}

	@Override
	public void bck( final int d )
	{
		index -= currentCellSteps[ d ];
		if ( --position[ d ] < currentCellMin[ d ] )
		{
			randomAccessOnCells.bck( d );
			updatePosition( position[ d ] < 0 );
		}
		i.set( index );
	}

	@Override
	public void move( final int distance, final int d )
	{
		index += distance * currentCellSteps[ d ];
		position[ d ] += distance;
		if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= dimensions[ d ] );
		}
		i.set( index );
	}

	@Override
	public void move( final long distance, final int d )
	{
		index += ( int ) distance * currentCellSteps[ d ];
		position[ d ] += distance;
		if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= dimensions[ d ] );
		}
		i.set( index );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long pos = localizable.getLongPosition( d );
			if ( pos != 0 )
			{
				index += ( int ) pos * currentCellSteps[ d ];
				position[ d ] += pos;
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= dimensions[ d ];

					for ( ++d; d < n; ++d )
					{
						final long pos2 = localizable.getLongPosition( d );
						if ( pos2 != 0 )
						{
							position[ d ] += pos2;
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= dimensions[ d ];
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		i.set( index );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( distance[ d ] != 0 )
			{
				index += distance[ d ] * currentCellSteps[ d ];
				position[ d ] += distance[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= dimensions[ d ];

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= dimensions[ d ];
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		i.set( index );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( distance[ d ] != 0 )
			{
				index += ( int ) distance[ d ] * currentCellSteps[ d ];
				position[ d ] += distance[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= dimensions[ d ];

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= dimensions[ d ];
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		i.set( index );
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
		position[ d ] = pos;
		if ( pos < currentCellMin[ d ] || pos > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( pos / cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= dimensions[ d ] );
		}
		i.set( index );
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
		position[ d ] = pos;
		if ( pos < currentCellMin[ d ] || pos > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( pos / cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= dimensions[ d ] );
		}
		i.set( index );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long pos = localizable.getLongPosition( d );
			if ( pos != position[ d ] )
			{
				index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
				position[ d ] = pos;
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= dimensions[ d ];

					for ( ++d; d < n; ++d )
					{
						final long posInner = localizable.getLongPosition( d );
						if ( posInner != position[ d ] )
						{
							position[ d ] = posInner;
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= dimensions[ d ];
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		i.set( index );
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				index += ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ];
				if ( pos[ d ] < currentCellMin[ d ] || pos[ d ] > currentCellMax[ d ] )
				{
					setPos2( pos, d );
					break;
				}
				position[ d ] = pos[ d ];
			}
		}
		i.set( index );
	}

	private void setPos2( final int[] pos, final int d0 )
	{
		boolean movedOutOfBounds = false;
		for ( int d = d0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				position[ d ] = pos[ d ];
				if ( pos[ d ] < currentCellMin[ d ] || pos[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( pos[ d ] / cellDims[ d ], d );
					movedOutOfBounds |= pos[ d ] < 0 || pos[ d ] >= dimensions[ d ];
				}
			}
		}
		updatePosition( movedOutOfBounds );
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				index += ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ];
				position[ d ] = pos[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= dimensions[ d ];

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= dimensions[ d ];
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		i.set( index );
	}

	/**
	 * Update type to currentCellSteps, currentCellMin, and type after switching
	 * cells. This is called after randomAccessOnCells and position fields have
	 * been set.
	 */
	private void updatePosition( final boolean movedOutOfBounds )
	{
		// are we out of the image?
		if ( movedOutOfBounds )
		{
			isOutOfBounds = true;
			currentCellMin = oobCellMin;
			currentCellMax = oobCellMax;
		}
		else
		{
			if ( isOutOfBounds )
			{
				// did we come back into the image?
				for ( int d = 0; d < n; ++d )
					if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ] )
						return;

				// yes. we came back into the image.
				// re-initialize randomAccessOnCells to the correct
				// position.
				isOutOfBounds = false;
				grid.getCellPosition( position, randomAccessOnCells );
			}

			final C cell = getCell();

			currentCellSteps = cell.steps;
			currentCellMin = cell.min;
			currentCellMax = cell.max;

			index = cell.globalPositionToIndex( position );
			type.updateContainer( this );
		}
	}
}
