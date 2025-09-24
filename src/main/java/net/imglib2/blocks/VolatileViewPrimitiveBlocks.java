package net.imglib2.blocks;

import net.imglib2.Interval;
import net.imglib2.Volatile;
import net.imglib2.type.NativeType;

// TODO: This is just a dummy to make it compile. Should be based on a copy of ViewPrimitiveBlocks instead
class VolatileViewPrimitiveBlocks< T extends Volatile< ? > & NativeType< T >> implements VolatilePrimitiveBlocks< T >
{
	public VolatileViewPrimitiveBlocks( final ViewProperties< T, ? > viewProperties )
	{
	}

	@Override
	public void copy( final Interval interval, final Object dest, final byte[] destValid )
	{

	}

	@Override
	public VolatilePrimitiveBlocks< T > threadSafe()
	{
		return null;
	}

	@Override
	public VolatilePrimitiveBlocks< T > independentCopy()
	{
		return null;
	}

	@Override
	public int numDimensions()
	{
		return 0;
	}

	@Override
	public T getType()
	{
		return null;
	}
}
