package net.imglib2.ops.example;

import net.imglib2.img.Img;
import net.imglib2.ops.Condition;
import net.imglib2.ops.Function;
import net.imglib2.ops.condition.AtKeyPointCondition;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.image.ImageAssignment;
import net.imglib2.ops.operation.unary.real.RealSqr;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class ExampleMisc {

	static void basicAssignmentSequence() {
		Img<UnsignedByteType> inputImg = null; // image 100x200
		Img<UnsignedByteType> outputImg = null; // image 100x200

		// sub region for assignment
		long[] origin = new long[] { 0, 0 };
		long[] span = new long[] { 50, 40 };

		RealSqr<UnsignedByteType, UnsignedByteType> op = new RealSqr<UnsignedByteType, UnsignedByteType>();

		Function<long[], UnsignedByteType> imageFunc = new RealImageFunction<UnsignedByteType>(
				inputImg);

		Function<long[], UnsignedByteType> func = null;
		// new GeneralUnaryFunction<long[],UnsignedByteType>(imageFunc, op);

		Condition condition = new AtKeyPointCondition();

		ImageAssignment<UnsignedByteType> assigner = new ImageAssignment<UnsignedByteType>(
				outputImg, origin, span, func);

		assigner.setCondition(condition);

		assigner.assign(); // processed in parallel

		assigner.abort(); // if desired
	}

	public static void main(String[] args) {

	}
}
