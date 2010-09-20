package mpicbg.imglib.cursor.shapelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import mpicbg.imglib.container.shapelist.ShapeListCached;
import mpicbg.imglib.type.Type;

public class ShapeListCacheFIFO<T extends Type< T > > extends ShapeListCache<T>
{
	final Map<Integer, T> cache;
	final LinkedList< Integer > queue;

	public ShapeListCacheFIFO( final int cacheSize, final ShapeListCached<T> container )
	{		
		super( cacheSize, container );
		
		cache = new HashMap<Integer, T>( cacheSize );
		queue = new LinkedList<Integer>( );
		
		for ( int i = 0; i < cacheSize; ++i )
			queue.add( Integer.MIN_VALUE );		
	}

	@Override
	public T lookUp( final int[] position )
	{
		final int index = fakeArray.getPos( position );

		final T value = cache.get( index );
		
		if ( value == null )
		{
			final T t = container.getShapeType( position );
			queue.add( index );
			cache.put( index , t );
			
			cache.remove( queue.removeFirst() );				
			
			return t;
		}
		else
		{
			return value;
		}		
	}

	@Override
	public ShapeListCache<T> getCursorCacheInstance() { return new ShapeListCacheFIFO<T>( cacheSize, container );	}
}
