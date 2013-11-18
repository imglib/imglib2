package net.imglib2.display.screenimage.awt;

import java.awt.image.DataBufferInt;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.IntType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<ShortType, ShortArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class IntAWTScreenImage extends ArrayImgAWTScreenImage< IntType, IntArray >
{

	public IntAWTScreenImage( final ArrayImg< IntType, IntArray > img )
	{
		super( img );
	}

	public IntAWTScreenImage( final IntType type, final IntArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected DataBufferInt createDataBuffer( final IntArray data )
	{
		final int[] sourceArray = data.getCurrentStorageArray();
		return new DataBufferInt( sourceArray, sourceArray.length );
	}

}
