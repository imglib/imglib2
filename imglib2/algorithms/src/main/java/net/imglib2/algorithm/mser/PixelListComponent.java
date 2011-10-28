package net.imglib2.algorithm.mser;

import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.util.IntervalIndexer;

public class PixelListComponent< T extends Type< T > > implements Component< T >, Iterable< Localizable >
{
	private static int idGen = 0;
	
	final int id;
	
	private long headIndex;
	
	private final long[] tailPos;
	
	private final RandomAccess< LongType > locationsAccess;

	private final long[] dimensions;
	
	T value;

	long size;
		
	public PixelListComponent( final T value, final PixelListComponentGenerator< T > generator )
	{
		id = idGen++;
		locationsAccess = generator.linkedList.randomAccess();
		headIndex = 0;
		size = 0;
		dimensions = generator.dimensions;
		tailPos = new long[ dimensions.length ];
		this.value = value.copy();
	}
	
	@Override
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
	
	@Override
	public T getValue()
	{
		return value;
	}
	
	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}
	
	@Override
	public void merge( final Component< T > component )
	{
		final PixelListComponent< T > c = ( PixelListComponent< T > ) component;
		if ( size == 0 )
		{
			headIndex = c.headIndex;
			for ( int i = 0; i < tailPos.length; ++i )
				tailPos[i] = c.tailPos[i];
		}
		else
		{
			locationsAccess.setPosition( tailPos );
			locationsAccess.get().set( c.headIndex );
			for ( int i = 0; i < tailPos.length; ++i )
				tailPos[i] = c.tailPos[i];
		}
		size += c.size;
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : id=" + id + " : ";
		boolean first = true;
		for ( Localizable l : this )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				s += ", ";
			}
			s += l.toString();
		}
		return s + "}";
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
}
