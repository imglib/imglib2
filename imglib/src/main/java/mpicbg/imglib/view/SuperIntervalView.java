package mpicbg.imglib.view;

import java.util.Iterator;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

public class SuperIntervalView< T > implements AbstractView< T >
{

	@Override
	public RandomAccess< T > randomAccess( OutOfBoundsFactory< T, Img< T >> factory )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor< T > cursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor< T > localizingCursor()
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
	public T firstElement()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equalIterationOrder( IterableRealInterval< ? > f )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double realMin( int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void realMin( double[] min )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public double realMax( int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void realMax( double[] max )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int numDimensions()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator< T > iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long min( int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void min( long[] min )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public long max( int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void max( long[] max )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dimensions( long[] dimensions )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public long dimension( int d )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
