package mpicbg.imglib.view;

import java.util.Iterator;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.img.array.ArrayOutOfBoundsRandomAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

public class ViewTransformView< T > implements View< T >
{
	protected final int n;

	protected final long numPixels;

	protected final ExtendableRandomAccessibleInterval< T > targetImg;
	
	protected final ViewTransform cumulativeTransform;

	protected final long[] dimension;
	protected final long[] max;

	protected final long[] tmpSourcePosition;
	protected final long[] tmpTargetPosition;

	public ViewTransformView( View< T > target, ViewTransform transform, long[] dim )
	{
		assert target.numDimensions() == transform.targetDim();
		assert dim.length == transform.sourceDim();

		n = transform.sourceDim();

		numPixels = numElements( dim );

		targetImg = target.getImg();
		
		final int targetDim = target.getViewTransform().targetDim();
		cumulativeTransform = new ViewTransform( n, targetDim );
		ViewTransform.concatenate( target.getViewTransform(), transform, cumulativeTransform );
		
		dimension = dim.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;
		
		tmpSourcePosition = new long[ n ];
		tmpTargetPosition = new long[ targetDim ];
	}
	
	@Override
	public Iterator<T> iterator()
	{ 
		return cursor();
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}
	
	public static long numElements( final long[] dim )
	{
		long numPixels = 1;		
		
		for ( int i = 0; i < dim.length; ++i )
			numPixels *= dim[ i ];
		
		return numPixels;		
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
	public long size() { return numPixels; }

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
	public boolean equalIterationOrder( IterableRealInterval< ? > f )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		for ( int d = 0; d < n; ++d )
		{
			tmpSourcePosition[ d ] = 0;
		}
		cumulativeTransform.transform( tmpSourcePosition, tmpTargetPosition );
		if ( ! targetImg.isOutOfBounds( tmpTargetPosition ) )
		{
			for ( int d = 0; d < n; ++d )
			{
				tmpSourcePosition[ d ] = max[ d ];
			}
			cumulativeTransform.transform( tmpSourcePosition, tmpTargetPosition );
			if ( ! targetImg.isOutOfBounds( tmpTargetPosition ) )
			{
				return new ViewTransformRandomAccess< T >( targetImg.randomAccess(), cumulativeTransform );
			}
		}
		return new ViewTransformRandomAccess< T >( targetImg.extendedRandomAccess(), cumulativeTransform );
	}

	@Override
	public Cursor< T > cursor()
	{
		return new RandomAccessibleZeroMinIntervalCursor< T >( this );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return new RandomAccessibleZeroMinIntervalCursor< T >( this );
	}

	@Override
	public ViewTransform getViewTransform()
	{
		return cumulativeTransform;
	}

	@Override
	public ExtendableRandomAccessibleInterval< T > getImg()
	{
		return targetImg;
	}

	@Override
	public RandomAccess< T > randomAccess( OutOfBoundsFactory< T, View< T > > outOfBoundsFactory )
	{
		return new ViewOutOfBoundsRandomAccess< T >( this, outOfBoundsFactory );
	}

}
