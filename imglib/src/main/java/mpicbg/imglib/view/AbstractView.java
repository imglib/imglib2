package mpicbg.imglib.view;

import java.util.Iterator;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.img.Img;

public class AbstractView< T > implements View< T >
{
	protected final int n;

	protected final long numPixels;

	protected final View< T > targetView;
	
	protected final ViewTransform thisViewsTransform;

	protected final ViewTransform cumulativeTransform;

	// cumulative offset wrt the underlying img.
	protected final long[] offset;
	
	protected final long[] dimension;
	protected final long[] max;

	AbstractView( View< T > target, long[] offset, long[] dim )
	{
		assert target.numDimensions() == offset.length;
		assert target.numDimensions() == dim.length;

		n = target.numDimensions();
		numPixels = numElements( dim );
		targetView = target;
		thisViewsTransform = new ViewTransform( offset );
		dimension = dim.clone();
		max = new long[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = dimension[ d ] - 1;

		cumulativeTransform = new ViewTransform( n );
		cumulativeTransform.set( thisViewsTransform );
		cumulativeTransform.concatenate( targetView.getViewTransform() );
		
		this.offset = new long[ n ];
		cumulativeTransform.getTranslation( this.offset );
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
		return new OffsetRandomAccess< T >( getImg().randomAccess(), offset );
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
	public Img< T > getImg()
	{
		return targetView.getImg();
	}
}
