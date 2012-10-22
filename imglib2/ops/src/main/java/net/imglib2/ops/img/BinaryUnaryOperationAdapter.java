package net.imglib2.ops.img;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;

public abstract class BinaryUnaryOperationAdapter< A, B, C, D > implements BinaryOperation< A, B, D >
{

	private final BinaryOperation< A, B, C > binaryOp;

	private final UnaryOperation< C, D > unaryOp1;

	public BinaryUnaryOperationAdapter( BinaryOperation< A, B, C > binaryOp, UnaryOperation< C, D > op1 )
	{
		this.binaryOp = binaryOp;
		this.unaryOp1 = op1;
	}

	public D compute( A input1, B input2, D output )
	{
		return unaryOp1.compute( binaryOp.compute( input1, input2, getBinaryOpBuffer() ), output );
	};

	@Override
	public BinaryOperation< A, B, D > copy()
	{
		return new BinaryUnaryOperationAdapter< A, B, C, D >( binaryOp.copy(), unaryOp1.copy() )
		{

			@Override
			protected C getBinaryOpBuffer()
			{
				return this.getBinaryOpBuffer();
			}
		};
	}

	protected abstract C getBinaryOpBuffer();

}
