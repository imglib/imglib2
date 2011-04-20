package mpicbg.imglib.view;

import mpicbg.imglib.AbstractSampler;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.transform.integer.Translation;

public final class TranslationRandomAccess< T > extends AbstractSampler< T > implements RandomAccess< T >
{
	private final RandomAccess< T > s;
	
	private final long[] translation;

	private final long[] tmp;

	TranslationRandomAccess( RandomAccess< T > source, Translation transformToSource )
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

		s = randomAccess.s.copy();
		translation = randomAccess.translation.clone();
		tmp = new long[ n ];
	}

	@Override
	public void localize( int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getIntPosition( d ) + ( int ) translation[ d ];
	}

	@Override
	public void localize( long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getLongPosition( d ) + ( int ) translation[ d ];
	}

	@Override
	public int getIntPosition( int d )
	{
		assert d <= n;
		return s.getIntPosition( d ) + ( int ) translation[ d ];
	}

	@Override
	public long getLongPosition( int d )
	{
		assert d <= n;
		return s.getLongPosition( d ) + ( int ) translation[ d ];
	}

	@Override
	public void localize( float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getFloatPosition( d ) + translation[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = s.getDoublePosition( d ) + translation[ d ];
	}

	@Override
	public float getFloatPosition( int d )
	{
		assert d <= n;
		return s.getFloatPosition( d ) + translation[ d ];
	}

	@Override
	public double getDoublePosition( int d )
	{
		assert d <= n;
		return s.getDoublePosition( d ) + translation[ d ];
	}

	@Override
	public void fwd( int d )
	{
		s.fwd( d );
	}

	@Override
	public void bck( int d )
	{
		s.bck( d );
	}

	@Override
	public void move( int distance, int d )
	{
		s.move( distance, d );
	}

	@Override
	public void move( long distance, int d )
	{
		s.move( distance, d );
	}

	@Override
	public void move( Localizable localizable )
	{
		s.move( localizable );
	}

	@Override
	public void move( int[] distance )
	{
		s.move( distance );
	}

	@Override
	public void move( long[] distance )
	{
		s.move( distance );
	}

	@Override
	public void setPosition( Localizable localizable )
	{
		assert localizable.numDimensions() >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = localizable.getLongPosition( d ) + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] + translation[ d ];
		s.setPosition( tmp );
	}

	@Override
	public void setPosition( int position, int d )
	{
		assert d <= n;
		s.setPosition( position + translation[ d ], d);
	}

	@Override
	public void setPosition( long position, int d )
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
}
