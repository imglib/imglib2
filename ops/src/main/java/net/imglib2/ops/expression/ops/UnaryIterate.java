package net.imglib2.ops.expression.ops;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Sampler;
import net.imglib2.ops.expression.AbstractUnaryOp;
import net.imglib2.ops.expression.UnaryOp;

public final class UnaryIterate< O, I1 > extends AbstractUnaryOp< IterableInterval< O >, IterableInterval< I1 > >
{
	final protected UnaryOp< O, I1 > op;

	public UnaryIterate( final UnaryOp< O, I1 > op )
	{
		this.op = op;
	}

	public UnaryIterate( final UnaryOp< O, I1 > op, final Sampler< IterableInterval< O > > output, final Sampler< IterableInterval< I1 > > input1 )
	{
		super( output, input1 );
		this.op = op;
	}

	@Override
	public IterableInterval< O > get()
	{
		final IterableInterval< O > o = output.get();
		final Cursor< O > co = o.cursor();
		final Cursor< I1 > ci1 = input1.get().cursor();
		op.output().set( co );
		op.input1().set( ci1 );
		while ( co.hasNext() )
		{
			co.fwd();
			ci1.fwd();
			op.get();
		}
		return o;
	}

	@Override
	public UnaryIterate< O, I1 > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}
}
