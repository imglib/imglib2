package net.imglib2.newroi.util;

import net.imglib2.Localizable;
import net.imglib2.Pair;
import net.imglib2.RandomAccess;

public final class PairRandomAccess< A, B > implements RandomAccess< Pair< A, B > >, Pair< A, B >
{
	private final RandomAccess< A > a;

	private final RandomAccess< B > b;

	public PairRandomAccess( final RandomAccess<A> accessA, final RandomAccess<B> accessB )
	{
		a = accessA;
		b = accessB;
	}

	private PairRandomAccess( final PairRandomAccess< A, B > access )
	{
		a = access.a.copyRandomAccess();
		b = access.b.copyRandomAccess();
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
		return a.numDimensions();
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
	public Pair< A, B > get()
	{
		return this;
	}

	@Override
	public PairRandomAccess< A, B > copy()
	{
		return new PairRandomAccess< A, B >( this );
	}

	@Override
	public PairRandomAccess< A, B > copyRandomAccess()
	{
		return copy();
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
}