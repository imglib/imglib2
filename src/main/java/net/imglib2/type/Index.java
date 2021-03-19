package net.imglib2.type;

public final class Index
{
	private int i = 0;

	public int get()
	{
		return i;
	}

	public void set( final int index)
	{
		i = index;
	}

	public void inc()
	{
		++i;
	}

	public void inc( final int increment )
	{
		i += increment;
	}

	public void dec()
	{
		--i;
	}

	public void dec( final int decrement )
	{
		i -= decrement;
	}
}
