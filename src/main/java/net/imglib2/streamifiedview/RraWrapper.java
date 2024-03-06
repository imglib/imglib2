package net.imglib2.streamifiedview;

import net.imglib2.RealRandomAccessible;

class RraWrapper< T > implements RraView< T >
{
	private final RealRandomAccessible< T > delegate;

	RraWrapper( final RealRandomAccessible< T > delegate )
	{
		this.delegate = delegate;
	}

	@Override
	public RealRandomAccessible< T > delegate()
	{
		return delegate;
	}
}
