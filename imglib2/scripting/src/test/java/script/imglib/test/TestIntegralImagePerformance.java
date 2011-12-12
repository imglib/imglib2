package script.imglib.test;

import ij.process.FloatProcessor;
import mpicbg.ij.integral.DoubleIntegralImage;
import net.imglib2.script.algorithm.IntegralImage;
import net.imglib2.script.img.FloatImage;


/** Compare performance with Saalfeld's 2d {@link DoubleIntegralImage}.
 * 
 * Beating Saalfeld's by a factor of 5!
 */
public class TestIntegralImagePerformance {

	public static void main(String[] args) {
		final int side = 2048;
		final float[] pix = new float[side * side];
		final FloatProcessor fp = new FloatProcessor(side, side, pix, null);
		final FloatImage fii = new FloatImage(new long[]{side, side}, pix);
		
		for (int i=0; i<10; ++i) {
			long t0 = System.currentTimeMillis();
			DoubleIntegralImage dii = new DoubleIntegralImage(fp);
			long t1 = System.currentTimeMillis();
			IntegralImage dig = new IntegralImage(fii); // also uses double as type
			long t2 = System.currentTimeMillis();
			System.out.println("Saalfelds: " + (t2 - t1) + " ms -- imglib2.script: " + (t1 - t0) + " ms");
		}
	}

}
