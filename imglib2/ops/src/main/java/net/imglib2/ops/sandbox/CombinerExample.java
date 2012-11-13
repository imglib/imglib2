package net.imglib2.ops.sandbox;

import net.imglib2.combiner.read.CombinedIterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.img.BinaryOperationBasedCombiner;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.unary.RealMultiplyConstant;
import net.imglib2.type.numeric.real.DoubleType;

public class CombinerExample
{
	public static void main( String[] args )
	{
		// Images are Created
		Img< DoubleType > imgA = new ArrayImgFactory< DoubleType >().create( new long[] { 100, 100, 5 }, new DoubleType() );

		Img< DoubleType > imgB = new ArrayImgFactory< DoubleType >().create( new long[] { 100, 100, 5 }, new DoubleType() );

		Img< DoubleType > imgC = new ArrayImgFactory< DoubleType >().create( new long[] { 100, 100, 5 }, new DoubleType() );

		// Combiner is created
		CombinedIterableInterval< DoubleType, DoubleType, DoubleType > in = new CombinedIterableInterval< DoubleType, DoubleType, DoubleType >( imgA, imgB, new BinaryOperationBasedCombiner< DoubleType, DoubleType, DoubleType >( new RealAdd< DoubleType, DoubleType, DoubleType >() ), new DoubleType() );

		new UnaryOperationAssignment< DoubleType, DoubleType >( new RealMultiplyConstant< DoubleType, DoubleType >( 0.5d ) ).compute( in, imgC );

	}
}
