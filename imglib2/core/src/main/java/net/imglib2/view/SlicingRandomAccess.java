package net.imglib2.view;

import net.imglib2.AbstractSampler;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.Slicing;

/**
 *
 * @author Tobias Pietzsch
 *
 * @param <T>
 */
public class SlicingRandomAccess< T > extends AbstractSampler< T > implements RandomAccess< T >
{
	/**
	 * source RandomAccess. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final RandomAccess< T > s;

	/**
	 * number of dimensions of source RandomAccess, respectively
	 * numTargetDimensions of the Slicing transform.
	 */
	private final int m;

	/**
	 * for each component of the source vector: to which target vector component
	 * should it be taken.
	 */
	private final int[] sourceComponent;

	private final long[] tmpPosition;

	private final long[] tmpDistance;

	SlicingRandomAccess( final RandomAccess< T > source, final Slicing transformToSource )
	{
		super( transformToSource.numSourceDimensions() );
		// n == transformToSource.numSourceDimensions()
		// m == transformToSource.numTargetDimensions()

		assert source.numDimensions() == transformToSource.numTargetDimensions();

		s = source;
		m = transformToSource.numTargetDimensions();
		final boolean[] targetZero = new boolean[ m ];
		final int[] targetComponent = new int[ m ];
		transformToSource.getComponentZero( targetZero );
		transformToSource.getComponentMapping( targetComponent );

		sourceComponent = new int[ n ];
		for ( int d = 0; d < m; ++d )
			if ( transformToSource.getComponentZero( d ) )
				s.setPosition( transformToSource.getTranslation( d ), d );
			else
				sourceComponent[ transformToSource.getComponentMapping( d ) ] = d;

		tmpPosition = new long[ m ];
		transformToSource.getTranslation( tmpPosition );

		tmpDistance = new long[ m ];
	}

	protected SlicingRandomAccess(  final SlicingRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );
		s = randomAccess.s;
		m = randomAccess.m;
		sourceComponent = randomAccess.sourceComponent.clone();
		tmpPosition = randomAccess.tmpPosition.clone();
		tmpDistance = randomAccess.tmpDistance.clone();
	}

	@Override
	public void localize( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getIntPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		assert d < n;
		return s.getIntPosition( sourceComponent[ d ] );
	}

	@Override
	public long getLongPosition( final int d )
	{
		assert d < n;
		return s.getLongPosition( sourceComponent[ d ] );
	}

	@Override
	public void localize( final float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	@Override
	public void localize( final double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		assert d < n;
		return s.getFloatPosition( sourceComponent[ d ] );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		assert d < n;
		return s.getDoublePosition( sourceComponent[ d ] );
	}

	@Override
	public void fwd( final int d )
	{
		s.fwd( sourceComponent[ d ] );
	}

	@Override
	public void bck( final int d )
	{
		s.bck( sourceComponent[ d ] );
	}

	@Override
	public void move( final int distance, final int d )
	{
		assert d < n;
		s.move( distance, sourceComponent[ d ] );
	}

	@Override
	public void move( final long distance, final int d )
	{
		assert d < n;
		s.move( distance, sourceComponent[ d ] );
	}

	@Override
	public void move( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in
		// tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
			tmpDistance[ sourceComponent[ d ] ] = localizable.getLongPosition( d );
		s.move( tmpDistance );
	}

	@Override
	public void move( final int[] distance )
	{
		assert distance.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
			tmpDistance[ sourceComponent[ d ] ] = distance[ d ];
		s.move( tmpDistance );
	}

	@Override
	public void move( final long[] distance )
	{
		assert distance.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target distance in tmpDistance[].
		// however, the missing components are already assigned to 0
		for ( int d = 0; d < n; ++d )
			tmpDistance[ sourceComponent[ d ] ] = distance[ d ];
		s.move( tmpDistance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
			tmpPosition[ sourceComponent[ d ] ] = localizable.getLongPosition( d );
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final int[] position )
	{
		assert position.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
			tmpPosition[ sourceComponent[ d ] ] = position[ d ];
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final long[] position )
	{
		assert position.length >= n;

		// we just loop over the source dimension.
		// this may not assign all components of the target position in
		// tmpPosition[].
		// however, the missing components are already assigned to the correct
		// translation components.
		for ( int d = 0; d < n; ++d )
			tmpPosition[ sourceComponent[ d ] ] = position[ d ];
		s.setPosition( tmpPosition );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d < n;
		s.setPosition( position, sourceComponent[ d ] );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d < n;
		s.setPosition( position, sourceComponent[ d ] );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public SlicingRandomAccess< T > copy()
	{
		return new SlicingRandomAccess< T >( this );
	}

	@Override
	public SlicingRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
