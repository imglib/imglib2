package net.imglib2.img.cell;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListImgFactory;

public class ListImgCells< A extends ArrayDataAccess< A > > implements Cells< A >
{
	private final int entitiesPerPixel;
	private final int n;
	private final long[] dimensions;
	private final int[] cellDimensions;
	private final ListImg< Cell< A > > cells;
	
	public ListImgCells( final A creator, int entitiesPerPixel, final long[] dimensions, final int[] cellDimensions  )
	{
		this.entitiesPerPixel = entitiesPerPixel;
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.cellDimensions = cellDimensions.clone();

		final long[] numCells = new long[ n ];
		final int[] borderSize = new int[ n ];
		final long[] currentCellOffset = new long[ n ];
		final int[] currentCellDims = new int[ n ];

		for ( int d = 0; d < n; ++d ) {
			numCells[ d ] = ( dimensions[ d ] - 1 ) / cellDimensions[ d ] + 1;
			borderSize[ d ] = ( int )( dimensions[ d ] - (numCells[ d ] - 1) * cellDimensions[ d ] );
		}

		cells = new ListImgFactory< Cell< A > >().create( numCells, new Cell< A >( n ) );

		Cursor< Cell < A > > cellCursor = cells.localizingCursor();		
		while ( cellCursor.hasNext() ) {
			Cell< A > c = cellCursor.next();
			
			cellCursor.localize( currentCellOffset );
			for ( int d = 0; d < n; ++d )
			{
				currentCellDims[ d ] = ( int )( (currentCellOffset[d] + 1 == numCells[d])  ?  borderSize[ d ]  :  cellDimensions[ d ] );
				currentCellOffset[ d ] *= cellDimensions[ d ];
			}
			
			c.set( new Cell< A >( creator, currentCellDims, currentCellOffset, entitiesPerPixel ) );
		}
	}
	
	@Override
	public RandomAccess< Cell< A > > randomAccess()
	{
		return cells.randomAccess();
	}

	@Override
	public Cursor< Cell< A > > cursor()
	{
		return cells.cursor();
	}

	@Override
	public Cursor< Cell< A >> localizingCursor()
	{
		return cells.localizingCursor();
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
		try { return this.dimensions[ d ]; }
		catch ( ArrayIndexOutOfBoundsException e ) { return 1; }
	}

	@Override
	public void cellDimensions( int[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = cellDimensions[ i ];
	}

	@Override
	public int cellDimension( int d )
	{
		try { return this.cellDimensions[ d ]; }
		catch ( ArrayIndexOutOfBoundsException e ) { return 1; }
	}

	@Override
	public int getEntitiesPerPixel()
	{
		return entitiesPerPixel;
	}
}
