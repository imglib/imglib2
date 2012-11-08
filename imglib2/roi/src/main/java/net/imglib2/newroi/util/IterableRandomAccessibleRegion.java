package net.imglib2.newroi.util;

import java.util.Iterator;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.view.RandomAccessibleIntervalCursor;

public class IterableRandomAccessibleRegion< T extends BooleanType< T > > extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements IterableInterval< T >
{
	final long size;

	public static < T extends BooleanType< T > > IterableRandomAccessibleRegion< T > create( final RandomAccessibleInterval< T > interval )
	{
		return new IterableRandomAccessibleRegion< T >( interval, 1 /*TODO*/ );
	}

	public IterableRandomAccessibleRegion( final RandomAccessibleInterval< T > interval, final long size )
	{
		super( interval );
		this.size = size;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< T > cursor()
	{
		return new RandomAccessibleIntervalCursor< T >( sourceInterval );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}
}
