package net.imglib2.display.screenimage.awt;

import java.awt.image.DataBufferDouble;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<DoubleType, DoubleArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class DoubleAWTScreenImage extends ArrayImgAWTScreenImage< DoubleType, DoubleArray >
{

	public DoubleAWTScreenImage( final ArrayImg< DoubleType, DoubleArray > img )
	{
		super( img );
	}

	public DoubleAWTScreenImage( final DoubleType type, final DoubleArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected DataBufferDouble createDataBuffer( final DoubleArray data )
	{
		final double[] sourceArray = data.getCurrentStorageArray();
		return new DataBufferDouble( sourceArray, sourceArray.length );
	}

}
