package mpicbg.imglib.view;

import mpicbg.imglib.AbstractSampler;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.transform.integer.Mixed;

/**
 * Wrap a {@code source} RandomAccess which is related to this by a {@link Mixed}
 * {@code transformToSource}.
 * This is for {@link Mixed} transforms that feature a full mapping of source to
 * target components. That is, there is no down-projection, no source component is discarded.
 * In this case, the current position can be recovered from the position of the source RandomAccess.
 * Localize can be implemented via localize on the source RandomAccess.
 *
 * <p>
 * For the general case, see {@link MixedRandomAccess}.
 * </p>
 * 
 * @author Tobias Pietzsch
 * 
 * @param <T>
 */
public final class FullSourceMapMixedRandomAccess< T > extends AbstractSampler< T > implements RandomAccess< T >
{
	/**
	 * source RandomAccess. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final RandomAccess< T > s;

	/**
	 * number of dimensions of source RandomAccess, respectively
	 * numTargetDimensions of the Mixed transform.
	 */
	private final int m;

	private final long[] translation;

	/**
	 * for each component of the source vector: should the target vector
	 * component be inverted (true).
	 */
	private final boolean[] sourceInv;

	/**
	 * for each component of the source vector: to which target vector component
	 * should it be taken.
	 */
	private final int[] sourceComponent;

	private final long[] tmpPosition;

	private final long[] tmpDistance;

	FullSourceMapMixedRandomAccess( RandomAccess< T > source, Mixed transformToSource )
	{
		super( transformToSource.numSourceDimensions() );
		// n == transformToSource.numSourceDimensions()
		// m == transformToSource.numTargetDimensions()

		assert source.numDimensions() == transformToSource.numTargetDimensions();

		s = source;
		m = transformToSource.numTargetDimensions();
		translation = new long[ m ];
		final boolean[] targetZero = new boolean[ m ];
		final boolean[] targetInv = new boolean[ m ];
		final int[] targetComponent = new int[ m ];
		transformToSource.getTranslation( translation );
		transformToSource.getComponentZero( targetZero );
		transformToSource.getComponentMapping( targetComponent );
		transformToSource.getComponentInversion( targetInv );

		sourceInv = new boolean[ n ];
		sourceComponent = new int[ n ];
		for ( int d = 0; d < m; ++d )
		{
			if ( !targetZero[ d ] )
			{
				final int e = targetComponent[ d ];
				// sourceZero[ e ] = false;
				sourceInv[ e ] = targetInv[ d ];
				sourceComponent[ e ] = d;
			}
		}

		tmpPosition = translation.clone();
		tmpDistance = new long[ m ];
	}

	protected FullSourceMapMixedRandomAccess( final FullSourceMapMixedRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.s = randomAccess.s.copy();
		this.m = randomAccess.m; 
		this.translation = randomAccess.translation.clone();
		this.sourceInv = randomAccess.sourceInv.clone();
		this.sourceComponent = randomAccess.sourceComponent.clone();

		tmpPosition = translation.clone();
		tmpDistance = new long[ m ];
	}

	@Override
	public void localize( int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getIntPosition( d );
	}

	@Override
	public void localize( long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	@Override
	public int getIntPosition( int d )
	{
		assert d < n;
		final int td = sourceComponent[ d ];
		final int v = s.getIntPosition( td ) - ( int ) translation[ td ];
		return sourceInv[ d ] ? -v : v;
	}

	@Override
	public long getLongPosition( int d )
	{
		assert d < n;
		final int td = sourceComponent[ d ];
		final long v = s.getLongPosition( td ) - translation[ td ];
		return sourceInv[ d ] ? -v : v;
	}

	@Override
	public void localize( float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	@Override
	public void localize( double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return ( float ) getLongPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return ( double ) getLongPosition( d );
	}

	@Override
	public void fwd( int d )
	{
		assert d < n;
		if ( sourceInv[ d ] )
			s.bck( sourceComponent[ d ] );
		else
			s.fwd( sourceComponent[ d ] );
	}

	@Override
	public void bck( int d )
	{
		assert d < n;
		if ( sourceInv[ d ] )
			s.fwd( sourceComponent[ d ] );
		else
			s.bck( sourceComponent[ d ] );
	}

	@Override
	public void move( int distance, int d )
	{
		assert d < n;
		s.move( sourceInv[ d ] ? -distance : distance, sourceComponent[ d ] );
	}

	@Override
	public void move( long distance, int d )
	{
		assert d < n;
		s.move( sourceInv[ d ] ? -distance : distance, sourceComponent[ d ] );
	}

	@Override
	public void move( Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in
		// tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
		{
			final int td = sourceComponent[ d ];
			final long p = localizable.getLongPosition( d );
			tmpDistance[ td ] = sourceInv[ d ] ? -p : p;
		}
		s.move( tmpDistance );
	}

	@Override
	public void move( int[] distance )
	{
		assert distance.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
		{
			final int td = sourceComponent[ d ];
			tmpDistance[ td ] = sourceInv[ d ] ? -distance[ d ] : distance[ d ];
		}
		s.move( tmpDistance );
	}

	@Override
	public void move( long[] distance )
	{
		assert distance.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
		{
			final int td = sourceComponent[ d ];
			tmpDistance[ td ] = sourceInv[ d ] ? -distance[ d ] : distance[ d ];
		}
		s.move( tmpDistance );
	}

	@Override
	public void setPosition( Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
		{
			final int td = sourceComponent[ d ];
			final long p = localizable.getLongPosition( d );
			tmpPosition[ td ] = translation[ td ] + ( sourceInv[ d ] ? -p : p );
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( int[] position )
	{
		assert position.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
		{
			final int td = sourceComponent[ d ];
			tmpPosition[ td ] = translation[ td ] + ( sourceInv[ d ] ? -position[ d ] : position[ d ] );
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( long[] position )
	{
		assert position.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
		{
			final int td = sourceComponent[ d ];
			tmpPosition[ td ] = translation[ td ] + ( sourceInv[ d ] ? -position[ d ] : position[ d ] );
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( int position, int d )
	{
		assert d < n;
		final int td = sourceComponent[ d ];
		final int targetPos = ( int ) translation[ td ] + ( sourceInv[ d ] ? -position : position );
		s.setPosition( targetPos, td );
	}

	@Override
	public void setPosition( long position, int d )
	{
		assert d < n;
		final int td = sourceComponent[ d ];
		final long targetPos = translation[ td ] + ( sourceInv[ d ] ? -position : position );
		s.setPosition( targetPos, td );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public FullSourceMapMixedRandomAccess< T > copy()
	{
		return new FullSourceMapMixedRandomAccess< T >( this );
	}
}
