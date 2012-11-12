package net.imglib2.newroi.util;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

public class RandomAccessibleRegionCursor< T extends BooleanType< T > > extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements Cursor< T >
{
	private final RandomAccess< T > randomAccess;

	private final int n;

	private long index;

	private final long maxIndex;

	private long lineIndex;

	private final long maxLineIndex;

	public RandomAccessibleRegionCursor( final RandomAccessibleInterval< T > interval, final long size )
	{
		super( interval );
		randomAccess = interval.randomAccess();
		n = numDimensions();
		maxLineIndex = dimension( 0 );
		maxIndex = size;
		reset();
	}

	protected RandomAccessibleRegionCursor( final RandomAccessibleRegionCursor< T > cursor )
	{
		super( cursor.sourceInterval );
		this.randomAccess = cursor.randomAccess.copyRandomAccess();
		n = cursor.n;
		lineIndex = cursor.lineIndex;
		maxIndex = cursor.maxIndex;
		maxLineIndex = cursor.maxLineIndex;
	}

	@Override
	public T get()
	{
		return randomAccess.get();
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long i = 0; i < steps; ++i )
			fwd();
	}

	@Override
	public void fwd()
	{
		do
		{
			randomAccess.fwd( 0 );
			if ( ++lineIndex > maxLineIndex )
				nextLine();
		}
		while ( !randomAccess.get().get() );
		++index;
	}

	private void nextLine()
	{
		lineIndex = 0;
		randomAccess.setPosition( min( 0 ), 0 );
		for ( int d = 1; d < n; ++d )
		{
			randomAccess.fwd( d );
			if ( randomAccess.getLongPosition( d ) > max( d ) )
				randomAccess.setPosition( min( d ), d );
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = 0;
		lineIndex = 0;
		min( randomAccess );
		randomAccess.bck( 0 );
	}

	@Override
	public boolean hasNext()
	{
		return index < maxIndex;
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{}

	@Override
	public RandomAccessibleRegionCursor< T > copy()
	{
		return new RandomAccessibleRegionCursor< T >( this );
	}

	@Override
	public RandomAccessibleRegionCursor< T > copyCursor()
	{
		return copy();
	}

	@Override
	public void localize( final float[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return randomAccess.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return randomAccess.getDoublePosition( d );
	}

	@Override
	public void localize( final int[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return randomAccess.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return randomAccess.getLongPosition( d );
	}
}
