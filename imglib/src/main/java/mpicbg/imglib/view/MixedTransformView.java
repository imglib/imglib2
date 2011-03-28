package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.transform.integer.MixedTransform;

public class MixedTransformView< T > implements TransformedRandomAccessible< T >, RandomAccessibleInterval< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;
	
	protected final MixedTransform transformToSource;

	protected final long[] dimension;
	protected final long[] max;

	protected RandomAccessible< T > fullViewRandomAccessible;
		
	public MixedTransformView( RandomAccessible< T > source, MixedTransform transformToSource, long[] dim )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();
		assert dim.length == transformToSource.numSourceDimensions();

		this.n = transformToSource.numSourceDimensions();

		if ( MixedTransformView.class.isInstance( source ) )
		{
			MixedTransformView< T > v = ( MixedTransformView< T > ) source;
			this.source = v.getSource();
			this.transformToSource = v.getTransformToSource().concatenate( transformToSource );
		}
		else
		{
			this.source = source;
			final int sourceDim = this.source.numDimensions();
			this.transformToSource = new MixedTransform( n, sourceDim );
			this.transformToSource.set( transformToSource );
		}
		
		this.dimension = dim.clone();

		this.max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = this.dimension[ d ] - 1;
		
		fullViewRandomAccessible = null;
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
	public MixedTransform getTransformToSource()
	{
		return transformToSource;
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
	{
		return TransformBuilder.getEfficientRandomAccessible( this, this ).randomAccess(); 
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		if ( fullViewRandomAccessible == null )
			fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible( this, this ); 
		return fullViewRandomAccessible.randomAccess();
	}
}
