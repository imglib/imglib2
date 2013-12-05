package net.imglib2.display.screenimage.awt;

import java.awt.image.DataBufferFloat;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A {@link AWTScreenImage} that is an {@code ArrayImg<FloatType, FloatArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class FloatAWTScreenImage extends ArrayImgAWTScreenImage< FloatType, FloatArray >
{

	public FloatAWTScreenImage( final ArrayImg< FloatType, FloatArray > img )
	{
		super( img );
	}

	public FloatAWTScreenImage( final FloatType type, final FloatArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected DataBufferFloat createDataBuffer( final FloatArray data )
	{
		final float[] sourceArray = data.getCurrentStorageArray();
		return new DataBufferFloat( sourceArray, sourceArray.length );
	}

}
