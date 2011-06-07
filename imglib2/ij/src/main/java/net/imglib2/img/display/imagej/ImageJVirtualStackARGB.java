package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;

public class ImageJVirtualStackARGB< S > extends ImageJVirtualStack< S, ARGBType >
{
	public ImageJVirtualStackARGB( RandomAccessibleInterval< S > source, Converter< S, ARGBType > converter )
	{
		super( source, converter, new ARGBType(), ImagePlus.COLOR_RGB );
		imageProcessor.setMinAndMax( 0, 255 );
	}
}
