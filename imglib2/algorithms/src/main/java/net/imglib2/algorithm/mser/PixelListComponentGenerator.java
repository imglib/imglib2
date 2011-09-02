package net.imglib2.algorithm.mser;

import net.imglib2.type.Type;

public class PixelListComponentGenerator< T extends Type< T > > implements ComponentGenerator< T, PixelListComponent< T > >
{
	final T maxValue;
	
	public PixelListComponentGenerator( final T maxValue )
	{
		this.maxValue = maxValue;
	}

	@Override
	public PixelListComponent< T > createComponent( T value )
	{
		return new PixelListComponent< T >( value );
	}

	@Override
	public PixelListComponent< T > createMaxComponent()
	{
		return new PixelListComponent< T >( maxValue );
	}
}
