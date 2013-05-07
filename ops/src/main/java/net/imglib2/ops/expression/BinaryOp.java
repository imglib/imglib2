package net.imglib2.ops.expression;

public interface BinaryOp< O, I1, I2  > extends Op< O >
{
	public Port< I1 > input1();

	public Port< I2 > input2();
}
