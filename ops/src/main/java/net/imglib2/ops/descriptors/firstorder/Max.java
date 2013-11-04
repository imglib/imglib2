package net.imglib2.ops.descriptors.firstorder;

import java.util.Iterator;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class Max extends AbstractFeatureModule
{
	@ModuleInput
	private Iterable< ? extends RealType< ? > > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		double max = Double.MIN_VALUE;

		final Iterator< ? extends RealType< ? >> it = ii.iterator();
		while ( it.hasNext() )
		{
			double val = it.next().getRealDouble();
			max = val > max ? val : max;
		}

		return max;
	}

	@Override
	public String name()
	{
		return "Maximum";
	}
}
