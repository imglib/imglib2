package script.imglib.test;

import java.awt.image.BufferedImage;

import net.imglib.script.bufferedimage.BufferedImageImg;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

public class TestBufferedImageImg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new ImageJ();
		ImagePlus imp = IJ.openImage("http://imagej.nih.gov/ij/images/lena-std.tif");
		imp.show();

		// Test that original image is edited: a black rectangle should appear
		ColorProcessor cp = (ColorProcessor) imp.getProcessor();
		BufferedImage colorImage = (BufferedImage) cp.createImage();
		BufferedImageImg<ARGBType> c = new BufferedImageImg<ARGBType>(colorImage);
		for (ARGBType t : Views.flatIterable(Views.interval(c, new long[]{100, 100}, new long[]{199, 199}))) {
			t.setZero();
		}
		imp.updateAndDraw();

		// Test that grayscale image is edited: a black rectangle should appear
		ByteProcessor bp = (ByteProcessor) cp.convertToByte(false);
		BufferedImage grayImage = (BufferedImage) bp.createImage();
		BufferedImageImg<UnsignedByteType> b = new BufferedImageImg<UnsignedByteType>(grayImage);
		for (UnsignedByteType t : Views.flatIterable(Views.interval(b, new long[]{100, 100}, new long[]{199, 199}))) {
			t.setZero();
		}
		new ImagePlus("gray image", bp).show();
	}

}
