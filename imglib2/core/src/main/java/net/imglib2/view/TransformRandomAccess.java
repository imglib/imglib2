package net.imglib2.view;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.Transform;

/**
 * Wrap a {@code source} RandomAccess which is related to this by a generic
 * {@link Transform} {@code transformToSource}.
 *  
 * @author Tobias Pietzsch
 */
public final class TransformRandomAccess< T > extends AbstractRandomAccess< T >
{
	/**
	 * source RandomAccess. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final RandomAccess< T > source;
	
	private final Transform transformToSource;

	private final long[] tmp;

	TransformRandomAccess( RandomAccess< T > source, Transform transformToSource )
	{
		super( transformToSource.numSourceDimensions() );
		this.source = source;
		this.transformToSource = transformToSource;
		this.tmp = new long[ transformToSource.numTargetDimensions() ];
	}

	protected TransformRandomAccess( final TransformRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );
		this.source = randomAccess.source.copyRandomAccess();
		this.transformToSource = randomAccess.transformToSource;
		this.tmp = new long[ randomAccess.tmp.length ];
	}
	
	@Override
	public void fwd( int d )
	{
		assert d < n;
		position[ d ] += 1;
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void bck( int d )
	{
		assert d < n;
		position[ d ] -= 1;
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void move( long distance, int d )
	{
		assert d < n;
		position[ d ] += distance;
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void move( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		for ( int d = 0; d < n; ++d )
			position[ d ] += localizable.getLongPosition( d );
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void move( final int[] distance )
	{
		assert distance.length >= n;

		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void move( final long[] distance )
	{
		assert distance.length >= n;

		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		for ( int d = 0; d < n; ++d )
			this.position[ d ] = localizable.getLongPosition( d );
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( int[] pos )
	{
		assert pos.length >= n;

		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( long[] pos )
	{
		assert pos.length >= n;

		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( long pos, int d )
	{
		assert d < n;
		position[ d ] = pos;
		transformToSource.apply( position, tmp );
		source.setPosition( tmp );
	}

	@Override
	public T get()
	{
		return source.get();
	}

	@Override
	public TransformRandomAccess< T > copy()
	{
		return new TransformRandomAccess< T >( this );
	}

	@Override
	public TransformRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
