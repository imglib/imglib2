package net.imglib2.newroi.util;

import java.util.Iterator;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

/**
 * Make a boolean {@link RandomAccessibleInterval} iterable. The resulting
 * {@link IterableInterval} contains all samples of the source interval that
 * evaluate to {@code true}.
 *
 * {@link Cursor Cursors} are realized by wrapping source {@link RandomAccess
 * RandomAccesses} (using {@link RandomAccessibleRegionCursor}).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class IterableRandomAccessibleRegion< T extends BooleanType< T > >
	extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements IterableInterval< T >
{
	final long size;

	public static < T extends BooleanType< T > > IterableRandomAccessibleRegion< T > create( final RandomAccessibleInterval< T > interval )
	{
		return new IterableRandomAccessibleRegion< T >( interval, Util.countTrue( interval ) );
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
		return new RandomAccessibleRegionCursor< T >( sourceInterval, size );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}
}
