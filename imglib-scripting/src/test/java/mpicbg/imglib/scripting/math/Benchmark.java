package mpicbg.imglib.scripting.math;

import mpicbg.imglib.algorithm.roi.MedianFilter;
import mpicbg.imglib.algorithm.roi.StructuringElement;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.real.FloatType;

/* Tested in a MacBookPro 5,5, 4 Gb RAM, 2.4 Ghz
 * running Ubuntu 10.04 with Java 1.6.0_21
 *
 * 2010-11-23
 *
Opening 'http://imagej.nih.gov/ij/images/bridge.gif' [512x512x1 type=uint8 image=Image<ByteType>]
LOCI.openLOCI(): Cannot read metadata, setting calibration to 1
Start direct (correct illumination)...
  elapsed: 43
Start script (correct illumination)...
  elapsed: 100
Start direct (correct illumination)...
  elapsed: 42
Start script (correct illumination)...
  elapsed: 63
Start direct (correct illumination)...
  elapsed: 9
Start script (correct illumination)...
  elapsed: 24
Start direct (correct illumination)...
  elapsed: 9
Start script (correct illumination)...
  elapsed: 26
Start direct with heavy operations...
  elapsed: 421
Start script with heavy operations...
  elapsed: 508
Start direct with heavy operations...
  elapsed: 389
Start script with heavy operations...
  elapsed: 419
Start direct with heavy operations...
  elapsed: 375
Start script with heavy operations...
  elapsed: 419
Start direct with heavy operations...
  elapsed: 376
Start script with heavy operations...
  elapsed: 424

In conclusion: the scripting way is about 3x slower for relatively simple operations,
but about 1.1x for heavy operations!
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
				new Multiply<FloatType>(
						new Divide<FloatType>(
								new Subtract<FloatType>(img, brightfield),
								new Subtract<FloatType>(brightfield, darkfield)),
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
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}

	static public Image<FloatType> scriptHeavyOperations(
			final Image<? extends RealType<?>> img) throws Exception {
		p("Start script with heavy operations...");
		long t0 = System.currentTimeMillis();
		Image<FloatType> corrected = Compute.inFloats(
				new Multiply<FloatType>(
					new ASin<FloatType>(
						new Sin<FloatType>(
							new Divide<FloatType>(
								new Pow<FloatType>(new Sqrt<FloatType>(img), 0.5),
								new Pow<FloatType>(new Cbrt<FloatType>(img), 1.0/3)))),
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
							Math.pow(Math.sqrt(ci.getType().getRealDouble()), 0.5)
							/ Math.pow(Math.cbrt(ci.getType().getRealDouble()), 1.0/3)))
					* ci.getType().getRealDouble());					
		}
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}

	public static void main(String[] args) {
		try {
			String src = "http://imagej.nih.gov/ij/images/bridge.gif";
			Image<UnsignedByteType> img = LOCI.openLOCIUnsignedByteType(src, new ArrayContainerFactory());
			//
			double mean = 0;
			for (final UnsignedByteType t : img) mean += t.getRealDouble();
			mean /= img.size();
			//
			MedianFilter<UnsignedByteType> mf =
				new MedianFilter<UnsignedByteType>(img, StructuringElement.createBall(2, img.getDimension(0) / 2));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}