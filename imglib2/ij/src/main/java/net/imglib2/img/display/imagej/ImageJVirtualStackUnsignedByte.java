package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class ImageJVirtualStackUnsignedByte< S extends Type< S > & Comparable< S > > extends ImageJVirtualStack< S, UnsignedByteType >
{
	public ImageJVirtualStackUnsignedByte( RandomAccessibleInterval< S > source, Converter< S, UnsignedByteType > converter )
	{
		super( source, converter, new UnsignedByteType(), ImagePlus.GRAY8 );
		imageProcessor.setMinAndMax( 0, 255 );
	}
}
