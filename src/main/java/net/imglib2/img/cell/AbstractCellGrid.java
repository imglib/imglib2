package net.imglib2.img.cell;

public class AbstractCellGrid implements CellGrid
{
	protected final int n;

	protected final long[] dimensions;

	protected final int[] cellDimensions;

	protected final long[] numCells;

	protected final int[] borderSize;

	public AbstractCellGrid(
			final long[] dimensions,
			final int[] cellDimensions )
	{
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

	@Override
	public void getCellDimensions( long index, final long[] cellMin, final int[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long j = index / numCells[ d ];
			final long gridPos = index - j * numCells[ d ];
			index = j;
			cellDims[ d ] = ( ( gridPos == numCells[ d ] - 1 ) ? borderSize[ d ] : cellDimensions[ d ] );
			cellMin[ d ] = gridPos * cellDimensions[ d ];
		}
	}

	@Override
	public void getCellDimensions( final long[] cellGridPosition, final long[] cellMin, final int[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			cellDims[ d ] = ( ( cellGridPosition[ d ] + 1 == numCells[ d ] ) ? borderSize[ d ] : cellDimensions[ d ] );
			cellMin[ d ] = cellGridPosition[ d ] * cellDimensions[ d ];
		}
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

	public void gridDimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = numCells[ i ];
	}

	public long gridDimension( final int d )
	{
		return numCells[ d ];
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
}
