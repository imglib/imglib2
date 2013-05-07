package net.imglib2.ops.expression.ops;

import net.imglib2.Sampler;
import net.imglib2.ops.expression.AbstractBinaryOp;
import net.imglib2.type.numeric.NumericType;

public final class Add< T extends NumericType< T > > extends AbstractBinaryOp< T, T, T >
{
	public Add()
	{}

	public Add( final Sampler< T > output, final Sampler< T > input1, final Sampler< T > input2 )
	{
		super( output, input1, input2 );
	}

	@Override
	public T get()
	{
		final T t = output.get();
		t.set( input1.get() );
		t.add( input2.get() );
		return t;
	}

	protected Add( final Add< T > expression )
	{
		super( expression );
	}

	@Override
	public Add< T > copy()
	{
		return new Add< T >( this );
	}
}