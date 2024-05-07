package net.imglib2.streamifiedview;

import net.imglib2.RandomAccessible;

class RaWrapper< T > implements RaView< T, RaWrapper< T > >
{
	private final RandomAccessible< T > delegate;

	RaWrapper( final RandomAccessible< T > delegate )
	{
		this.delegate = delegate;
	}

	@Override
	public RandomAccessible< T > delegate()
	{
		return delegate;
	}
}
