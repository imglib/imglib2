package net.imglib2.display.screenimage.awt;

import net.imglib2.display.awt.SignedByteDataBuffer;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.ByteType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<ByteType, ByteArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class ByteAWTScreenImage extends ArrayImgAWTScreenImage< ByteType, ByteArray >
{

	public ByteAWTScreenImage( final ArrayImg< ByteType, ByteArray > img )
	{
		super( img );
	}

	public ByteAWTScreenImage( final ByteType type, final ByteArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected SignedByteDataBuffer createDataBuffer( final ByteArray data )
	{
		final byte[] sourceArray = data.getCurrentStorageArray();
		return new SignedByteDataBuffer( sourceArray, sourceArray.length );
	}

}
