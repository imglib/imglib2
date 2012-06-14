package net.imglib2.algorithm.gradient;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

public class TripleIterableInterval< A, B, C > extends AbstractInterval implements IterableInterval< Triple< A, B, C > >
{
	RandomAccessibleInterval< A > a;

	RandomAccessible< B > b;

	RandomAccessible< C > c;

	private final long size;

	public TripleIterableInterval( final RandomAccessibleInterval< A > a, final RandomAccessible< B > b, final RandomAccessible< C > c )
	{
		super( a );
		this.a = a;
		this.b = b;
		this.c = c;
		long tmpsize = a.dimension( 0 );
		for ( int d = 1; d < n; ++d )
			tmpsize *= a.dimension( d );
		size = tmpsize;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public Triple< A, B, C > firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return new FlatIterationOrder( this );
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public Iterator< Triple< A, B, C > > iterator()
	{
		return cursor();
	}

	public static final class TripleCursor< A, B, C > extends AbstractInterval implements Cursor< Triple< A, B, C > >
	{
		final private RandomAccess< A > ra;
		final private RandomAccess< B > rb;
		final private RandomAccess< C > rc;

		final private Triple< A, B, C > triple;

		private final long[] tmp;

		private final long[] dimensions;

		private final long maxIndex;

		private long index;

		private long maxIndexOnLine;

		public TripleCursor( final RandomAccessibleInterval< A > a, final RandomAccessible< B > b, final RandomAccessible< C > c )
		{
			super( a );
			ra = a.randomAccess();
			rb = b.randomAccess( a );
			rc = c.randomAccess( a );
			triple = new Triple< A, B, C >();
			tmp = new long[ n ];
			dimensions = new long[ n ];
			a.dimensions( dimensions );
			long size = dimensions[ 0 ];
			for ( int d = 1; d < n; ++d )
				size *= dimensions[ d ];
			maxIndex = size - 1;
			reset();
		}

		public TripleCursor( final TripleCursor< A, B, C > cursor )
		{
			super( cursor );
			ra = cursor.ra.copyRandomAccess();
			rb = cursor.rb.copyRandomAccess();
			rc = cursor.rc.copyRandomAccess();
			triple = new Triple< A, B, C >();
			tmp = new long[ n ];
			dimensions = cursor.dimensions.clone();
			maxIndex = cursor.maxIndex;
			index = cursor.index;
			maxIndexOnLine = cursor.maxIndexOnLine;
		}

		@Override
		public Triple< A, B, C > get()
		{
			triple.a = ra.get();
			triple.b = rb.get();
			triple.c = rc.get();
			return triple;
		}

		public A getA()
		{
			return ra.get();
		}

		public B getB()
		{
			return rb.get();
		}

		public C getC()
		{
			return rc.get();
		}

		@Override
		public void jumpFwd( final long steps )
		{
			index += steps;
			maxIndexOnLine += steps / dimensions[ 0 ];
			IntervalIndexer.indexToPosition( steps, dimensions, tmp );
			ra.move( tmp );
			rb.move( tmp );
			rc.move( tmp );
		}

		@Override
		public void fwd()
		{
			ra.fwd( 0 );
			rb.fwd( 0 );
			rc.fwd( 0 );
			if ( ++index > maxIndexOnLine )
				nextLine();
		}

		private void nextLine()
		{
			ra.setPosition( min[ 0 ], 0 );
			rb.setPosition( min[ 0 ], 0 );
			rc.setPosition( min[ 0 ], 0 );
			maxIndexOnLine += dimensions[ 0 ];
			for ( int d = 1; d < n; ++d )
			{
				ra.fwd( d );
				if ( ra.getLongPosition( d ) > max[ d ] )
				{
					ra.setPosition( min[ d ], d );
					rb.setPosition( min[ d ], d );
					rc.setPosition( min[ d ], d );
				}
				else
				{
					rb.fwd( d );
					rc.fwd( d );
					break;
				}
			}
		}

		@Override
		public void reset()
		{
			index = -1;
			maxIndexOnLine = dimensions[ 0 ] - 1;
			ra.setPosition( min );
			rb.setPosition( min );
			rc.setPosition( min );
			ra.bck( 0 );
			rb.bck( 0 );
			rc.bck( 0 );
		}

		@Override
		public boolean hasNext()
		{
			return index < maxIndex;
		}

		@Override
		public Triple< A, B, C > next()
		{
			fwd();
			return get();
		}

		@Override
		public void remove()
		{
			// NB: no action.
		}

		@Override
		public void localize( final float[] position )
		{
			ra.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			ra.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return ra.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return ra.getDoublePosition( d );
		}

		@Override
		public void localize( final int[] position )
		{
			ra.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			ra.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ra.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return ra.getLongPosition( d );
		}

		@Override
		public TripleCursor< A, B, C > copy()
		{
			return new TripleCursor< A, B, C >( this );
		}

		@Override
		public TripleCursor< A, B, C > copyCursor()
		{
			return copy();
		}
	};

	@Override
	public TripleCursor< A, B, C > cursor()
	{
		return new TripleCursor< A, B, C >( a, b, c );
	}


	@Override
	public TripleCursor localizingCursor()
	{
		return cursor();
	}
}
