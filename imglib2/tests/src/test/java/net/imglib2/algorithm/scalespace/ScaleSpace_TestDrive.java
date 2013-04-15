package net.imglib2.algorithm.scalespace;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;

import java.util.Collection;
import java.util.Random;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.HyperSphereNeighborhood;
import net.imglib2.converter.Converter;
import net.imglib2.display.RealFloatConverter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

public class ScaleSpace_TestDrive {

	public static void main(String[] args) {
		
		/*
		 *  Prepare image
		 */
		
		int width = 512;
		int height = 512;
		int R = 20;
		int N_SPOTS = 30;
		ImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>();
		Img<UnsignedByteType> img = factory.create(new int[] {width,  height}, new UnsignedByteType());
		
		Random ran = new Random();
		OutOfBoundsFactory<UnsignedByteType, RandomAccessibleInterval<UnsignedByteType>> oob = 
				new OutOfBoundsBorderFactory<UnsignedByteType, RandomAccessibleInterval<UnsignedByteType>>();
		
		for (int i = 0; i < N_SPOTS; i++) {
			long radius = Math.round( R + R/4 * ran.nextGaussian());
			HyperSphereNeighborhood<UnsignedByteType, RandomAccessibleInterval<UnsignedByteType>> region = 
					new HyperSphereNeighborhood<UnsignedByteType, RandomAccessibleInterval<UnsignedByteType>>(img, oob, radius);
			region.setPosition(ran.nextInt(width), 0);
			region.setPosition(ran.nextInt(height), 1);
			int val = ran.nextInt(256);
			for (UnsignedByteType pixel : region) {
				pixel.set(val);
			}
		}
		
		/*
		 * Execute scale space computation.
		 */
		
		double initialSigma = 2;
		Converter<UnsignedByteType, FloatType> converter = new RealFloatConverter<UnsignedByteType>();
		ScaleSpace<UnsignedByteType> scaleSpace = new ScaleSpace<UnsignedByteType>(img, converter , initialSigma, 0.03d );
		
		System.out.println("Starting scale space computation.");
		if (!scaleSpace.checkInput() || !scaleSpace.process()) {
			System.err.println("ScaleSpace failed: " + scaleSpace.getErrorMessage());
			return;
		}
		System.out.println("Done in " + scaleSpace.getProcessingTime() + " ms.");
		
		/*
		 * Show images
		 */
		
		Img<FloatType> results = scaleSpace.getResult();
		ImageJ.main(args);
		ImagePlus imp = ImageJFunctions.wrap(img, "Source");
		Overlay overlay = new Overlay();
		imp.setOverlay(overlay );
		ImageJFunctions.wrapFloat(results, "Scale space").show();
		
		
		/*
		 * Overlay results on source image
		 */
		
		Collection<DifferenceOfGaussianPeak<FloatType>> peaks = scaleSpace.getPeaks();
		for (DifferenceOfGaussianPeak<FloatType> peak : peaks) {
			
			if (!peak.isMax()) {
				continue; // only maxima
			}
			
			double diameter = 2 * peak.getDoublePosition(2) * Math.sqrt(img.numDimensions());
			Roi roi = new OvalRoi(
					0.5 + peak.getDoublePosition(0) - diameter/2, 
					0.5 + peak.getDoublePosition(1) - diameter/2, 
					diameter , diameter);
			overlay.add(roi );
		}
		
		imp.show();
	}

}
