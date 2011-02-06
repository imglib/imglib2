package mpicbg.imglib.container.newcell;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.IntervalIndexer;

public class Cell< T extends NativeType< T >, A extends DataAccess > extends Array< T, A > implements Type< Cell< T, A > > 
{
	final long[] offset;
	
	public Cell( final T type, final A data, final long[] dim, final int entitiesPerPixel )
	{
		super( type, data, dim, entitiesPerPixel );
		offset = new long[ dim.length ];
	}

	@Override
	public Cell<T, A> copy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set( Cell<T, A> c )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Cell<T, A> createVariable()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long indexToGlobalPosition( int index, int dimension )
	{
		return IntervalIndexer.indexToPosition( index, dim, steps, dimension ) + offset[ dimension ];
	}

	public void indexToGlobalPosition( int index, long[] position )
	{
		IntervalIndexer.indexToPosition( index, dim, position );
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += offset[ d ];
	}
}
