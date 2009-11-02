package mpi.imglib.cursor.special;

import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableByDimCursor3D;
import mpi.imglib.type.Type;

public class LocalNeighborhoodCursorFactory 
{
	public static <T extends Type<T>>LocalNeighborhoodCursor<T> createLocalNeighborhoodCursor( final LocalizableByDimCursor<T> cursor )
	{
		if ( cursor.getImage().getNumDimensions() == 3 )
		{
			if ( LocalizableByDimCursor3D.class.isInstance( cursor ) )
				return new LocalNeighborhoodCursor3DOptimized<T>( (LocalizableByDimCursor3D<T>)cursor );
			else
				return new LocalNeighborhoodCursor3D<T>( cursor );
		}
		else
		{
			return new LocalNeighborhoodCursor<T>( cursor );
		}
	}
}
