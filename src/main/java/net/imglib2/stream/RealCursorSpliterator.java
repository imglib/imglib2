package net.imglib2.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import net.imglib2.RealCursor;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;

/**
 * Spliterator implementation on top of {@code RealCursor}.
 *
 * @param <T> the type of elements returned by this Spliterator, and the pixel type of the underlying cursor.
 */
public class RealCursorSpliterator< T > implements RealLocalizableSpliterator< T >
{
	/**
	 * The underlying cursor, positioned such that {@code cursor.next()} yields the element at {@code index}.
	 */
	private final RealCursor< T > cursor;

	/**
	 * The current index, modified on advance/split.
	 */
	private long index;

	/**
	 * One past last index
	 */
	private final long fence;

	/**
	 * Characteristics always include {@code SIZED | SUBSIZED}.
	 */
	private final int characteristics;

	/**
	 * Creates a spliterator covering the given range.
	 *
	 * @param cursor
	 * 		provides elements, starting with the element at origin, on cursor.next()
	 * @param origin
	 * 		the least index (inclusive) to cover
	 * @param fence
	 * 		one past the greatest index to cover
	 * @param additionalCharacteristics
	 * 		additional characteristics besides {@code SIZED | SUBSIZED}
	 */
	public RealCursorSpliterator( RealCursor< T > cursor, long origin, long fence, int additionalCharacteristics )
	{
		this.cursor = cursor;
		this.index = origin;
		this.fence = fence;
		this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();
		if ( index >= 0 && index < fence )
		{
			++index;
			action.accept( cursor.next() );
			return true;
		}
		return false;
	}

	@Override
	public RealCursorSpliterator< T > trySplit()
	{
		long lo = index, mid = ( lo + fence ) >>> 1;
		if ( lo >= mid )
			return null;
		else
		{
			final RealCursorSpliterator< T > prefix = new RealCursorSpliterator<>( cursor.copy(), lo, mid, characteristics );
			cursor.jumpFwd( mid - lo );
			index = mid;
			return prefix;
		}
	}

	@Override
	public long estimateSize()
	{
		return fence - index;
	}

	@Override
	public int characteristics()
	{
		return characteristics;
	}


	// -----------------------------------------------------------
	//   Sampler

	@Override
	public T get()
	{
		return cursor.get();
	}

	@Override
	public RealCursorSpliterator< T > copy()
	{
		return new RealCursorSpliterator<>( cursor.copy(), index, fence, characteristics );
	}


	// -----------------------------------------------------------
	//   RealLocalizable

	@Override
	public int numDimensions()
	{
		return cursor.numDimensions();
	}

	@Override
	public void localize( final float[] position )
	{
		cursor.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		cursor.localize( position );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		cursor.localize( position );
	}

	@Override
	public double[] positionAsDoubleArray()
	{
		return cursor.positionAsDoubleArray();
	}

	@Override
	public RealPoint positionAsRealPoint()
	{
		return cursor.positionAsRealPoint();
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return cursor.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return cursor.getDoublePosition( d );
	}
}
