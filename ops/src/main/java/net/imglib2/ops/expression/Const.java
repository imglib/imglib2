package net.imglib2.ops.expression;

import net.imglib2.Sampler;

public final class Const< T > implements Sampler< T >
{
	private final T t;

	public Const( final T t )
	{
		this.t = t;
	}

	@Override
	public T get()
	{
		return t;
	}

	@Override
	public Const< T > copy()
	{
		return this;
	}

	public static < T > Const< T > create( final T t )
	{
		return new Const< T >( t );
	}
}
