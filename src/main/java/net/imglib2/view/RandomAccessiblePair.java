package net.imglib2.view;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.util.Pair;

/**
 * A {@link RandomAccessible} over two independent
 * {@link RandomAccessible RandomAccessibles} whose type is the {@link Pair} of
 * corresponding values at the same coordinates in either of the two sources.
 *
 * @author Stephan Saalfeld (saalfelds@janelia.hhmi.org)
 * @author Tobias Pietzsch (tobias.pietzsch@gmail.com)
 */
public class RandomAccessiblePair< A, B > implements RandomAccessible< Pair< A, B > >
{
	final protected RandomAccessible< A > sourceA;
	final protected RandomAccessible< B > sourceB;

	public class RandomAccess implements Pair< A, B >, net.imglib2.RandomAccess< Pair< A, B > >
	{
		final protected net.imglib2.RandomAccess< A > a;
		final protected net.imglib2.RandomAccess< B > b;

		public RandomAccess()
		{
			a = sourceA.randomAccess();
			b = sourceB.randomAccess();
		}

		@Override
		public A getA()
		{
			return a.get();
		}

		@Override
		public B getB()
		{
			return b.get();
		}

		@Override
		public void localize( final int[] position )
		{
			a.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			a.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return a.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return a.getLongPosition( d );
		}

		@Override
		public void localize( final float[] position )
		{
			a.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			a.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return a.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return a.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return RandomAccessiblePair.this.numDimensions();
		}

		@Override
		public void fwd( final int d )
		{
			a.fwd( d );
			b.fwd( d );
		}

		@Override
		public void bck( final int d )
		{
			a.bck( d );
			b.bck( d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			a.move( distance, d );
			b.move( distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			a.move( distance, d );
			b.move( distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			a.move( localizable );
			b.move( localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			a.move( distance );
			b.move( distance );
		}

		@Override
		public void move( final long[] distance )
		{
			a.move( distance );
			b.move( distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			a.setPosition( localizable );
			b.setPosition( localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			a.setPosition( position );
			b.setPosition( position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			a.setPosition( position );
			b.setPosition( position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			a.setPosition( position, d );
			b.setPosition( position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			a.setPosition( position, d );
			b.setPosition( position, d );
		}

		@Override
		public RandomAccess get()
		{
			return this;
		}

		@Override
		public RandomAccess copy()
		{
			final RandomAccess copy = new RandomAccess();
			copy.setPosition( this );
			return copy;
		}

		@Override
		public RandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	public RandomAccessiblePair(
			final RandomAccessible< A > sourceA,
			final RandomAccessible< B > sourceB )
	{
		this.sourceA = sourceA;
		this.sourceB = sourceB;
	}

	@Override
	public int numDimensions()
	{
		return sourceA.numDimensions();
	}

	@Override
	public RandomAccess randomAccess()
	{
		return new RandomAccess();
	}

	@Override
	public RandomAccess randomAccess( final Interval interval )
	{
		return new RandomAccess();
	}
}
