package net.imglib2.ops.operator.binary;

import net.imglib2.ops.operator.BinaryOperator;

public final class CopyTransparentZero implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		if (input2 == 0)    // TODO - treating input2 as source is compatible with IJ1 - make sure we are consistent with this in IJ2
			return input1;
		else
			return input2;
	}
	
}

