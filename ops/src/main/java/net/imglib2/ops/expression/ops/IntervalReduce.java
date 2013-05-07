package net.imglib2.ops.expression.ops;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Sampler;
import net.imglib2.ops.expression.AbstractUnaryOp;
import net.imglib2.ops.expression.BinaryOp;
import net.imglib2.ops.expression.Temporary;
import net.imglib2.type.Type;

public final class IntervalReduce< T extends Type< T > > extends AbstractUnaryOp< T, IterableInterval< T > >
{
	final protected BinaryOp< T, T, T > op;

	public IntervalReduce( final BinaryOp< T, T, T > op )
	{
		this.op = op;
	}

	public IntervalReduce( final BinaryOp< T, T, T > op, final Sampler< T > output, final Sampler< IterableInterval< T > > input1 )
	{
		super( output, input1 );
		this.op = op;
	}

	@Override
	public T get()
	{
		final IterableInterval< T > iterable = input1.get();
		final T t = output.get();
		final Cursor< T > i1 = iterable.cursor();
		if ( i1.hasNext() )
		{
			t.set( i1.next() );
			final T i2 = t.copy();
			op.input1().set( i1 );
			op.input2().set( new Temporary< T >( i2 ) );
			op.output().set( output );
			while ( i1.hasNext() )
			{
				i1.fwd();
				i2.set( op.get() );
			}
		}
		return t;
	}

	@Override
	public IntervalReduce< T > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}
}
