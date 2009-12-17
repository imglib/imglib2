package mpicbg.imglib.cursor.special;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor3D;
import mpicbg.imglib.type.Type;

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
