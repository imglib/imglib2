package net.imglib2.view.fluent;

import net.imglib2.RandomAccessibleInterval;

class RaiWrapper< T > implements RaiView< T >
{
	private final RandomAccessibleInterval< T > delegate;

	RaiWrapper( final RandomAccessibleInterval< T > delegate )
	{
		this.delegate = delegate;
	}

	@Override
	public RandomAccessibleInterval< T > delegate()
	{
		return delegate;
	}
}
