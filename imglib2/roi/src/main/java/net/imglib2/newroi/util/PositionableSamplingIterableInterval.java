package net.imglib2.newroi.util;

import java.util.Iterator;

import net.imglib2.AbstractWrappedPositionableInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.type.BooleanType;

public class PositionableSamplingIterableInterval< B extends BooleanType< B >, I extends IterableInterval< B > & Positionable & Localizable, T >
	extends AbstractWrappedPositionableInterval< I > implements PositionableIterableInterval< T >
{
	final RandomAccessible< T > target;

	public static < B extends BooleanType< B >, I extends IterableInterval< B > & Positionable & Localizable, T > PositionableSamplingIterableInterval< B, I, T > create( final I region, final RandomAccessible< T > target )
	{
		return new PositionableSamplingIterableInterval< B, I, T >( region, target );
	}

	public PositionableSamplingIterableInterval( final I region, final RandomAccessible< T > target )
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
