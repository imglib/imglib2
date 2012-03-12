package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Util;

public class ImageJVirtualStackUnsignedShort< S > extends ImageJVirtualStack< S, UnsignedShortType >
{
	public ImageJVirtualStackUnsignedShort( RandomAccessibleInterval< S > source, Converter< S, UnsignedShortType > converter )
	{
		super( source, converter, new UnsignedShortType(), ImagePlus.GRAY16 );

		int maxDisplay = (1 << 16) - 1;
		
		final S s = Util.getTypeFromInterval( source );
		
		if ( BitType.class.isInstance( s ) )
			maxDisplay = 1;
		else if ( Unsigned12BitType.class.isInstance( s ) )
			maxDisplay = 4095;
		
		imageProcessor.setMinAndMax( 0, maxDisplay );
	}
}
