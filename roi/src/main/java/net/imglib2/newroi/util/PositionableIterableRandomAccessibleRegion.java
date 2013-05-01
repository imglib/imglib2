package net.imglib2.newroi.util;

import java.util.Iterator;

import net.imglib2.AbstractWrappedPositionableInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

/**
 * Make a positionable boolean {@link RandomAccessibleInterval} iterable. The
 * resulting {@link IterableInterval} is positionable as well. It contains all
 * samples of the source interval that evaluate to {@code true}.
 *
 * {@link Cursor Cursors} are realized by wrapping source {@link RandomAccess
 * RandomAccesses} (using {@link RandomAccessibleRegionCursor}).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class PositionableIterableRandomAccessibleRegion< T extends BooleanType< T >, I extends RandomAccessibleInterval< T > & Localizable & Positionable >
	extends AbstractWrappedPositionableInterval< I > implements PositionableIterableInterval< T >
{
	final long size;

	public static < T extends BooleanType< T >, I extends RandomAccessibleInterval< T > & Localizable & Positionable > PositionableIterableRandomAccessibleRegion< T, I > create( final I interval )
	{
		return new PositionableIterableRandomAccessibleRegion< T, I >( interval, Util.countTrue( interval ) );
	}

	public PositionableIterableRandomAccessibleRegion( final I interval, final long size )
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
		return new RandomAccessibleRegionCursor< T >( sourceInterval, size );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}
}
