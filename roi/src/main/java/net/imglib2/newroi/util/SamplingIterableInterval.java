package net.imglib2.newroi.util;

import java.util.Iterator;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.BooleanType;

public class SamplingIterableInterval< B extends BooleanType< B >, T >
	extends AbstractWrappedInterval< IterableInterval< B > > implements IterableInterval< T >
{
	final RandomAccessible< T > target;

	public static < B extends BooleanType< B >, T > SamplingIterableInterval< B, T > create( final IterableInterval< B > region, final RandomAccessible< T > target )
	{
		return new SamplingIterableInterval< B, T >( region, target );
	}

	public SamplingIterableInterval( final IterableInterval< B > region, final RandomAccessible< T > target )
	{
		super( region );
		this.target = target;
	}

	@Override
	public Cursor< T > cursor()
	{
		return new SamplingCursor< B, T >( sourceInterval.cursor(), target.randomAccess( sourceInterval ) );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return new SamplingCursor< B, T >( sourceInterval.localizingCursor(), target.randomAccess( sourceInterval ) );
	}

	@Override
	public long size()
	{
		return sourceInterval.size();
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return sourceInterval.iterationOrder();
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
}
