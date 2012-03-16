package net.imglib2.view;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.Transform;

public final class SequentializeRandomAccess< T > extends AbstractLocalizable implements RandomAccess< T >
{
	private final RandomAccess< T > s;

	private final Transform transformToSource;

	// source dimension
	private final int m;

	private final long[] tmp;

	SequentializeRandomAccess( final RandomAccess< T > source, final Transform transformToSource )
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
	public void fwd( final int d )
	{
		++position[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += localizable.getLongPosition( d );
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( position );
		transformToSource.apply( position, tmp );
		s.setPosition( tmp );
	}
	@Override
	public void setPosition( final int[] position )
	{
		for( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		this.position[ d ] = position;
		transformToSource.apply( this.position, tmp );
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final long position, final int d )
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
