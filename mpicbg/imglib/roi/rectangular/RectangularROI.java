package mpicbg.imglib.roi.rectangular;

import java.util.Iterator;

import mpicbg.imglib.InjectiveInterval;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

public class RectangularROI< T > implements RandomAccessible< T >, RandomAccessibleInterval< T, InjectiveInterval >, InjectiveInterval
{
	final int n;
	
	final RandomAccessible< T > source1;
	final RandomAccessibleInterval< T, InjectiveInterval > source2;
	final InjectiveInterval source3;
	
	public < S extends RandomAccessible< T > & RandomAccessibleInterval< T, InjectiveInterval > & InjectiveInterval >
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
	public Cursor<T> cursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor<T> localizingCursor()
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
	public void dimensions(long[] size)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public long dimension(int d)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RandomAccess<T> integerRandomAccess(OutOfBoundsFactory<T, InjectiveInterval> factory)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RandomAccess<T> randomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
