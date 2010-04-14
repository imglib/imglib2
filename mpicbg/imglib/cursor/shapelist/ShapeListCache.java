package mpicbg.imglib.cursor.shapelist;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.container.shapelist.ShapeListCached;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public abstract class ShapeListCache< T extends Type< T > >
{
	final Array<FakeType, FakeAccess> fakeArray;
	final ShapeListCached<T> container;
	final int cacheSize;

	public ShapeListCache( final int cacheSize, final ShapeListCached<T> container )
	{		
		this.container = container;
		this.cacheSize = cacheSize;
		
		fakeArray = new Array<FakeType, FakeAccess>( null, new FakeArray(), container.getDimensions(), 1 );
	}
	
	public abstract T lookUp( final int[] position );	
	public abstract ShapeListCache<T> getCursorCacheInstance();
}
