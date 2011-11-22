package net.imglib2.algorithm.mser;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.LongType;

public class PixelListComponentGenerator< T extends Type< T > > implements Component.Generator< T, PixelListComponentIntermediate< T > >
{
	final T maxValue;
	
	final long[] dimensions;
	
	final Img< LongType > linkedList;

	public PixelListComponentGenerator( final T maxValue, final RandomAccessibleInterval< T > input, final ImgFactory< LongType > imgFactory )
	{
		this.maxValue = maxValue;
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );
		linkedList = imgFactory.create( dimensions, new LongType() );
	}
	
	@Override
	public PixelListComponentIntermediate< T > createComponent( T value )
	{
		return new PixelListComponentIntermediate< T >( value, this );
	}

	@Override
	public PixelListComponentIntermediate< T > createMaxComponent()
	{
		return new PixelListComponentIntermediate< T >( maxValue, this );
	}
}
