package mpicbg.imglib.container.newcell;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.IntervalIndexer;

public class Cell< T extends NativeType< T >, A extends DataAccess > extends Array< T, A > implements Type< Cell< T, A > > 
{
	final long[] offset;
	
	public Cell( final T type, final A data, final long[] dim, final long[] offset, final int entitiesPerPixel )
	{
		super( type, data, dim, entitiesPerPixel );

		this.offset = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.offset[ d ] = offset[ d ];
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

	public void offset( final long[] o )
	{
		for ( int d = 0; d < n; ++d )
			o[ d ] = offset[ d ];
	}

	public long indexToGlobalPosition( int index, int dimension )
	{
		return IntervalIndexer.indexToPosition( index, dim, steps, dimension ) + offset[ dimension ];
	}

	public void indexToGlobalPosition( int index, final long[] position )
	{
		IntervalIndexer.indexToPosition( index, dim, position );
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += offset[ d ];
	}
	
	public int localPositionToIndex( final long[] position )
	{
		return IntervalIndexer.positionToIndex( position, dim );
	}
}
