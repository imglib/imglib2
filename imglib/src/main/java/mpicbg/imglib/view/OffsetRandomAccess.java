package mpicbg.imglib.view;

import mpicbg.imglib.AbstractRandomAccess;
import mpicbg.imglib.RandomAccess;

/**
 * TODO: extend AbstractSampler and implement RandomAccess< T > interface directly
 *       (keeping our own position[] in addition to target's is not very efficient...)
 * 
 * @author Tobias Pietzsch
 *
 * @param <T>
 */
public final class OffsetRandomAccess< T > extends AbstractRandomAccess< T >
{
	private final RandomAccess< T > target;
	
	private final long[] offset;
	
	private final long[] tmp;

	OffsetRandomAccess( RandomAccess< T > target, final long[] offset )
	{
		super( target.numDimensions() );
		this.target = target;
		this.offset = offset.clone();
		tmp = new long[ n ];
	}

	@Override
	public void fwd( int d )
	{
		position[ d ] += 1;
		target.fwd( d );
	}

	@Override
	public void bck( int d )
	{
		position[ d ] -= 1;
		target.bck( d );
	}

	@Override
	public void move( long distance, int d )
	{
		position[ d ] += distance;
		target.move( distance, d );
	}

	@Override
	public void setPosition( int[] position )
	{
		for( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			tmp[ d ] = position[ d ] + offset[ d ];
		}
		target.setPosition( tmp );
	}

	@Override
	public void setPosition( long[] position )
	{
		for( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			tmp[ d ] = position[ d ] + offset[ d ];
		}
		target.setPosition( tmp );
	}

	@Override
	public void setPosition( long position, int d )
	{
		this.position[ d ] = position;
		target.setPosition( position + offset[ d ], d );
	}

	@Override
	public T get()
	{
		return target.get();
	}
}
