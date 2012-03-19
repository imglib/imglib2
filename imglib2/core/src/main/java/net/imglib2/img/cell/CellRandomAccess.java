package net.imglib2.img.cell;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

/**
 * This {@link ImgRandomAccess} assumes that successive accesses fall
 * within different cells more often than not.
 * No checks are performed to determine whether we stay in the same cell.
 * Instead, the cell position is computed and set on every access.
 */
public class CellRandomAccess< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > > extends AbstractLocalizable implements RandomAccess< T >, CellImg.CellContainerSampler< T, A, C >
{
	protected final CellImg< T, A, C > img;

	protected final T type;

	protected final RandomAccess< C > randomAccessOnCells;

	protected final int[] defaultCellDims;

	protected final long[] positionOfCurrentCell;
	protected final long[] positionInCell;

	protected int[] currentCellSteps;
	protected long[] currentCellMin;
	protected long[] currentCellMax;

	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex().
	 */
	protected int index;

	protected CellRandomAccess( final CellRandomAccess< T, A, C > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.img = randomAccess.img;
		this.type = randomAccess.type.duplicateTypeOnSameNativeImg();
		this.randomAccessOnCells = randomAccess.randomAccessOnCells.copyRandomAccess();
		this.defaultCellDims = randomAccess.defaultCellDims;

		this.positionOfCurrentCell = new long[ n ];
		this.positionInCell = new long[ n ];

		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = randomAccess.position[ d ];
			positionOfCurrentCell[ d ] = randomAccess.positionOfCurrentCell[ d ];
			positionInCell[ d ] = randomAccess.positionInCell[ d ];
		}

		currentCellSteps = randomAccess.currentCellSteps;
		currentCellMin = randomAccess.currentCellMin;
		currentCellMax = randomAccess.currentCellMax;

		index = randomAccess.index;
		type.updateContainer( this );
		type.updateIndex( index );
	}

	public CellRandomAccess( final CellImg< T, A, C > img )
	{
		super( img.numDimensions() );

		this.img = img;
		this.type = img.createLinkedType();
		this.randomAccessOnCells = img.cells.randomAccess();
		this.defaultCellDims = img.cellDims;

		this.positionOfCurrentCell = new long[ n ];
		this.positionInCell = new long[ n ];

		img.splitGlobalPosition( position, positionOfCurrentCell, positionInCell );
		randomAccessOnCells.setPosition( positionOfCurrentCell );
		updatePosition();
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
	public CellRandomAccess< T, A, C > copy()
	{
		return new CellRandomAccess< T, A, C >( this );
	}

	@Override
	public CellRandomAccess< T, A, C > copyRandomAccess()
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
			updatePosition();
		}
		type.updateIndex( index );
	}

	@Override
	public void bck( final int d )
	{
		index -= currentCellSteps[ d ];
		if ( --position[ d ] < currentCellMin[ d ] )
		{
			randomAccessOnCells.bck( d );
			updatePosition();
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final int distance, final int d )
	{
		index += distance * currentCellSteps[ d ];
		position[ d ] += distance;
		if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
			updatePosition();
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final long distance, final int d )
	{
		index += ( int ) distance * currentCellSteps[ d ];
		position[ d ] += distance;
		if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
			updatePosition();
		}
		type.updateIndex( index );
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
					randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						final long pos2 = localizable.getLongPosition( d );
						if ( pos2 != 0 )
						{
							position[ d ] += pos2;
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}

					updatePosition();
				}
			}
		}
		type.updateIndex( index );
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
					randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}

					updatePosition();
				}
			}
		}
		type.updateIndex( index );
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
					randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}

					updatePosition();
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
		position[ d ] = pos;
		if ( pos < currentCellMin[ d ] || pos > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( pos / defaultCellDims[ d ], d );
			updatePosition();
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
		position[ d ] = pos;
		if ( pos < currentCellMin[ d ] || pos > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( pos / defaultCellDims[ d ], d );
			updatePosition();
		}
		type.updateIndex( index );
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
					randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						final long posInner = localizable.getLongPosition( d );
						if ( posInner != position[ d ] )
						{
							position[ d ] = posInner;
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}

					updatePosition();
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				index += ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ];
				position[ d ] = pos[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}

					updatePosition();
				}
			}
		}
		type.updateIndex( index );
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
					randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}

					updatePosition();
				}
			}
		}
		type.updateIndex( index );
	}

	/**
	 * Update type to currentCellSteps, currentCellMin, and type after
	 * switching cells. This is called after randomAccessOnCells and position
	 * fields have been set.
	 */
	private void updatePosition()
	{
		final C cell = getCell();

		currentCellSteps = cell.steps;
		currentCellMin = cell.min;
		currentCellMax = cell.max;

		for ( int d = 0; d < n; ++d )
			positionInCell[ d ] = position[ d ] - currentCellMin[ d ];

		index = cell.localPositionToIndex( positionInCell );
		type.updateContainer( this );
	}
}
