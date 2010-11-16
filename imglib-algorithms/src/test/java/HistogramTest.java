import java.util.ArrayList;
import java.util.Collections;

import ij.IJ;
import ij.io.OpenDialog;
import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBin;
import mpicbg.imglib.algorithm.histogram.HistogramBinFactory;
import mpicbg.imglib.algorithm.histogram.HistogramKey;

import mpicbg.imglib.algorithm.histogram.discrete.DiscreteIntHistogramBinFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.IntegerType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;


public class HistogramTest {

	
	//<R extends IntegerType<R>, T extends RealType<T>> 
	public static void main(String[] args)
	{
		ImageFactory<UnsignedByteType> imFactory =
			new ImageFactory<UnsignedByteType>(new UnsignedByteType(),
					new ArrayContainerFactory());
		Image<UnsignedByteType> im = imFactory.createImage(new int[]{3, 3});
		Cursor<UnsignedByteType> cursor = im.createCursor();
		Histogram<UnsignedByteType> histogram;
		HistogramBinFactory<UnsignedByteType> binFactory = 
			new DiscreteIntHistogramBinFactory<UnsignedByteType>();
		IJ.log("" + true);

		HistogramKey<UnsignedByteType> key1 = binFactory.createKey(new UnsignedByteType(1));
		HistogramKey<UnsignedByteType> key2 = binFactory.createKey(new UnsignedByteType(1));
		
		if (key1.equals(key2))
		{
			IJ.log("Phew, equals returns ok");			
		}
		else
		{
			IJ.log("We Are Boned.");
		}
		
		int i = 0;		
		while (cursor.hasNext())
		{
			cursor.fwd();
			cursor.getType().set(i % 4);
			++i;
		}
		
		histogram = new Histogram<UnsignedByteType>(
					binFactory,
					im.createCursor());
		
		histogram.process();
		
		for (HistogramKey<UnsignedByteType> k  : histogram.getKeys())
		{
			HistogramBin<UnsignedByteType> bin = histogram.getBin(k);
			IJ.log("" + bin.getCenter() + " : " + bin.getCount());
		}

		
//		OpenDialog od = new OpenDialog("Select an Image File", "");
//		Image<T> im = LOCI.openLOCI(od.getDirectory() + od.getFileName(), 
//				new ArrayContainerFactory());
//		Image<R> intIm = (Image<R>)im;
//		//Image<R> intIm = (Image<R>) im;
//		HistogramBinFactory<R> binFactory = new DiscreteIntHistogramBinFactory<R>();
//		Histogram<R> histogram = new Histogram<R>(binFactory, intIm.createCursor());
//		histogram.process();
//
//		ArrayList<HistogramKey<R>> binKeys = histogram.getKeys();
//		
//		ArrayList<R> keys = histogram.getKeyTypes();
//
//		
//		//Collections.sort(keys);
//		
//		for (HistogramKey<R> k  : binKeys)
//		{
//			HistogramBin<R> bin = histogram.getBin(k);
//			IJ.log("" + bin.getCenter() + " : " + bin.getCount());
//		}
	}
	
}
