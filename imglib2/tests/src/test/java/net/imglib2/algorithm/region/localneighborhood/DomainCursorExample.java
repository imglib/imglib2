package net.imglib2.algorithm.region.localneighborhood;

import ij.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

public class DomainCursorExample {

	
	private static final int DIM = 100; // also N points
	
	public static void main(String[] args) {
		
		 final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<UnsignedByteType>();
		 Img<UnsignedByteType> image = imgFactory.create(new int[] { DIM, DIM, DIM }, new UnsignedByteType());

		 long[] center = new long[3];
		 long[] span = new long[3];
		 long[] position = new long[3];
		 
		 RandomAccess<UnsignedByteType> ra = image.randomAccess();
		 
		 for (int i = 0; i < DIM; i++) {
			 
			 center[0] = (long) (Math.random() * DIM);
			 center[1] = (long) (Math.random() * DIM);
			 center[2] = (long) (Math.random() * DIM);
			 
			 ra.setPosition(center);

			 span[0] = (long) (Math.random() / 10 * DIM);
			 span[1] = (long) (Math.random() / 10 * DIM);
			 span[2] = (long) (Math.random() / 10 * DIM);
			 
			 DomainCursor<UnsignedByteType> cursor = new DomainCursor<UnsignedByteType>(ra, span);
			 
			 System.out.println("Center: " + Util.printCoordinates(center));// DEBUG
			 System.out.println("Span: " + Util.printCoordinates(span));// DEBUG
			 
			 while (cursor.hasNext()) {
				 
				 cursor.fwd();
				 cursor.localize(position);

				 boolean oob = false;
				 for (int j = 0; j < image.numDimensions(); j++) {
					if (position[j] < image.min(j) || position[j] > image.max(j)) {
						oob = true;
					}
				 }
				 if (oob) {
					 continue;
				 }
				 
				 cursor.get().add(new UnsignedByteType(50));
				 
			 }
			 
		}
		
		 ImageJ.main(args);
		 ImageJFunctions.show(image);
		
	}
	
}
