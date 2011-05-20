package script.imglib.test;

import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.math.ASin;
import net.imglib2.script.math.Abs;
import net.imglib2.script.math.Add;
import net.imglib2.script.math.Cbrt;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.Difference;
import net.imglib2.script.math.Divide;
import net.imglib2.script.math.Identity;
import net.imglib2.script.math.Multiply;
import net.imglib2.script.math.Pow;
import net.imglib2.script.math.Sin;
import net.imglib2.script.math.Sqrt;
import net.imglib2.script.math.Subtract;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/* Tested in a MacBookPro 5,5, 4 Gb RAM, 2.4 Ghz
 * running Ubuntu 10.04 with Java 1.6.0_21
 *
 * 2010-11-24
 *
Opening '/home/albert/Desktop/t2/bridge.gif' [512x512x1 type=uint8 image=Image<ByteType>]
LOCI.openLOCI(): Cannot read metadata, setting calibration to 1
Gauss processing time: 2060
Start direct (correct illumination)...
  elapsed: 108.590441  image: result
Start script (correct illumination)...
  elapsed: 56.862998 image result
Start direct (correct illumination)...
  elapsed: 138.54601  image: result
Start script (correct illumination)...
  elapsed: 18.287601 image result
Start direct (correct illumination)...
  elapsed: 7.65112  image: result
Start script (correct illumination)...
  elapsed: 15.75756 image result
Start direct (correct illumination)...
  elapsed: 8.854321  image: result
Start script (correct illumination)...
  elapsed: 15.40572 image result
Start direct with heavy operations...
  elapsed: 361
Start script with heavy operations...
  elapsed: 390
Start direct with heavy operations...
  elapsed: 402
Start script with heavy operations...
  elapsed: 352
Start direct with heavy operations...
  elapsed: 346
Start script with heavy operations...
  elapsed: 355
Start direct with heavy operations...
  elapsed: 349
Start script with heavy operations...
  elapsed: 372
Start differenceFn
  elapsed: 17.939521
Start differenceCompFn
  elapsed: 25.551281
Start differenceFn
  elapsed: 15.748361
Start differenceCompFn
  elapsed: 10.02808
Start differenceFn
  elapsed: 6.3574
Start differenceCompFn
  elapsed: 9.96048
Start differenceFn
  elapsed: 5.7024
Start differenceCompFn
  elapsed: 6.0376
Original pixel at 348,95: 190.0
After varargs addition, pixel at 348,95: 760.0 which is 4 * val: true

In conclusion: the scripting way is about 1.8x slower for relatively simple operations,
but about 1x for heavy operations!

 */
public class Benchmark {

	static public final void p(String s) {
		System.out.println(s);
	}

	static public Img<FloatType> scriptCorrectIllumination(
			final Img<? extends RealType<?>> img,
			final Img<? extends RealType<?>> brightfield,
			final Img<? extends RealType<?>> darkfield,
			final double mean) throws Exception {
		p("Start script (correct illumination)...");
		long t0 = System.nanoTime();
		Img<FloatType> corrected = Compute.inFloats(2,
				new Multiply(
						new Divide(
								new Subtract(img, brightfield),
								new Subtract(brightfield, darkfield)),
						mean));
		p("  elapsed: " + (System.nanoTime() - t0)/1000000.0 + " image ");
		return corrected;
	}

	static public Img<FloatType> correctIllumination(
			final Img<? extends RealType<?>> img,
			final Img<? extends RealType<?>> brightfield,
			final Img<? extends RealType<?>> darkfield,
			final double mean) {
		p("Start direct (correct illumination)...");
		long t0 = System.nanoTime();
		Img<FloatType> corrected = new ArrayImgFactory<FloatType>().create(Util.intervalDimensions(img), new FloatType());
		final Cursor<FloatType> c = corrected.cursor();
		final Cursor<? extends RealType<?>> ci = img.cursor(),
											cb = brightfield.cursor(),
											cd = darkfield.cursor();
		while (c.hasNext()) {
			c.fwd();
			ci.fwd();
			cb.fwd();
			cd.fwd();
			c.get().setReal( (  (ci.get().getRealDouble() - cb.get().getRealDouble())
								  / (cb.get().getRealDouble() - cd.get().getRealDouble()))
								 * mean);
		}
		p("  elapsed: " + (System.nanoTime() - t0)/1000000.0 + "  image");
		return corrected;
	}

