package mpicbg.imglib.image.display.imagej;

import ij.ImagePlus;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.converter.Converter;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;

public class ImageJVirtualStackUnsignedShort< S extends Type< S > & Comparable< S > > extends ImageJVirtualStack< S, UnsignedShortType >
{
	public ImageJVirtualStackUnsignedShort( RandomAccessibleInterval< S > source, Converter< S, UnsignedShortType > converter )
	{
		super( source, converter, new UnsignedShortType(), ImagePlus.GRAY16 );
		imageProcessor.setMinAndMax( 0, (1 << 16) - 1 );
	}
}
