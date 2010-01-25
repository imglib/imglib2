package mpicbg.imglib.cursor.special;

import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.type.Type;

public class RegionOfInterestCursor<T extends Type<T>> extends CursorImpl<T> implements LocalizableCursor<T> 
{
	final LocalizableByDimCursor<T> cursor;
	final int[] offset, size, roiPosition;
	
	// true means go forward, false go backward
	final boolean[] currentDirectionDim;
	
	final int numDimensions, numPixels, numPixelsMinus1;
	
	boolean isActive, debug = false;
	int i;
	
	public RegionOfInterestCursor( final LocalizableByDimCursor<T> cursor, final int[] offset, final int size[] )
	{
		super( cursor.getStorageContainer(), cursor.getImage(), cursor.getType() );
		
		this.offset = offset.clone();
		this.size = size.clone();		
		this.cursor = cursor;
		
		this.numDimensions = cursor.getImage().getNumDimensions();
		this.roiPosition = new int[ numDimensions ];
		this.currentDirectionDim = new boolean[ numDimensions ]; 
		
		int count = 1;
		for ( int d = 0; d < numDimensions; ++d )
			count *= size[ d ];
		
		numPixels = count;
		numPixelsMinus1 = count - 1;
		
		reset();
	}
	
	@Override
	public boolean hasNext() { return i < numPixelsMinus1; }
	
	@Override
	public void close()  { isActive = false; }

	@Override
	public T getType() { return cursor.getType(); }
	
	@Override
	public void reset()
	{
		i = -1;
		cursor.setPosition( offset );
		cursor.bck( 0 );
			
		for ( int d = 0; d < numDimensions; ++d )
		{
			// true means go forward
			currentDirectionDim[ d ] = true;
			roiPosition[ d ] = 0;
		}
		
		roiPosition[ 0 ] = -1;
	}

	@Override
	public void fwd()
	{
		++i;
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			if ( currentDirectionDim[ d ] )
			{
				if ( roiPosition[ d ] < size[ d ] - 1 )
				{
					cursor.fwd( d );
					++roiPosition[ d ];
					
					// revert the direction of all lower dimensions
					for ( int e = 0; e < d; e++ )
						currentDirectionDim[ e ] = !currentDirectionDim[ e ];
					
					return;
				}				
			}
			else
			{
				if ( roiPosition[ d ] > 0 )
				{
					cursor.bck( d );
					--roiPosition[ d ];

					// revert the direction of all lower dimensions
					for ( int e = 0; e < d; e++ )
						currentDirectionDim[ e ] = !currentDirectionDim[ e ];
					
					return;
				}
			}
		}		
	}
	
	@Override
	public int getArrayIndex() { return cursor.getArrayIndex(); }

	@Override
	public int getStorageIndex() { return cursor.getStorageIndex();	}

	@Override
	public boolean isActive() { return cursor.isActive() && isActive; }

	@Override
	public void setDebug( boolean debug ) { this.debug = debug; }

	@Override
	public void getPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = roiPosition[ d ];
	}

	@Override
	public int[] getPosition() { return roiPosition.clone(); }

	@Override
	public int getPosition( final int dim ) { return roiPosition[ dim ]; }

	@Override
	public String getPositionAsString()
	{
		String pos = "(" + roiPosition[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + roiPosition[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getPositionAsString() + " = " + getType(); }
}
