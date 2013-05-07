package net.imglib2.ops.expression;

public final class WrapUnary< O, I1 > implements UnaryOp< O, I1 >
{
	protected final Op< O > op;

	protected final Port< I1 > input1Port;

	public WrapUnary( final Op< O > op, final Port< I1 > input1Port )
	{
		this.op = op;
		this.input1Port = input1Port;
	}

	@Override
	public Port< O > output()
	{
		return op.output();
	}

	@Override
	public O get()
	{
		return op.get();
	}

	@Override
	public WrapUnary< O, I1 > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public Port< I1 > input1()
	{
		return input1Port;
	}
}
