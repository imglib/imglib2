package net.imglib2.ops.expression.ops;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Sampler;
import net.imglib2.ops.expression.AbstractBinaryOp;
import net.imglib2.ops.expression.BinaryOp;

public final class BinaryIterate< O, I1, I2 > extends AbstractBinaryOp< IterableInterval< O >, IterableInterval< I1 >, IterableInterval< I2 > >
{
	final protected BinaryOp< O, I1, I2 > op;

	public BinaryIterate( final BinaryOp< O, I1, I2 > op )
	{
		this.op = op;
	}

	public BinaryIterate( final BinaryOp< O, I1, I2 > op, final Sampler< IterableInterval< O > > output, final Sampler< IterableInterval< I1 > > input1, final Sampler< IterableInterval< I2 > > input2 )
	{
		super( output, input1, input2 );
		this.op = op;
	}

	@Override
	public IterableInterval< O > get()
	{
		final IterableInterval< O > o = output.get();
		final Cursor< O > co = o.cursor();
		final Cursor< I1 > ci1 = input1.get().cursor();
		final Cursor< I2 > ci2 = input2.get().cursor();
		op.output().set( co );
		op.input1().set( ci1 );
		op.input2().set( ci2 );
		while ( co.hasNext() )
		{
			co.fwd();
			ci1.fwd();
			ci2.fwd();
			op.get();
		}
		return o;
	}

	@Override
	public BinaryIterate< O, I1, I2 > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}
}
