package mpicbg.imglib.view;

import mpicbg.imglib.ExtendedRandomAccessibleInterval;
import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.util.Pair;

public class ViewTransformView< T > implements TransformingIntervalView< T >
{
	protected final int n;

	protected final RandomAccessible< T > targetImg;
	
	protected final ViewTransform cumulativeTransform;

	protected final long[] dimension;
	protected final long[] max;

	protected final long[] tmpSourcePosition;
	protected final long[] tmpTargetPosition;

	public ViewTransformView( RandomAccessible< T > target, ViewTransform transform, long[] dim )
	{
		assert target.numDimensions() == transform.targetDim();
		assert dim.length == transform.sourceDim();

		n = transform.sourceDim();

		targetImg = target;
		
		final int targetDim = targetImg.numDimensions();
		cumulativeTransform = new ViewTransform( n, targetDim );
		cumulativeTransform.set( transform );
		
		dimension = dim.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;
		
		tmpSourcePosition = new long[ n ];
		tmpTargetPosition = new long[ targetDim ];
	}

	public ViewTransformView( ViewTransformView< T > target, ViewTransform transform, long[] dim )
	{
		assert target.numDimensions() == transform.targetDim();
		assert dim.length == transform.sourceDim();

		n = transform.sourceDim();

		targetImg = target.getTargetRandomAccessible();
		
		final int targetDim = targetImg.numDimensions();
		cumulativeTransform = new ViewTransform( n, targetDim );
		ViewTransform.concatenate( target.getViewTransform(), transform, cumulativeTransform );
		
		dimension = dim.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;
		
		tmpSourcePosition = new long[ n ];
		tmpTargetPosition = new long[ targetDim ];
	}
	
	public ViewTransformView( ExtendedRandomAccessibleInterval< T, ? > target, ViewTransform transform, long[] dim )
	{
		assert target.numDimensions() == transform.targetDim();
		assert dim.length == transform.sourceDim();

		n = transform.sourceDim();

		targetImg = target;
		
		final int targetDim = targetImg.numDimensions();
		cumulativeTransform = new ViewTransform( n, targetDim );
		cumulativeTransform.set( transform );
		
		dimension = dim.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;
		
		tmpSourcePosition = new long[ n ];
		tmpTargetPosition = new long[ targetDim ];
	}
			
	@Override
	public int numDimensions() { return n; }
	
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
	public Pair< RandomAccess< T >, ViewTransform > untransformedRandomAccess( Interval interval )
	{
		System.out.println( "ViewTransformView.untransformedRandomAccess in " + toString() );
		Interval transformedInterval = cumulativeTransform.transform( interval );
		if ( View.class.isInstance( targetImg ) )
		{
			Pair< RandomAccess< T >, ViewTransform > pair = ( ( View < T > ) targetImg ).untransformedRandomAccess( transformedInterval );
			ViewTransform accessTransform = pair.b;
			if ( accessTransform == null )
			{
				return new Pair< RandomAccess< T >, ViewTransform >( pair.a, cumulativeTransform );
			}
			else
			{
				ViewTransform t = new ViewTransform( n, accessTransform.targetDim );
				ViewTransform.concatenate( accessTransform, cumulativeTransform, t );
				return new Pair< RandomAccess< T >, ViewTransform >( pair.a, t );
			}
		}
		else
		{
			return new Pair< RandomAccess< T >, ViewTransform > ( targetImg.randomAccess( transformedInterval ), cumulativeTransform );
		}
	}

	@Override
	public ViewTransform getViewTransform()
	{
		return cumulativeTransform;
	}

	@Override
	public RandomAccessible< T > getTargetRandomAccessible()
	{
		return targetImg;
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
	{
		Pair< RandomAccess< T >, ViewTransform > pair = untransformedRandomAccess( interval );
		return new ViewTransformRandomAccess< T >( pair.a, pair.b );
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		Pair< RandomAccess< T >, ViewTransform > pair = untransformedRandomAccess( this );
		return new ViewTransformRandomAccess< T >( pair.a, pair.b );
	}
}
