package net.imglib2.ops.expression;

import net.imglib2.Sampler;
import net.imglib2.type.Type;

public final class Temporary< T extends Type< T > > implements Sampler< T >
{
	private final T t;

	public Temporary( final T t )
	{
		this.t = t;
	}

	@Override
	public T get()
	{
		return t;
	}

	@Override
	public Temporary< T > copy()
	{
		return new Temporary< T >( t.copy() );
	}

	public static < T extends Type< T > > Temporary< T > create( final T t )
	{
		return new Temporary< T >( t );
	}
}
