package net.imglib2.ops.example;

import net.imglib2.img.Img;
import net.imglib2.ops.Condition;
import net.imglib2.ops.Function;
import net.imglib2.ops.condition.AtKeyPointCondition;
import net.imglib2.ops.function.complex.ComplexImageFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.image.ImageAssignment;
import net.imglib2.ops.operation.unary.real.RealSqr;
import net.imglib2.ops.operation.unary.real.RealUnaryOperation;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

public class ExampleMisc {

	static void basicAssignmentSequence() {
		Img<UnsignedByteType> inputImg = null; // image 100x200
		Img<UnsignedByteType> outputImg = null; // image 100x200

		// sub region for assignment
		long[] origin = new long[] { 0, 0 };
		long[] span = new long[] { 50, 40 };

		RealUnaryOperation<DoubleType,DoubleType> op =
				new RealSqr<DoubleType, DoubleType>();

		Function<long[], DoubleType> imageFunc =
				new ComplexImageFunction<UnsignedByteType,DoubleType>(
						inputImg, new DoubleType());

		Function<long[], DoubleType> func =
			new GeneralUnaryFunction<long[],DoubleType,DoubleType>(
					imageFunc, op, new DoubleType());

		Condition<long[]> condition = new AtKeyPointCondition();

		ImageAssignment<UnsignedByteType,DoubleType> assigner =
				new ImageAssignment<UnsignedByteType,DoubleType>(
				outputImg, origin, span, func, condition);

		assigner.assign(); // processed in parallel

		assigner.abort(); // if desired
	}

	public static void main(String[] args) {

		RealType<?> r = new UnsignedByteType();
		ComplexType<?> c = new ComplexDoubleType();
		
		System.out.println(r.getClass()+" is a RealType : "+(r instanceof RealType<?>));
		System.out.println(r.getClass()+" is a ComplexType : "+(r instanceof ComplexType<?>));
		System.out.println(c.getClass()+" is a RealType : "+(c instanceof RealType<?>));
		System.out.println(c.getClass()+" is a ComplexType : "+(c instanceof ComplexType<?>));
	}
}
