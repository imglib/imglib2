package net.imglib2.ops.img;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;

public class UnaryOperationWrapper< A, B > implements UnaryOutputOperation< A, B >
{

	private UnaryOperation< A, B > op;

	private UnaryObjectFactory< A, B > fac;

	public UnaryOperationWrapper( final UnaryOperation< A, B > op, final UnaryObjectFactory< A, B > fac )
	{
		this.op = op;
		this.fac = fac;
	}

	@Override
	public B compute( A input, B output )
	{
		return op.compute( input, output );
	}

	@Override
	public UnaryObjectFactory< A, B > bufferFactory()
	{
		return fac;
	}

	@Override
	public UnaryOutputOperation< A, B > copy()
	{
		return new UnaryOperationWrapper< A, B >( op.copy(), fac );
	}

}
