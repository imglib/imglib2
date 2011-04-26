package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class ImageJVirtualStackUnsignedShort< S extends Type< S > & Comparable< S > > extends ImageJVirtualStack< S, UnsignedShortType >
{
	public ImageJVirtualStackUnsignedShort( RandomAccessibleInterval< S > source, Converter< S, UnsignedShortType > converter )
	{
		super( source, converter, new UnsignedShortType(), ImagePlus.GRAY16 );
		imageProcessor.setMinAndMax( 0, (1 << 16) - 1 );
	}
}
