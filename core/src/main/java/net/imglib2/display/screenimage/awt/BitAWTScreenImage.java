package net.imglib2.display.screenimage.awt;

import net.imglib2.display.awt.BitDataBuffer;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.logic.BitType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<BitType, BitArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class BitAWTScreenImage extends ArrayImgAWTScreenImage< BitType, BitArray >
{

	public BitAWTScreenImage( final ArrayImg< BitType, BitArray > img )
	{
		super( img );
	}

	public BitAWTScreenImage( final BitType type, final BitArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected BitDataBuffer createDataBuffer( final BitArray data )
	{
		final int[] sourceArray = data.getCurrentStorageArray();
		return new BitDataBuffer( sourceArray, sourceArray.length );
	}
}
