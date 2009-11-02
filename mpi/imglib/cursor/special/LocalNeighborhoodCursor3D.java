package mpi.imglib.cursor.special;

import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.type.Type;

public class LocalNeighborhoodCursor3D<T extends Type<T>> extends LocalNeighborhoodCursor<T>
{
	int i = -1;
	
	public LocalNeighborhoodCursor3D( final LocalizableByDimCursor<T> cursor ) 
	{ 
		super( cursor );
		
		if ( numDimensions != 3 )
		{
			System.out.println( "LocalNeighborhoodCursor3D.constructor(): Error, dimensionality is not 3 but " + numDimensions + ", I have to close." );
			close();
		}
	}
	
	@Override
	public void reset()
	{
		if (i == 25)
			cursor.bck( 2 );
		else
			cursor.setPosition( position );
		
		i = -1;
	}
	
	@Override
	public void update()
	{
		cursor.getPosition( position );		
		i = -1;
	}
	
	@Override
	public boolean hasNext()
	{
		if (i < 25)
			return true;
		else 
			return false;		
	}
	
	@Override
	public void fwd()
	{
		// acces plan for neighborhood, starting at the 
		// center position (x)
		//
		// upper z plane (z-1)
		// -------------
		// | 2 | 1 | 8 |
		// |------------
		// | 3 | 0 | 7 |
		// |------------
		// | 4 | 5 | 6 |
		// -------------
		//
		// mid z plane (z=0)
		// -------------
		// | 11| 10| 9 |
		// |------------
		// | 12| x | 16|
		// |------------
		// | 13| 14| 15|
		// -------------
		//
		// lower z plane(z+1)
		// -------------
		// | 20| 19| 18|
		// |------------
		// | 21| 25| 17|
		// |------------
		// | 22| 23| 24|
		// -------------
		//
		
		switch( i )		
		{
			case -1: cursor.bck( 2 ); break;
			case  0: cursor.bck( 1 ); break;
			case  1: cursor.bck( 0 ); break;
			case  2: cursor.fwd( 1 ); break;
			case  3: cursor.fwd( 1 ); break;
			case  4: cursor.fwd( 0 ); break;
			case  5: cursor.fwd( 0 ); break;
			case  6: cursor.bck( 1 ); break;
			case  7: cursor.bck( 1 ); break;			
			case  8: cursor.fwd( 2 ); break;
			
			case  9: cursor.bck( 0 ); break;
			case 10: cursor.bck( 0 ); break;
			case 11: cursor.fwd( 1 ); break;
			case 12: cursor.fwd( 1 ); break;
			case 13: cursor.fwd( 0 ); break;
			case 14: cursor.fwd( 0 ); break;
			case 15: cursor.bck( 1 ); break;
			case 16: cursor.fwd( 2 ); break;

			case 17: cursor.bck( 1 ); break;
			case 18: cursor.bck( 0 ); break;
			case 19: cursor.bck( 0 ); break;
			case 20: cursor.fwd( 1 ); break;
			case 21: cursor.fwd( 1 ); break;
			case 22: cursor.fwd( 0 ); break;
			case 23: cursor.fwd( 0 ); break;
			case 24: cursor.bck( 0 ); cursor.bck( 1 ); break;
		}
		
		++i;
	}
}
