package mpicbg.imglib.image.display.imagej;

import ij.ImagePlus;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.converter.Converter;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

public class ImageJVirtualStackUnsignedByte< S extends Type< S > & Comparable< S > > extends ImageJVirtualStack< S, UnsignedByteType >
{
	public ImageJVirtualStackUnsignedByte( RandomAccessibleInterval< S > source, Converter< S, UnsignedByteType > converter )
	{
		super( source, converter, new UnsignedByteType(), ImagePlus.GRAY8 );
		imageProcessor.setMinAndMax( 0, 255 );
	}
}
