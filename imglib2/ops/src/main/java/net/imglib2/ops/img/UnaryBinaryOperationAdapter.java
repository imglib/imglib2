package net.imglib2.ops.img;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;

public abstract class UnaryBinaryOperationAdapter< A, B, C, D > implements UnaryOperation< A, D >
{

	private final BinaryOperation< B, C, D > binaryOp;

	private final UnaryOperation< A, B > unaryOp1;

	private final UnaryOperation< A, C > unaryOp2;

	public UnaryBinaryOperationAdapter( UnaryOperation< A, B > op1, UnaryOperation< A, C > op2, BinaryOperation< B, C, D > binaryOp )
	{
		this.binaryOp = binaryOp;
		this.unaryOp1 = op1;
		this.unaryOp2 = op2;
	}

	public D compute( A input, D output )
	{
		return binaryOp.compute( unaryOp1.compute( input, getOp1Buffer() ), unaryOp2.compute( input, getOp2Buffer() ), output );
	};

	@Override
	public UnaryOperation< A, D > copy()
	{
		return new UnaryBinaryOperationAdapter< A, B, C, D >( unaryOp1.copy(), unaryOp2.copy(), binaryOp.copy() )
		{

			@Override
			protected B getOp1Buffer()
			{
				return this.getOp1Buffer();
			}

			@Override
			protected C getOp2Buffer()
			{
				return this.getOp2Buffer();
			}

		};
	}

	protected abstract B getOp1Buffer();

	protected abstract C getOp2Buffer();

}
