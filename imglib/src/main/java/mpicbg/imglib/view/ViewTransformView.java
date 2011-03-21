package mpicbg.imglib.view;

import mpicbg.imglib.ExtendedRandomAccessibleInterval;
import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.transform.Transform;
import mpicbg.imglib.util.Pair;

public class ViewTransformView< T > implements TransformedRandomAccessibleIntervalView< T >
{
	protected final int n;

	protected final RandomAccessible< T > source;
	
	protected final ViewTransform transformToSource;

	protected final long[] dimension;
	protected final long[] max;

	protected final long[] tmpSourcePosition;
	protected final long[] tmpTargetPosition;

	protected final RandomAccessible< T > fullViewUntransformedRandomAccessible;

	protected final ViewTransform fullViewRandomAccessibleTransform;
		
	public ViewTransformView( RandomAccessible< T > source, ViewTransform transformToSource, long[] dim )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();
		assert dim.length == transformToSource.numSourceDimensions();

		this.n = dim.length;

		this.source = source;
		
		final int sourceDim = this.source.numDimensions();
		this.transformToSource = new ViewTransform( n, sourceDim );
		this.transformToSource.set( transformToSource );
		
		this.dimension = dim.clone();

		this.max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = this.dimension[ d ] - 1;
		
		this.tmpSourcePosition = new long[ sourceDim ];
		this.tmpTargetPosition = new long[ n ];

		Pair< RandomAccessible< T >, Transform > pair = untransformedRandomAccessible( this );
		this.fullViewUntransformedRandomAccessible = pair.a;
		this.fullViewRandomAccessibleTransform = ( ViewTransform ) pair.b;
	}

	public ViewTransformView( ViewTransformView< T > source, ViewTransform transformToSource, long[] dim )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();
		assert dim.length == transformToSource.numSourceDimensions();

		this.n = dim.length;

		this.source = source.getSource();
		
		final int sourceDim = this.source.numDimensions();
		this.transformToSource = new ViewTransform( n, sourceDim );
		ViewTransform.concatenate( source.getTransformToSource(), transformToSource, this.transformToSource );
		
		this.dimension = dim.clone();

		this.max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = this.dimension[ d ] - 1;
		
		this.tmpSourcePosition = new long[ sourceDim ];
		this.tmpTargetPosition = new long[ n ];

		Pair< RandomAccessible< T >, Transform > pair = untransformedRandomAccessible( this );
		this.fullViewUntransformedRandomAccessible = pair.a;
		this.fullViewRandomAccessibleTransform = ( ViewTransform ) pair.b;
	}
	
	public ViewTransformView( ExtendedRandomAccessibleInterval< T, ? > source, ViewTransform transformToSource, long[] dim )
	{
		assert source.numDimensions() == transformToSource.numTargetDimensions();
		assert dim.length == transformToSource.numSourceDimensions();

		this.n = dim.length;

		this.source = source;
		
		final int sourceDim = this.source.numDimensions();
		this.transformToSource = new ViewTransform( n, sourceDim );
		this.transformToSource.set( transformToSource );
		
		this.dimension = dim.clone();

		this.max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = this.dimension[ d ] - 1;
		
		this.tmpSourcePosition = new long[ sourceDim ];
		this.tmpTargetPosition = new long[ n ];

		Pair< RandomAccessible< T >, Transform > pair = untransformedRandomAccessible( this );
		this.fullViewUntransformedRandomAccessible = pair.a;
		this.fullViewRandomAccessibleTransform = ( ViewTransform ) pair.b;
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
	public Pair< RandomAccessible< T >, Transform > untransformedRandomAccessible( Interval interval )
	{
		System.out.println( "ViewTransformView.untransformedRandomAccessible in " + toString() );
		// apply our own transformToSource to the interval before we pass it on to the source 
		Interval transformedInterval = transformToSource.transform( interval );
		if ( RandomAccessibleView.class.isInstance( source ) )
		{
			// if the source is a View,
			// get an untransformedRandomAccessible pair from it
			// and concatenate with our own transformToSource before we return it
			Pair< RandomAccessible< T >, Transform > pair = ( ( RandomAccessibleView < T > ) source ).untransformedRandomAccessible( transformedInterval );
			Transform accessTransform = pair.b;
			if ( accessTransform == null )
			{
				return new Pair< RandomAccessible< T >, Transform >( pair.a, transformToSource );
			}
			else
			{
				if ( ViewTransform.class.isInstance( accessTransform ) ) {
					ViewTransform t = new ViewTransform( n, accessTransform.numTargetDimensions() );
					ViewTransform.concatenate( ( ViewTransform ) accessTransform, transformToSource, t );
					return new Pair< RandomAccessible< T >, Transform >( pair.a, t );
				} else {
					// TODO: concatenate to TransformList
					return null;
				}
			}
		}
		else
		{
			// if the source is not a View, just use it with our own transformToSource
			return new Pair< RandomAccessible< T >, Transform > ( source, transformToSource );
		}
	}
	
	@Override
	public Pair< RandomAccess< T >, Transform > untransformedRandomAccess( Interval interval )
	{
		System.out.println( "ViewTransformView.untransformedRandomAccess in " + toString() );
		// apply our own transformToSource to the interval before we pass it on to the source 
		Interval transformedInterval = transformToSource.transform( interval );
		if ( RandomAccessibleView.class.isInstance( source ) )
		{
			// if the source is a View,
			// get an untransformedRandomAccess pair from it
			// and concatenate with our own transformToSource before we return it
			Pair< RandomAccess< T >, Transform > pair = ( ( RandomAccessibleView < T > ) source ).untransformedRandomAccess( transformedInterval );
			Transform accessTransform = pair.b;
			if ( accessTransform == null )
			{
				return new Pair< RandomAccess< T >, Transform >( pair.a, transformToSource );
			}
			else
			{
				if ( ViewTransform.class.isInstance( accessTransform ) ) {
					ViewTransform t = new ViewTransform( n, accessTransform.numTargetDimensions() );
					ViewTransform.concatenate( ( ViewTransform ) accessTransform, transformToSource, t );
					return new Pair< RandomAccess< T >, Transform >( pair.a, t );
				} else {
					// TODO: concatenate to TransformList
					return null;
				}
			}
		}
		else
		{
			// if the source is not a View, just return a plain randomAccess and our own transformToSource
			return new Pair< RandomAccess< T >, Transform > ( source.randomAccess( transformedInterval ), transformToSource );
		}
	}

	@Override
	public ViewTransform getTransformToSource()
	{
		return transformToSource;
	}

	@Override
	public RandomAccessible< T > getSource()
	{
		return source;
	}

	@Override
	public RandomAccess< T > randomAccess( Interval interval )
	{
		Pair< RandomAccess< T >, Transform > pair = untransformedRandomAccess( interval );
		return new ViewTransformRandomAccess< T >( pair.a, ( ViewTransform ) pair.b );
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
//		Pair< RandomAccess< T >, ViewTransform > pair = untransformedRandomAccess( this );
//		return new ViewTransformRandomAccess< T >( pair.a, pair.b );
		return new ViewTransformRandomAccess< T >( fullViewUntransformedRandomAccessible.randomAccess(), fullViewRandomAccessibleTransform );
	}
}
