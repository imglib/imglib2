package mpicbg.imglib.view;

import java.util.Iterator;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

final public class ExtendableRandomAccessibleIntervalImp< T, F extends RandomAccessibleInterval< T, F > > implements ExtendableRandomAccessibleInterval< T > 
{
	final protected F interval;
	final protected OutOfBoundsFactory< T, F > factory;

	final protected int n;
	
	public ExtendableRandomAccessibleIntervalImp( final F interval, final OutOfBoundsFactory< T, F > factory )
	{
		this.interval = interval;
		this.factory = factory;
		this.n = interval.numDimensions();
	}
	
	@Override
	final public long dimension( final int d )
	{
		return interval.dimension( d );
	}

	@Override
	final public void dimensions( final long[] dimensions )
	{
		interval.dimensions( dimensions );
	}

	@Override
	final public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	final public void max( final long[] max )
	{
		interval.max( max );
	}

	@Override
	final public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	final public void min( final long[] min )
	{
		interval.min( min );
	}

	@Override
	final public double realMax( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	final public void realMax( final double[] max )
	{
		realMax( max );
	}

	@Override
	final public double realMin( final int d )
	{
		return interval.realMin( d );
	}

	@Override
	final public void realMin( final double[] min )
	{
		realMin( min );
	}

	@Override
	final public int numDimensions()
	{
		return interval.numDimensions();
	}

	@Override
	final public RandomAccess< T > randomAccess()
	{
		System.out.println( "using normal randomAccess");
		return interval.randomAccess();
	}

	@Override
	public Cursor< T > cursor()
	{
		return interval.cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return interval.localizingCursor();
	}

	@Override
	public long size()
	{
		return interval.size();
	}

	@Override
	public T firstElement()
	{
		return interval.firstElement();
	}

	@Override
	public boolean equalIterationOrder( IterableRealInterval< ? > f )
	{
		return interval.equalIterationOrder( f );
	}

	@Override
	public Iterator< T > iterator()
	{
		return interval.iterator();
	}

	@Override
	final public boolean isOutOfBounds( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( position[ d ] < min( d ) || position[ d ] > max( d ) )
				return true;
		}
		return false;
	}
	
	@Override
	final public RandomAccess< T > extendedRandomAccess()
	{
		System.out.println( "using extended randomAccess");
		return interval.randomAccess( factory );
	}

	@Override
	final public ViewTransform getViewTransform ()
	{
		// TODO: There should be a singleton Identity Transform Object
		return new ViewTransform( n, n );
	}

	@Override
	final public ExtendableRandomAccessibleInterval< T > getImg()
	{
		return this;
	}

	@Override
	public RandomAccess< T > randomAccess( OutOfBoundsFactory< T, View< T > > outOfBoundsFactory )
	{
		return new ViewOutOfBoundsRandomAccess< T >( this, outOfBoundsFactory );
	}
}
