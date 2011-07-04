package net.imglib2.view;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.RandomAccess;
import net.imglib2.transform.Transform;

public final class SequentializeRandomAccess< T > extends AbstractRandomAccess< T >
{
	private final RandomAccess< T > s;
	
	private final Transform transformToSource;

	// source dimension
	private final int m;
	
	private final long[] tmp;

	SequentializeRandomAccess( RandomAccess< T > source, Transform transformToSource )
	{
		super( transformToSource.numSourceDimensions() );

		assert source.numDimensions() == transformToSource.numTargetDimensions();
		this.m = source.numDimensions();
		this.s = source;
		this.transformToSource = transformToSource;
		this.tmp = new long[ m ];
	}

	protected SequentializeRandomAccess( final SequentializeRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.s = randomAccess.s.copyRandomAccess();
		this.transformToSource = randomAccess.transformToSource;
		this.m = randomAccess.m;
		this.tmp = randomAccess.tmp.clone();
	}

	@Override
	public void fwd( int d )
	{
		position[ d ] += 1;
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void bck( int d )
	{
		position[ d ] -= 1;
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( long distance, int d )
	{
		position[ d ] += distance;
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( int[] position )
	{
		for( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
		}
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( long[] position )
	{
		for( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
		}
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( long position, int d )
	{
		this.position[ d ] = position; 
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public SequentializeRandomAccess< T > copy()
	{
		return new SequentializeRandomAccess< T >( this );
	}

	@Override
	public SequentializeRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
