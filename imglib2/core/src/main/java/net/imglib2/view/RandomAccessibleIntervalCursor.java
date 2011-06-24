package net.imglib2.view;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.iterator.LocalizingIntervalIterator;

public class RandomAccessibleIntervalCursor< T > extends LocalizingIntervalIterator implements Cursor< T >
{
	final RandomAccess< T > randomAccess;

	public <I extends RandomAccessible< T > & Interval > RandomAccessibleIntervalCursor( final I interval )
	{
		super( interval );
		randomAccess = interval.randomAccess();
	}

	protected RandomAccessibleIntervalCursor( final RandomAccessibleIntervalCursor< T > cursor )
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
	public RandomAccessibleIntervalCursor< T > copy()
	{
		return new RandomAccessibleIntervalCursor< T >( this );
	}

	@Override
	public RandomAccessibleIntervalCursor< T > copyCursor()
	{
		return copy();
	}
}
