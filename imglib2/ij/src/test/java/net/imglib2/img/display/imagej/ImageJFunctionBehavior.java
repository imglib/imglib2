package net.imglib2.img.display.imagej;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import ij.IJ;
import ij.ImagePlus;

public class ImageJFunctionBehavior {
	
	static private final void print(final Img<?> img) {
		System.out.println("img: " + img);
	}
	static private final void print(final ImagePlus imp) {
		System.out.println("imp: " + imp);
	}
	
	static public final void main(String[] arg) {
		final ImagePlus imp = IJ.openImage("http://imagej.nih.gov/ij/images/bat-cochlea-volume.zip");
		
		System.out.println("Opened image: " + imp);
		
		// 1. Test ImagePlus -> Img, specific wrappers
		IJ.run(imp, "8-bit", "");
		Img<UnsignedByteType> imgb = ImageJFunctions.wrapByte(imp);
		print(imgb);
		
		IJ.run(imp, "16-bit", "");
		Img<UnsignedShortType> imgs = ImageJFunctions.wrapShort(imp);
		print(imgs);
		
		IJ.run(imp, "32-bit", "");
		Img<FloatType> imgf = ImageJFunctions.wrapFloat(imp);
		print(imgf);
		
		IJ.run(imp, "RGB Color", "");
		Img<ARGBType> imgRGB = ImageJFunctions.wrapRGBA(imp);
		print(imgRGB);
		
		// 2. Test ImagePlus -> Img, generic wrapper
		IJ.run(imp, "8-bit", "");
		Img<UnsignedByteType> g_imgb = ImageJFunctions.wrap(imp);
		print(g_imgb);
		
		IJ.run(imp, "16-bit", "");
		Img<UnsignedShortType> g_imgs = ImageJFunctions.wrap(imp);
		print(g_imgs);
		
		IJ.run(imp, "32-bit", "");
		Img<FloatType> g_imgf = ImageJFunctions.wrap(imp);
		print(g_imgf);

		IJ.run(imp, "RGB Color", "");
		Img<ARGBType> g_imgRGB = ImageJFunctions.wrap(imp);
		print(g_imgRGB);
		
		// 3. Test Img -> ImagePlus, specific wrappers
		ImagePlus impb = ImageJFunctions.wrapUnsignedByte(imgb, "byte Img");
		print(impb);
		
		ImagePlus imps = ImageJFunctions.wrapUnsignedShort(imgs, "short Img");
		print(imps);
		
		ImagePlus impf = ImageJFunctions.wrapFloat(imgf, "float Img");
		print(impf);
		
		ImagePlus impRGB = ImageJFunctions.wrapRGB(imgRGB, "RGB Img");
		print(impRGB);
		
		// 4. Test Img -> ImagePlus, generic wrappers
		ImagePlus g_impb = ImageJFunctions.wrap(imgb, "byte Img");
		print(g_impb);
		
		ImagePlus g_imps = ImageJFunctions.wrap(imgs, "short Img");
		print(g_imps);
		
		ImagePlus g_impf = ImageJFunctions.wrap(imgf, "float Img");
		print(g_impf);
		
		ImagePlus g_impRGB = ImageJFunctions.wrap(imgRGB, "RGB Img");
		print(g_impRGB);
	}
}
