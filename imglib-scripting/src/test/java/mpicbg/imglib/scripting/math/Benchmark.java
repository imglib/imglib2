package mpicbg.imglib.scripting.math;

import mpicbg.imglib.algorithm.roi.MedianFilter;
import mpicbg.imglib.algorithm.roi.StructuringElement;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.scripting.math.ASin;
import mpicbg.imglib.scripting.math.Add;
import mpicbg.imglib.scripting.math.Cbrt;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.Divide;
import mpicbg.imglib.scripting.math.Multiply;
import mpicbg.imglib.scripting.math.Pow;
import mpicbg.imglib.scripting.math.Sin;
import mpicbg.imglib.scripting.math.Sqrt;
import mpicbg.imglib.scripting.math.Subtract;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.real.FloatType;

/* Tested in a MacBookPro 5,5, 4 Gb RAM, 2.4 Ghz
 * running Ubuntu 10.04 with Java 1.6.0_21
 *
 * 2010-11-24
 *
Opening '/home/albert/Desktop/t2/bridge.gif' [512x512x1 type=uint8 image=Image<ByteType>]
LOCI.openLOCI(): Cannot read metadata, setting calibration to 1
Start direct (correct illumination)...
  elapsed: 40
Start script (correct illumination)...
  elapsed: 40
Start direct (correct illumination)...
  elapsed: 46
Start script (correct illumination)...
  elapsed: 37
Start direct (correct illumination)...
  elapsed: 9
Start script (correct illumination)...
  elapsed: 15
Start direct (correct illumination)...
  elapsed: 8
Start script (correct illumination)...
  elapsed: 15
Start direct with heavy operations...
  elapsed: 421
Start script with heavy operations...
  elapsed: 417
Start direct with heavy operations...
  elapsed: 392
Start script with heavy operations...
  elapsed: 394
Start direct with heavy operations...
  elapsed: 387
Start script with heavy operations...
  elapsed: 395
Start direct with heavy operations...
  elapsed: 388
Start script with heavy operations...
  elapsed: 395

In conclusion: the scripting way is about 1.8x slower for relatively simple operations,
but about 1x for heavy operations!
 */
public class Benchmark {

	static public final void p(String s) {
		System.out.println(s);
	}

	static public Image<FloatType> scriptCorrectIllumination(
			final Image<? extends RealType<?>> img,
			final Image<? extends RealType<?>> brightfield,
			final Image<? extends RealType<?>> darkfield,
			final double mean) throws Exception {
		p("Start script (correct illumination)...");
		long t0 = System.currentTimeMillis();
		Image<FloatType> corrected = Compute.inFloats(
				new Multiply(
						new Divide(
								new Subtract(img, brightfield),
								new Subtract(brightfield, darkfield)),
						mean));
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}

	static public Image<FloatType> correctIllumination(
			final Image<? extends RealType<?>> img,
			final Image<? extends RealType<?>> brightfield,
			final Image<? extends RealType<?>> darkfield,
			final double mean) {
		p("Start direct (correct illumination)...");
		long t0 = System.currentTimeMillis();
		ImageFactory<FloatType> factory = new ImageFactory<FloatType>(new FloatType(), img.getContainerFactory());
		Image<FloatType> corrected = factory.createImage(img.getDimensions(), "result");
		final Cursor<FloatType> c = corrected.createCursor();
		final Cursor<? extends RealType<?>> ci = img.createCursor(),
											cb = brightfield.createCursor(),
											cd = darkfield.createCursor();
		while (c.hasNext()) {
			c.fwd();
			ci.fwd();
			cb.fwd();
			cd.fwd();
			c.getType().setReal( (  (ci.getType().getRealDouble() - cb.getType().getRealDouble())
								  / (cb.getType().getRealDouble() - cd.getType().getRealDouble()))
								 * mean);
		}
		corrected.removeAllCursors();
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}

	static public Image<FloatType> scriptHeavyOperations(
			final Image<? extends RealType<?>> img) throws Exception {
		p("Start script with heavy operations...");
		long t0 = System.currentTimeMillis();
		Image<FloatType> corrected = Compute.inFloats(
				new Multiply(
					new ASin(
						new Sin(
							new Divide(
								new Pow(new Sqrt(img), 2),
								new Pow(new Cbrt(img), 3)))),
					img));
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}

	static public Image<FloatType> heavyOperations(
			final Image<? extends RealType<?>> img) {
		p("Start direct with heavy operations...");
		long t0 = System.currentTimeMillis();
		ImageFactory<FloatType> factory = new ImageFactory<FloatType>(new FloatType(), img.getContainerFactory());
		Image<FloatType> corrected = factory.createImage(img.getDimensions(), "result");
		final Cursor<FloatType> c = corrected.createCursor();
		final Cursor<? extends RealType<?>> ci = img.createCursor();
		while (c.hasNext()) {
			c.fwd();
			ci.fwd();
			c.getType().setReal(
					Math.asin(
						Math.sin(
							Math.pow(Math.sqrt(ci.getType().getRealDouble()), 2)
							/ Math.pow(Math.cbrt(ci.getType().getRealDouble()), 3)))
					* ci.getType().getRealDouble());					
		}
		corrected.removeAllCursors();
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}
	
	static public Image<FloatType> sum(
			final Image<? extends RealType<?>> img) throws Exception {
		LocalizableByDimCursor<? extends RealType<?>> c = img.createLocalizableByDimCursor();
		c.setPosition(new int[]{348, 95});
		System.out.println("Original pixel at 348,95: " + c.getType().getRealDouble());

		Image<FloatType> result = Compute.inFloats(new Add(img, img, img, img));

		LocalizableByDimCursor<? extends RealType<?>> r = result.createLocalizableByDimCursor();
		r.setPosition(new int[]{348, 95});
		System.out.println("After varargs addition, pixel at 348,95: " + r.getType().getRealDouble()
				+ " which is 4 * val: " + (c.getType().getRealDouble() * 4 == r.getType().getRealDouble()));

		img.removeAllCursors();
		result.removeAllCursors();
		
		return result;
	}

	public static void main(String[] args) {
		try {
			//String src = "http://imagej.nih.gov/ij/images/bridge.gif";
			String src = "/home/albert/Desktop/t2/bridge.gif";
			Image<UnsignedByteType> img = LOCI.openLOCIUnsignedByteType(src, new ArrayContainerFactory());
			//
			double mean = 0;
			for (final UnsignedByteType t : img) mean += t.getRealDouble();
			mean /= img.size();
			//
			MedianFilter<UnsignedByteType> mf =
				new MedianFilter<UnsignedByteType>(img, StructuringElement.createBall(2, img.getDimension(0) / 2));
			//mf.process();
			Image<UnsignedByteType> brightfield = mf.getResult();
			//
			Image<UnsignedByteType> darkfield = img.createNewImage(); // empty

			// Test:
			for (int i=0; i<4; i++) {
				correctIllumination(img, brightfield, darkfield, mean);
				scriptCorrectIllumination(img, brightfield, darkfield, mean);
			}
			for (int i=0; i<4; i++) {
				heavyOperations(img);
				scriptHeavyOperations(img);
			}
			
			// Test varargs:
			sum(img);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}