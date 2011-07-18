package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.transform.Transform;
import net.imglib2.transform.integer.SequentializeTransform;

public class SequentializeView< T > implements TransformedRandomAccessible< T >, RandomAccessibleInterval< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;
	
	protected final SequentializeTransform transformFromSource;

	protected final long[] dimension;
	protected final long[] max;
	
	public SequentializeView( RandomAccessibleInterval< T > source, final int numDimensions )
	{
		assert numDimensions <= source.numDimensions();
		
		this.n = numDimensions;
		this.source = source;

		final int m = source.numDimensions();
		final long[] sourceDim = new long[ m ];
		source.dimensions( sourceDim );
		this.transformFromSource = new SequentializeTransform( sourceDim, n);

		this.dimension = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.dimension[ d ] = sourceDim[ d ];
		for ( int d = n; d < m; ++d )
			this.dimension[ n-1 ] *= sourceDim[ d ];
		
		this.max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = this.dimension[ d ] - 1;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new SequentializeRandomAccess< T >( source.randomAccess(), transformFromSource.inverse() );
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
	{
		return new SequentializeRandomAccess< T >( source.randomAccess(), transformFromSource.inverse() );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}
	
	@Override
	public void dimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = dimension[ i ];
	}

	@Override
	public long dimension( final int d )
	{
		try { return this.dimension[ d ]; }
		catch ( ArrayIndexOutOfBoundsException e ) { return 1; }
	}
	
	@Override
	public String toString()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring( className.lastIndexOf(".") + 1, className.length());
		
		String description = className + " [" + dimension[ 0 ];
		
		for ( int i = 1; i < n; ++i )
			description += "x" + dimension[ i ];
		
		description += "]";
		
		return description;
	}

	@Override
	public double realMax( int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public double realMin( int d )
	{
		return 0;
	}

	@Override
	public void realMin( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}

	@Override
	public long max( int d )
	{
		return max[ d ];
	}

	@Override
	public void max( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public long min( int d )
	{
		return 0;
	}

	@Override
	public void min( final long[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = 0;
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public Transform getTransformToSource()
	{
		return transformFromSource.inverse();
	}

	@Override
	public void min( Positionable m )
	{
		for ( int d = 0; d < n; ++d )
			m.setPosition( 0, d );
	}

	@Override
	public void max( Positionable m )
	{
		m.setPosition( max );
	}

	@Override
	public void realMin( RealPositionable m )
	{
		for ( int d = 0; d < n; ++d )
			m.setPosition( 0, d );
	}

	@Override
	public void realMax( RealPositionable m )
	{
		m.setPosition( max );
	}
}
