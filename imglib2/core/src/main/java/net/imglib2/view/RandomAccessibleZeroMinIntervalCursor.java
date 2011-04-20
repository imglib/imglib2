package net.imglib2.view;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;

public final class RandomAccessibleZeroMinIntervalCursor< T > extends LocalizingZeroMinIntervalIterator implements Cursor< T >
{
	final RandomAccess< T > randomAccess;

	public <I extends RandomAccessible< T > & Interval > RandomAccessibleZeroMinIntervalCursor( final I interval )
	{
		super( interval );
		randomAccess = interval.randomAccess();
	}

	protected RandomAccessibleZeroMinIntervalCursor( final RandomAccessibleZeroMinIntervalCursor< T > cursor )
	{
		super( cursor );
		this.randomAccess = cursor.randomAccess.copyRandomAccess();
	}

	@Override
	public T get()
	{
		randomAccess.setPosition( position );
		return randomAccess.get();
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
	}

	@Override
	public RandomAccessibleZeroMinIntervalCursor< T > copy()
	{
		return new RandomAccessibleZeroMinIntervalCursor< T >( this );
	}

	@Override
	public RandomAccessibleZeroMinIntervalCursor< T > copyCursor()
	{
		return copy();
	}
}
