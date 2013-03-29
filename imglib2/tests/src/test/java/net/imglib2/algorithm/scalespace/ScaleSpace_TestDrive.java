package net.imglib2.algorithm.scalespace;

import java.util.ArrayList;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.converter.Converter;
import net.imglib2.display.RealFloatConverter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

public class ScaleSpace_TestDrive {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Prepare image
		int width = 64;
		int height = 64;
		int R = 5;
		ImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> img = factory.create(new int[] {width,  height}, new UnsignedByteType());
		long[] pos = new long[2];
		Cursor<UnsignedByteType> cursor = img.localizingCursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.localize(pos);
			long dx = pos[0]-width/2;
			long dy = pos[1]-height/2;
			if ( dx*dx + dy*dy <= R ) {
				cursor.get().set(100);
			}
		}
		
		double initialSigma = 1;
		Converter<UnsignedByteType, FloatType> converter = new RealFloatConverter<UnsignedByteType>();
		ScaleSpace<UnsignedByteType> scaleSpace = new ScaleSpace<UnsignedByteType>(img, converter , initialSigma );
		
		System.out.println("Starting scale space computation.");
		if (!scaleSpace.checkInput() || !scaleSpace.process()) {
			System.err.println("ScaleSpace failed: " + scaleSpace.getErrorMessage());
			return;
		}
		System.out.println("Done in " + scaleSpace.getProcessingTime() + " ms.");
		
		Img<FloatType> results = scaleSpace.getResult();
		
		ImageJ.main(args);
		ImageJFunctions.wrapFloat(img, "Source").show();
		ImageJFunctions.wrapFloat(results, "Scale space").show();
		
		ArrayList<DifferenceOfGaussianPeak<FloatType>> peaks = scaleSpace.getPeaks();
		for (int i = 0; i < peaks.size(); i++) {
			System.out.println("Peak " + i + ": " + Util.printCoordinates(peaks.get(i).getSubPixelPosition()));
		}
	}

}
