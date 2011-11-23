package net.imglib2.algorithm.mser;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.LongType;

final class MserComponentGenerator< T extends Type< T > > implements Component.Generator< T, MserComponentIntermediate< T > >
{
	final T maxValue;

	final long[] dimensions;

	final Img< LongType > linkedList;

	public MserComponentGenerator( final T maxValue, final RandomAccessibleInterval< T > input, final ImgFactory< LongType > imgFactory )
	{
		this.maxValue = maxValue;
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );
		linkedList = imgFactory.create( dimensions, new LongType() );
	}

	@Override
	public MserComponentIntermediate< T > createComponent( T value )
	{
		return new MserComponentIntermediate< T >( value, this );
	}

	@Override
	public MserComponentIntermediate< T > createMaxComponent()
	{
		return new MserComponentIntermediate< T >( maxValue, this );
	}
}
