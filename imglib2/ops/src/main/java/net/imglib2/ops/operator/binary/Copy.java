package net.imglib2.ops.operator.binary;

import net.imglib2.ops.operator.BinaryOperator;

public final class Copy implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		return input2;  // TODO - in IJ1 the second dataset is the source - make sure we are consistent with this in IJ2
	}
	
}

