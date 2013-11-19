package net.imglib2.display.screenimage.awt;

import java.awt.image.DataBufferByte;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * A {@link AWTScreenImage} that is an
 * {@code ArrayImg<UnsignedByteType, ByteArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class UnsignedByteAWTScreenImage extends ArrayImgAWTScreenImage< UnsignedByteType, ByteArray >
{

	public UnsignedByteAWTScreenImage( final ArrayImg< UnsignedByteType, ByteArray > img )
	{
		super( img );
	}

	public UnsignedByteAWTScreenImage( final UnsignedByteType type, final ByteArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected DataBufferByte createDataBuffer( final ByteArray data )
	{
		final byte[] sourceArray = data.getCurrentStorageArray();
		return new DataBufferByte( sourceArray, sourceArray.length );
	}

}
