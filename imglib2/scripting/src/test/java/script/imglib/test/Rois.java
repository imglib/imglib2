package script.imglib.test;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.script.math.Add;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;

public class Rois {

	static public final <T extends NumericType<T>> void main(String[] args) {
		
		// Generate some data
		FloatProcessor b1 = new FloatProcessor(512, 512);
		b1.setValue(127);
		b1.setRoi(new Roi(100, 100, 200, 200));
		b1.fill();
		
		FloatProcessor b2 = new FloatProcessor(512, 512);
		b2.setValue(128);
		b2.setRoi(new Roi(10, 30, 200, 200));
		b2.fill();
		
		Img<T> img1 = ImageJFunctions.wrap(new ImagePlus("1", b1));
		Img<T> img2 = ImageJFunctions.wrap(new ImagePlus("2", b2));
		
		
		// Add two ROIs of both images
		RectangleROI<T> r1 = new RectangleROI<T>(img1, 50, 50, 200, 200);
		RectangleROI<T> r2 = new RectangleROI<T>(img2, 50, 50, 200, 200);
		try {
			Img<FloatType> result = new Add(r1, r2).asImage(1);
			
			new ImageJ();
			
			ImageJFunctions.show(r1, "r1");
			ImageJFunctions.show(r2, "r2");
			ImageJFunctions.show(img1, "img1");
			ImageJFunctions.show(img2, "img2");
			ImageJFunctions.show(result, "added rois");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
