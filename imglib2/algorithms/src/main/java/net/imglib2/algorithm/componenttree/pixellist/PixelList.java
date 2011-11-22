package net.imglib2.algorithm.componenttree.pixellist;

import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.util.IntervalIndexer;

public final class PixelList implements Iterable< Localizable >
{
	private final RandomAccess< LongType > locationsAccess;

	private final long[] dimensions;

	private long headIndex;

	private final long[] tailPos;

	private long size;

	/**
	 *
	 * @param locationsAccess
	 *            RandomAccess into the index image to store the linked list.
	 * @param dimensions
	 *            Dimensions of the index image.
	 */
	public PixelList( final RandomAccess< LongType > locationsAccess, final long[] dimensions )
	{
		this.locationsAccess = locationsAccess;
		this.dimensions = dimensions;
		headIndex = 0;
		tailPos = new long[ dimensions.length ];
		size = 0;
	}

	public PixelList( final PixelList l )
	{
		this.locationsAccess = l.locationsAccess;
		this.dimensions = l.dimensions;
		this.headIndex = l.headIndex;
		this.tailPos = null;
		this.size = l.size;
	}

	public void addPosition( final Localizable position )
	{
		if ( size == 0 )
		{
			position.localize( tailPos );
			final long i = IntervalIndexer.positionToIndex( tailPos, dimensions );
			headIndex = i;
		}
		else
		{
			locationsAccess.setPosition( tailPos );
			position.localize( tailPos );
			final long i = IntervalIndexer.positionToIndex( tailPos, dimensions );
			locationsAccess.get().set( i );
		}
		++size;
	}

	public void merge( final PixelList l )
	{
		if ( size == 0 )
		{
			headIndex = l.headIndex;
			for ( int i = 0; i < tailPos.length; ++i )
				tailPos[i] = l.tailPos[i];
		}
		else
		{
			locationsAccess.setPosition( tailPos );
			locationsAccess.get().set( l.headIndex );
			for ( int i = 0; i < tailPos.length; ++i )
				tailPos[i] = l.tailPos[i];
		}
		size += l.size;
	}

	public class PixelListIterator implements Iterator< Localizable >
	{
		private long i;
		private long nextIndex;
		private final long[] tmp;
		private final Point pos;

		public PixelListIterator()
		{
			i = 0;
			nextIndex = headIndex;
			tmp = new long[ dimensions.length ];
			pos = new Point( dimensions.length );
		}

		@Override
		public boolean hasNext()
		{
			return i < size;
		}

		@Override
		public Localizable next()
		{
			++i;
			IntervalIndexer.indexToPosition( nextIndex, dimensions, tmp );
			pos.setPosition( tmp );
			locationsAccess.setPosition( tmp );
			nextIndex = locationsAccess.get().get();
			return pos;
		}

		@Override
		public void remove() {}
	}

	@Override
	public Iterator< Localizable > iterator()
	{
		return new PixelListIterator();
	}

	public long size()
	{
		return size;
	}
}
