package net.imglib2.display.screenimage.awt;

import net.imglib2.display.awt.SignedShortDataBuffer;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.ShortType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<ShortType, ShortArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class ShortAWTScreenImage extends ArrayImgAWTScreenImage< ShortType, ShortArray >
{

	public ShortAWTScreenImage( final ArrayImg< ShortType, ShortArray > img )
	{
		super( img );
	}

	public ShortAWTScreenImage( final ShortType type, final ShortArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected SignedShortDataBuffer createDataBuffer( final ShortArray data )
	{
		final short[] sourceArray = data.getCurrentStorageArray();
		return new SignedShortDataBuffer( sourceArray, sourceArray.length );
	}

}
