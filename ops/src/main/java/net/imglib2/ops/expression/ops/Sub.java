package net.imglib2.ops.expression.ops;

import net.imglib2.Sampler;
import net.imglib2.ops.expression.AbstractBinaryOp;
import net.imglib2.type.numeric.NumericType;

public final class Sub< T extends NumericType< T > > extends AbstractBinaryOp< T, T, T >
{
	public Sub()
	{}

	public Sub( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		super( output, input1, input2 );
	}

	@Override
	public T get()
	{
		final T t = output.get();
		t.set( input1.get() );
		t.sub( input2.get() );
		return t;
	}

	protected Sub( final Sub< T > expression )
	{
		super( expression );
	}

	@Override
	public Sub< T > copy()
	{
		return new Sub< T >( this );
	}
}