package net.imglib2.ops.example;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.operation.binary.realtype.RealTypeAdd;
import net.imglib2.type.numeric.real.DoubleType;

public class Example12 {

	private static final int XSIZE = 10;
	private static final int YSIZE = 20;
	
	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private static void fillImage(Img<DoubleType> img, double value) {
		Cursor<DoubleType> cursor = img.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.get().set(value);
		}
	}
	
	public static void main(String[] args) {
		Img<DoubleType> in1 = allocateImage();
		fillImage(in1, 1);
		Img<DoubleType> in2 = allocateImage();
		fillImage(in2, 2);
		Img<DoubleType> out = allocateImage();
		fillImage(out, 0);
		RealTypeAdd<DoubleType> op = new RealTypeAdd<DoubleType>();
		Cursor<DoubleType> ic1 = in1.cursor();
		Cursor<DoubleType> ic2 = in2.cursor();
		Cursor<DoubleType> oc = out.cursor();
		while ((ic1.hasNext()) &&
				(ic2.hasNext()) &&
				(oc.hasNext())) {
			ic1.fwd();
			ic2.fwd();
			oc.fwd();
			op.compute(ic1.get(), ic2.get(), oc.get());
		}
		
		System.out.println("checking pixel values");
		int i = 0;
		oc.reset();
		while (oc.hasNext()) {
			System.out.println(i++);
			oc.fwd();
			double value = oc.get().getRealDouble();
			if (!veryClose(value,1+2)) {
				System.out.println("IMAGES ARE DIFFERENT");
			}
		}
	}
}
