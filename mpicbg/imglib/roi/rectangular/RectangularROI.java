package mpicbg.imglib.roi.rectangular;

import java.util.Iterator;

import mpicbg.imglib.InjectiveIntegerInterval;
import mpicbg.imglib.IntegerCursor;
import mpicbg.imglib.IntegerRandomAccess;
import mpicbg.imglib.IntegerRandomAccessible;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccessibleIntegerInterval;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

public class RectangularROI< T > implements IntegerRandomAccessible< T >, RandomAccessibleIntegerInterval< T, InjectiveIntegerInterval >, InjectiveIntegerInterval
{
	final int n;
	
	final IntegerRandomAccessible< T > source1;
	final RandomAccessibleIntegerInterval< T, InjectiveIntegerInterval > source2;
	final InjectiveIntegerInterval source3;
	
	public < S extends IntegerRandomAccessible< T > & RandomAccessibleIntegerInterval< T, InjectiveIntegerInterval > & InjectiveIntegerInterval >
		RectangularROI( final long[] offset, final long[] size, final S source )
	{
		this.n = source.numDimensions();
		
		this.source1 = source;
		this.source2 = source;
		this.source3 = source;
	}
	
	@Override
	public int numDimensions() { return n; }

	@Override
	public IntegerCursor<T> cursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegerCursor<T> localizingCursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double realMin(int d)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void realMin(double[] min)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public double realMax(int d)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void realMax(double[] max)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<T> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long min(int d)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void min(long[] min)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public long max(int d)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void max(long[] max)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void size(long[] size)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public long size(int d)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IntegerRandomAccess<T> integerRandomAccess(OutOfBoundsFactory<T, InjectiveIntegerInterval> factory)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegerRandomAccess<T> integerRandomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
