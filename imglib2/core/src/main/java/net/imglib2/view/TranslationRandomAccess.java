package net.imglib2.view;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.Translation;

public final class TranslationRandomAccess< T > extends AbstractEuclideanSpace implements RandomAccess< T >
{
	private final RandomAccess< T > s;

	private final long[] translation;

	private final long[] tmp;

	TranslationRandomAccess( final RandomAccess< T > source, final Translation transformToSource )
	{
		super( transformToSource.numSourceDimensions() );

		assert source.numDimensions() == transformToSource.numTargetDimensions();
		s = source;

		translation = new long[ n ];
		transformToSource.getTranslation( translation );

		tmp = new long[ n ];
	}

	protected TranslationRandomAccess( final TranslationRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		s = randomAccess.s.copyRandomAccess();
		translation = randomAccess.translation.clone();
		tmp = new long[ n ];
	}

	@Override
	public void localize( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getIntPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public void localize( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getLongPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public int getIntPosition( final int d )
	{
		assert d <= n;
		return s.getIntPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		assert d <= n;
		return s.getLongPosition( d ) - ( int ) translation[ d ];
	}

	@Override
	public void localize( final float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getFloatPosition( d ) - translation[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getDoublePosition( d ) - translation[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		assert d <= n;
		return s.getFloatPosition( d ) - translation[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		assert d <= n;
		return s.getDoublePosition( d ) - translation[ d ];
	}

	@Override
	public void fwd( final int d )
	{
		s.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		s.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		s.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		s.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		s.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		s.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		s.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() == n;
		localizable.localize( tmp );
		for ( int d = 0; d < n; ++d )
			tmp[ d ] += translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d <= n;
		s.setPosition( position + translation[ d ], d);
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d <= n;
		s.setPosition( position + translation[ d ], d);
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public TranslationRandomAccess< T > copy()
	{
		return new TranslationRandomAccess< T >( this );
	}

	@Override
	public TranslationRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
