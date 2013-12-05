package net.imglib2.display.screenimage.awt;

import net.imglib2.display.awt.UnsignedIntDataBuffer;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.UnsignedIntType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<ShortType, ShortArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class UnsignedIntAWTScreenImage extends ArrayImgAWTScreenImage< UnsignedIntType, IntArray >
{

	public UnsignedIntAWTScreenImage( final ArrayImg< UnsignedIntType, IntArray > img )
	{
		super( img );
	}

	public UnsignedIntAWTScreenImage( final UnsignedIntType type, final IntArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected UnsignedIntDataBuffer createDataBuffer( final IntArray data )
	{
		final int[] sourceArray = data.getCurrentStorageArray();
		return new UnsignedIntDataBuffer( sourceArray, sourceArray.length );
	}

}
