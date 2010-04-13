package mpicbg.imglib.container.shapelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class ShapeListCached<T extends Type<T> > extends ShapeList<T>
{
	final Array<FakeType, FakeAccess> fakeArray;
	
	final Map<Integer, T> cache;
	final LinkedList< Integer > queue;
	
	public ShapeListCached( final int[] dim, final T background, final int cacheSize )
	{
		super(dim, background);
		
		fakeArray = new Array<FakeType, FakeAccess>( null, new FakeArray(), dim, 1 );
		
		cache = new HashMap<Integer, T>( cacheSize );
		queue = new LinkedList<Integer>( );
		
		for ( int i = 0; i < cacheSize; ++i )
			queue.add( Integer.MIN_VALUE );
	}
	
	public ShapeListCached( final int[] dim, final T background )
	{
		this( dim, background, 8 );
	}

	@Override
	public T getShapeType( final int[] position )
	{
		final int index = fakeArray.getPos( position );

		final T value;
		
		synchronized ( cache )
		{
			value = cache.get( index );
		}
		
		if ( value == null )
		{
			final T t = super.getShapeType( position );
			
			synchronized ( cache )
			{
				queue.add( index );
				cache.put( index , t );
				
				cache.remove( queue.removeFirst() );				
			}
			
			return t;
		}
		else
		{
			return value;
		}
	}

}
