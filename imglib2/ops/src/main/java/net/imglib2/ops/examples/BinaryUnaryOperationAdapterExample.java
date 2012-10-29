package net.imglib2.ops.examples;

import net.imglib2.img.Img;
import net.imglib2.ops.img.BinaryOperationAssignment;
import net.imglib2.ops.img.BinaryUnaryOperationAdapter;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.unary.RealMultiplyConstant;
import net.imglib2.type.numeric.real.DoubleType;

public class BinaryUnaryOperationAdapterExample
{

	/**
	 * Example shows how to add to images followed by a multiplication with a
	 * constant in one run
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{

		double constant = 0.5;

		Img< DoubleType > inA = null;
		Img< DoubleType > inB = null;
		Img< DoubleType > out = null;

		// Operation to add two images
		RealAdd< DoubleType, DoubleType, DoubleType > addOp = new RealAdd< DoubleType, DoubleType, DoubleType >();

		// Operation to multiply an image with constant
		RealMultiplyConstant< DoubleType, DoubleType > multiplyOp = new RealMultiplyConstant< DoubleType, DoubleType >( constant );

		// Put them together
		BinaryUnaryOperationAdapter< DoubleType, DoubleType, DoubleType, DoubleType > adapterOp = new BinaryUnaryOperationAdapter< DoubleType, DoubleType, DoubleType, DoubleType >( addOp, multiplyOp )
		{
			@Override
			protected DoubleType getBinaryOpBuffer()
			{
				return new DoubleType();
			}
		};

		// Iterate over image
		BinaryOperationAssignment< DoubleType, DoubleType, DoubleType > assignment = new BinaryOperationAssignment< DoubleType, DoubleType, DoubleType >( adapterOp );

		// Create Resulting Image
		assignment.compute( inA, inB, out );

		// All in one version
		new BinaryOperationAssignment< DoubleType, DoubleType, DoubleType >( new BinaryUnaryOperationAdapter< DoubleType, DoubleType, DoubleType, DoubleType >( new RealAdd< DoubleType, DoubleType, DoubleType >(), new RealMultiplyConstant< DoubleType, DoubleType >( constant ) )
		{
			@Override
			protected DoubleType getBinaryOpBuffer()
			{
				return new DoubleType();
			}
		} ).compute( inA, inB, out );

	}
}
