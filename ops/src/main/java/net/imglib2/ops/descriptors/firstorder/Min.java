package net.imglib2.ops.descriptors.firstorder;

import java.util.Iterator;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class Min extends AbstractFeatureModule
{

	@ModuleInput
	private Iterable< ? extends RealType< ? >> ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Minimum";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		System.out.println( "12355" );
		double min = Double.MAX_VALUE;

		final Iterator< ? extends RealType< ? > > it = ii.iterator();
		while ( it.hasNext() )
		{
			double val = it.next().getRealDouble();
			min = val < min ? val : min;
		}

		return min;
	}
}