	static public Img<FloatType> scriptHeavyOperations(
			final Img<? extends RealType<?>> img) throws Exception {
		p("Start script with heavy operations...");
		long t0 = System.currentTimeMillis();
		Img<FloatType> corrected = Compute.inFloats(1, 
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

	static public Img<FloatType> heavyOperations(
			final Img<? extends RealType<?>> img) {
		p("Start direct with heavy operations...");
		long t0 = System.currentTimeMillis();
		Img<FloatType> corrected = new ArrayImgFactory<FloatType>().create(Util.intervalDimensions(img), new FloatType());
		final Cursor<FloatType> c = corrected.cursor();
		final Cursor<? extends RealType<?>> ci = img.cursor();
		while (c.hasNext()) {
			c.fwd();
			ci.fwd();
			c.get().setReal(
					Math.asin(
						Math.sin(
							Math.pow(Math.sqrt(ci.get().getRealDouble()), 2)
							/ Math.pow(Math.cbrt(ci.get().getRealDouble()), 3)))
					* ci.get().getRealDouble());					
		}
		p("  elapsed: " + (System.currentTimeMillis() - t0));
		return corrected;
	}
	
	static public Img<FloatType> sum(
			final Img<? extends RealType<?>> img) throws Exception {
		RandomAccess<? extends RealType<?>> c = img.randomAccess();
		int[] pos = new int[img.numDimensions()];
		pos[0] = 348;
		pos[1] = 95;
		c.setPosition(pos);
		System.out.println("Original pixel at 348,95: " + c.get().getRealDouble());

		Img<FloatType> result = Compute.inFloats(new Add(img, img, img, img));

		RandomAccess<? extends RealType<?>> r = result.randomAccess();
		r.setPosition(pos);
		System.out.println("After varargs addition, pixel at 348,95: " + r.get().getRealDouble()
				+ " which is 4 * val: " + (c.get().getRealDouble() * 4 == r.get().getRealDouble()));
		
		return result;
	}

	static public Img<FloatType> differenceFn(
			final Img<? extends RealType<?>> img) throws Exception {
		p("Start differenceFn");
		long t0 = System.nanoTime();
		try {
			return Compute.inFloats(new Difference(img, img));
		} finally {
			p("  elapsed: " + (System.nanoTime() - t0)/1000000.0);
		}
	}

	static public Img<FloatType> differenceCompFn(
			final Img<? extends RealType<?>> img) throws Exception {
		p("Start differenceCompFn");
		long t0 = System.nanoTime();
		try {
			return Compute.inFloats(new Abs(new Subtract(img, img)));
		} finally {
			p("  elapsed: " + (System.nanoTime() - t0)/1000000.0);
		}
	}

	/** Copy the contents from img to an ImagePlus; assumes containers are compatible. */
	static public ImagePlus copyToFloatImagePlus(final Img<? extends RealType<?>> img, final String title) {
		ImagePlusImgFactory<FloatType> factory = new ImagePlusImgFactory<FloatType>();
		ImagePlusImg<FloatType, ?> iml = factory.create(img, new FloatType());
		Cursor<FloatType> c1 = iml.cursor();
		Cursor<? extends RealType<?>> c2 = img.cursor();
		while (c1.hasNext()) {
			c1.fwd();
			c2.fwd();
			c1.get().set(c2.get().getRealFloat());
		}
		try {
			ImagePlus imp = iml.getImagePlus();
			imp.setTitle(title);
			return imp;
		} catch (ImgLibException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		try {
			
			new ImageJ();
			
			String src = "http://imagej.nih.gov/ij/images/bridge.gif";
			//String src = "/home/albert/Desktop/t2/bridge.gif";
			Img<UnsignedByteType> img = new ImgOpener().openImg(src);
			//
			double mean = 0;
			for (final UnsignedByteType t : img) mean += t.getRealDouble();
			mean /= img.size();
			//

			/*
			GaussianConvolutionReal<UnsignedByteType> gauss = new GaussianConvolutionReal<UnsignedByteType>( img, new OutOfBoundsMirrorFactory(), 60 );
			gauss.process();
			
			System.out.println( "Gauss processing time: " + gauss.getProcessingTime() );
			
			Image<UnsignedByteType> brightfield = gauss.getResult();
			*/
			
			/*
			DownSample<UnsignedByteType> downSample = new DownSample<UnsignedByteType>( img, 0.25f );			
			downSample.process();
			Image<UnsignedByteType> down = downSample.getResult();
			
			ImageJFunctions.show( down );
			
			AffineModel2D model = new AffineModel2D();
			model.set( 4.03f, 0, 0, 4.03f, 0, 0 );
			
			ImageTransform<UnsignedByteType> imgTransform = new ImageTransform<UnsignedByteType>( brightfield, model, new LinearInterpolatorFactory<UnsignedByteType>( new OutOfBoundsStrategyMirrorFactory<UnsignedByteType>()) );
			imgTransform.process();
			
			brightfield = imgTransform.getResult();
			*/
			
			/*
			ImageJFunctions.show( img );
			ImageJFunctions.show( brightfield );
			*/

			// A simulated brightfield image
			// Until Gauss is restored, just use an image with half-tone
			Img<UnsignedByteType> brightfield = img.factory().create(Util.intervalDimensions(img), new UnsignedByteType());
			for (UnsignedByteType t : brightfield) {
				t.set(127);
			}
			
			// Test:
			System.out.println("img.numDimensions: " + img.numDimensions());
			copyToFloatImagePlus(img, "original").show();
			copyToFloatImagePlus(brightfield, "brightfield").show();
			copyToFloatImagePlus(Compute.inFloats(2, new Identity(img)), "identity").show();
			
			// A black image: empty
			Img<UnsignedByteType> darkfield = img.factory().create(Util.intervalDimensions(img), new UnsignedByteType());

			// Test:
			for (int i=0; i<4; i++) {
				Img<FloatType> corrected1 = correctIllumination(img, brightfield, darkfield, mean);
				Img<FloatType> corrected2 = scriptCorrectIllumination(img, brightfield, darkfield, mean);
				
				if ( i == 0 ) {
					copyToFloatImagePlus(corrected1, "corrected direct").show();
					copyToFloatImagePlus(corrected2, "corrected script").show();
				}
			}

			for (int i=0; i<4; i++) {
				heavyOperations(img);
				scriptHeavyOperations(img);
			}

			// Compare Difference(img) vs. Abs(Subtract(img1, img2))
			for (int i=0; i<4; i++) {
				differenceFn(img);
				differenceCompFn(img);
			}

			// Test varargs:
			sum(img);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
