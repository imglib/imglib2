package net.imglib2.algorithm.region.localneighborhood;

import ij.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

public class RectangleNeighborhoodExample {


	private static final int DIM = 100; // also N points

	public static void main(String[] args) {
		ImageJ.main(args);
		example1();
		example2();
	}

	public static void example2() {


		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> image = imgFactory.create(new int[] { DIM, DIM, DIM }, new UnsignedByteType());

		long[] center = new long[3];
		long[] span = new long[3];

		RandomAccess<UnsignedByteType> ra = image.randomAccess();

		for (int i = 0; i < DIM; i++) {

			center[0] = (long) (Math.random() * DIM);
			center[1] = (long) (Math.random() * DIM);
			center[2] = (long) (Math.random() * DIM);

			ra.setPosition(center);

			span[0] = (long) (Math.random() / 10 * DIM);
			span[1] = (long) (Math.random() / 10 * DIM);
			span[2] = (long) (Math.random() / 10 * DIM);

			RectangleNeighborhood<UnsignedByteType> rectangle = new RectangleNeighborhood<UnsignedByteType>(image);
			rectangle.setPosition(center);
			rectangle.setSpan(span);
			RectangleCursor<UnsignedByteType> cursor = rectangle.cursor();

			System.out.println("Center: " + Util.printCoordinates(center));// DEBUG
			System.out.println("Span: " + Util.printCoordinates(span));// DEBUG

			while (cursor.hasNext()) {

				cursor.fwd();
				if (cursor.isOutOfBounds())
					continue;
				cursor.get().add(new UnsignedByteType(50));

			}

		}

		ImageJFunctions.show(image);

	}


	public static void example1() {
		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> image = imgFactory.create(new int[] { DIM, DIM, DIM }, new UnsignedByteType());

		long[] center = new long[] { 50, 50 , 50 }; // the middle
		long[] span = new long[] { 30, 30, 0 }; // a single plane in the middle 

		// Write into the image
		RectangleNeighborhood<UnsignedByteType> rectangle = new RectangleNeighborhood<UnsignedByteType>(image);
		rectangle.setPosition(center);
		rectangle.setSpan(span);

		UnsignedByteType val = new UnsignedByteType(200);
		for(UnsignedByteType pixel : rectangle) {
			pixel.set(val);
		}

		ImageJFunctions.show(image);
	}

}
