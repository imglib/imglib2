package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.meta.Named;
import net.imglib2.ops.UnaryOperation;

public class CopyNamed< K extends Named > implements UnaryOperation< K, K >
{

	@Override
	public K compute( K input, K output )
	{
		output.setName( input.getName() );
		return output;
	}

	@Override
	public UnaryOperation< K, K > copy()
	{
		return new CopyNamed<K>();
	}

}
