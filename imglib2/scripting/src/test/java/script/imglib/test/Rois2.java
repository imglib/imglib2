package script.imglib.test;

import ij.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.sampler.special.ConstantRandomAccessible;
import net.imglib2.script.math.Add;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Rois2 {

	
	static public final void main(String[] args) {
		
		// Generate some data:
		// A virtual image with a ROI filled with value 127
		RandomAccessibleInterval<FloatType> img1 =
			// The 'image'
			Views.interval(
				// The outside, with value 0
				Views.extendValue(
					// The ROI filled with value 127
					Views.interval(
						new ConstantRandomAccessible<FloatType>(new FloatType(127), 2),
						// The domain of the ROI
						new long[]{100, 100},
						new long[]{399, 399}),
					new FloatType(0)),
				// The domain of the image
				new long[]{0, 0},
				new long[]{511, 511});
		
		// A virtual image with a ROI filled with value 128
		RandomAccessibleInterval<FloatType> img2 =
			// The 'image'
			Views.interval(
				// The outside, with value 0
				Views.extendValue(
					// The ROI filled with value 128
					Views.interval(
						new ConstantRandomAccessible<FloatType>(new FloatType(128), 2),
						// The domain of the ROI
						new long[]{10, 30},
						new long[]{209, 229}),
					new FloatType(0)),
				// The domain of the image
				new long[]{0, 0},
				new long[]{511, 511});
		
		// Add two ROIs of both images
		RectangleROI<FloatType> r1 = new RectangleROI<FloatType>(img1, 50, 50, 200, 200);
		RectangleROI<FloatType> r2 = new RectangleROI<FloatType>(img2, 50, 50, 200, 200);
		try {
			// The 'result' is the first image that actually has any data in it!
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

