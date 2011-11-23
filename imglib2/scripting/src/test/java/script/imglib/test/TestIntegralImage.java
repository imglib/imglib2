package script.imglib.test;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.IntegralImage;
import net.imglib2.script.img.FloatImage;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class TestIntegralImage {

	@SuppressWarnings("unused")
	static public final void main(String[] args) {
		try {
			FloatImage fi = new FloatImage(new long[]{10, 10, 10});
			Cursor<FloatType> c = fi.cursor();
			while (c.hasNext()) {
				c.next().set(1);
			}
			IntegralImage ig = new IntegralImage(fi);
			
			RandomAccess<DoubleType> p = ig.randomAccess();
			p.setPosition(new long[]{9, 9, 9});
			System.out.println("Test integral image passed: " + ((10 * 10 * 10) == p.get().get()));
			
			new ImageJ();
			ImgLib.show(ig, "Integral Image");
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
}
