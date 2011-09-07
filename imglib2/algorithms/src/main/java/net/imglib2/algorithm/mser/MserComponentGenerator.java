package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.IntegerType;

public class MserComponentGenerator< T extends IntegerType< T > > implements ComponentGenerator< T, MserComponent< T > >
{
	final T maxValue;
	
	public MserComponentGenerator( final T maxValue )
	{
		this.maxValue = maxValue;
	}

	@Override
	public MserComponent< T > createComponent( T value )
	{
		return new MserComponent< T >( value );
	}

	@Override
	public MserComponent< T > createMaxComponent()
	{
		return new MserComponent< T >( maxValue );
	}
}
