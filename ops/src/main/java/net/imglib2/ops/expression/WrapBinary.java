package net.imglib2.ops.expression;

public final class WrapBinary< O, I1, I2 > implements BinaryOp< O, I1, I2 >
{
	protected final Op< O > op;

	protected final Port< I1 > input1Port;

	protected final Port< I2 > input2Port;

	public WrapBinary( final Op< O > op, final Port< I1 > input1Port, final Port< I2 > input2Port )
	{
		this.op = op;
		this.input1Port = input1Port;
		this.input2Port = input2Port;
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
	public WrapBinary< O, I1, I2 > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public Port< I1 > input1()
	{
		return input1Port;
	}

	@Override
	public Port< I2 > input2()
	{
		return input2Port;
	}
}
