package script.imglib.test;

import ij.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.edit.Insert;
import net.imglib2.script.img.FloatImage;
import net.imglib2.script.math.Add;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.ImageFunction;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.numeric.real.FloatType;

public class Insert_and_RectangleROI {

	@SuppressWarnings("boxing")
	static public final void main(String[] args) {
		// Create image
		long[] dim = new long[]{512, 512};
		
		Img<FloatType> im1 = new FloatImage(dim);

		// Create two images with various areas filled
		Add fn1 = new Add(127, new RectangleROI<FloatType>(im1, 100, 100, dim[0] - 200, dim[1] - 200));
		Add fn2 = new Add(255, new RectangleROI<FloatType>(im1, 0, 0, 100, 100));

		try {
			// Insert the second into the first
			Insert<FloatType, Img<FloatType>, FloatType> fn3 =
				new Insert<FloatType, Img<FloatType>, FloatType>(fn2.asImage(), fn1.asImage(), new long[]{-50, -20});
			Insert<FloatType, Img<FloatType>, FloatType> fn4 =
				new Insert<FloatType, Img<FloatType>, FloatType>(fn2.asImage(), fn1.asImage(), new long[]{250, 300});
			
			new ImageJ();
			
			ImgLib.show(Compute.inFloats(new ImageFunction<FloatType>(fn3)));
			ImgLib.show(Compute.inFloats(new ImageFunction<FloatType>(fn4)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
