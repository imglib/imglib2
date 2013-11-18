package net.imglib2.display.screenimage.awt;

import java.awt.image.DataBufferUShort;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * A {@link AWTScreenImage} that is an
 * {@code ArrayImg<UnsignedShortType, ShortArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class UnsignedShortAWTScreenImage extends ArrayImgAWTScreenImage< UnsignedShortType, ShortArray >
{

	public UnsignedShortAWTScreenImage( final ArrayImg< UnsignedShortType, ShortArray > img )
	{
		super( img );
	}

	public UnsignedShortAWTScreenImage( final UnsignedShortType type, final ShortArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected DataBufferUShort createDataBuffer( final ShortArray data )
	{
		final short[] sourceArray = data.getCurrentStorageArray();
		return new DataBufferUShort( sourceArray, sourceArray.length );
	}

}
