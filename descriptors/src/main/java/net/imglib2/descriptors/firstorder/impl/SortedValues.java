package net.imglib2.descriptors.firstorder.impl;

import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractModule;
import net.imglib2.descriptors.Module;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class SortedValues extends AbstractModule< double[] >
{
	@ModuleInput
	private IterableInterval< ? extends RealType< ? >> ii;

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return SortedValues.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public boolean hasCompatibleOutput( Class< ? > clazz )
	{
		return clazz.isAssignableFrom( double[].class );
	}

	@Override
	protected double[] recompute()
	{
		double[] values = new double[ ( int ) ii.size() ];

		final Iterator< ? extends RealType< ? >> it = ii.iterator();
		int i = 0;
		while ( it.hasNext() )
		{
			values[ i ] = it.next().getRealDouble();
			i++;
		}

		Arrays.sort( values );
		return values;
	}
}
