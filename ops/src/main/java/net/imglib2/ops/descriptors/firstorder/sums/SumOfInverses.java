package net.imglib2.ops.descriptors.firstorder.sums;

import java.util.Iterator;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class SumOfInverses extends AbstractFeatureModule
{
	@ModuleInput
	private Iterable< ? extends RealType< ? > > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Sum of Inverses";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		Iterator< ? extends RealType< ? > > it = ii.iterator();
		double result = 0.0;

		while ( it.hasNext() )
		{
			result += ( 1 / it.next().getRealDouble() );
		}
		return result;
	}
}
