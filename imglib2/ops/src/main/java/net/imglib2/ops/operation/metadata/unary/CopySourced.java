package net.imglib2.ops.operation.metadata.unary;

import net.imglib2.meta.Sourced;
import net.imglib2.ops.operation.UnaryOperation;

public class CopySourced< S extends Sourced > implements UnaryOperation< S, S >
{

	@Override
	public S compute( S input, S output )
	{
		output.setSource( input.getSource() );
		return output;
	}

	@Override
	public UnaryOperation< S, S > copy()
	{
		return new CopySourced< S >();
	}

}
