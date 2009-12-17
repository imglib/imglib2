package mpicbg.imglib.cursor.special;

import mpicbg.imglib.cursor.LocalizableByDimCursor3D;
import mpicbg.imglib.type.Type;

public class LocalNeighborhoodCursor3DOptimized<T extends Type<T>> extends LocalNeighborhoodCursor3D<T>
{
	final LocalizableByDimCursor3D<T> cursor;
	int x, y, z;
	
	public LocalNeighborhoodCursor3DOptimized( final LocalizableByDimCursor3D<T> cursor )
	{
		super( cursor );
		
		this.cursor = cursor;
		this.x = cursor.getX();
		this.y = cursor.getY();
		this.z = cursor.getZ();
	}
	
	@Override
	public void reset()
	{
		if (i == 25)
			cursor.bckZ();
		else
			cursor.setPosition( x, y, z );
		
		i = -1;
	}
	
	@Override
	public void update()
	{
		this.x = cursor.getX();
		this.y = cursor.getY();
		this.z = cursor.getZ();
		
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
			case -1: cursor.bckZ(); break;
			case  0: cursor.bckY(); break;
			case  1: cursor.bckX(); break;
			case  2: cursor.fwdY(); break;
			case  3: cursor.fwdY(); break;
			case  4: cursor.fwdX(); break;
			case  5: cursor.fwdX(); break;
			case  6: cursor.bckY(); break;
			case  7: cursor.bckY(); break;			
			case  8: cursor.fwdZ(); break;
			
			case  9: cursor.bckX(); break;
			case 10: cursor.bckX(); break;
			case 11: cursor.fwdY(); break;
			case 12: cursor.fwdY(); break;
			case 13: cursor.fwdX(); break;
			case 14: cursor.fwdX(); break;
			case 15: cursor.bckY(); break;
			case 16: cursor.fwdZ(); break;

			case 17: cursor.bckY(); break;
			case 18: cursor.bckX(); break;
			case 19: cursor.bckX(); break;
			case 20: cursor.fwdY(); break;
			case 21: cursor.fwdY(); break;
			case 22: cursor.fwdX(); break;
			case 23: cursor.fwdX(); break;
			case 24: cursor.bckX(); cursor.bckY(); break;
		}
		
		++i;
	}
}
