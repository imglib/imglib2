package net.imglib2.ops.expression;

public interface UnaryOp< O, I1  > extends Op< O >
{
	public Port< I1 > input1();
}
