package script.imglib.test;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import net.imglib2.RandomAccess;
import net.imglib2.script.bufferedimage.BufferedImageImg;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

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
		
		// Paint onto the BufferedImage, to see if the underlying array has changed
		Graphics2D g = colorImage.createGraphics();
		g.setColor(Color.yellow);
		g.fillRect(140, 140, 20, 20);
		g.dispose();
		
		// Check that a given pixel is yellow in all views of the same data
		// 1. In the original ColorProcessor
		IJ.log("ColorProcessor pixel array color is yellow: " + ((((int[])cp.getPixels())[cp.getWidth() * 140 + 140] & 0x00ffffff) == 0x00ffff00));
		// 2. In the BufferedImageImg
		RandomAccess<ARGBType> ra = c.randomAccess();
		ra.setPosition(new long[]{140, 140});
		IJ.log("Img pixel at x=140, y=140 is yellow: " + ((ra.get().get() & 0x00ffffff) == 0x00ffff00));
		// 3. In the BufferedImage
		int[] pix = new int[4];
		PixelGrabber pg = new PixelGrabber(colorImage, 140, 140, 1, 1, pix, 0, 1);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		IJ.log("BufferedImage pixel at x=140, y=140 is yellow: " + ((pix[0] & 0x00ffffff) == 0x00ffff00));
		
		
		// All print true!
		
		// Redraw the ImageProcessor's image
		imp.updateAndDraw();

		// Test that the BufferedImage has been edited: show it
		new ImagePlus("color image", new ColorProcessor(colorImage)).show();
		

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
