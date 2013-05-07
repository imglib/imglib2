package net.imglib2.ops.expression.ops;

import net.imglib2.Sampler;
import net.imglib2.ops.expression.AbstractBinaryOp;
import net.imglib2.type.Type;

public final class Min< T extends Comparable< T > & Type< T > > extends AbstractBinaryOp< T, T, T >
{
	public Min()
	{}

	public Min( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		super( output, input1, input2 );
	}

	@Override
	public T get()
	{
		final T t = output.get();
		final T i1 = input1.get();
		final T i2 = input2.get();
		t.set( i1.compareTo( i2 ) < 0 ? i1 : i2 );
		return t;
	}

	protected Min( final Min< T > expression )
	{
		super( expression );
	}

	@Override
	public Min< T > copy()
	{
		return new Min< T >( this );
	}
}