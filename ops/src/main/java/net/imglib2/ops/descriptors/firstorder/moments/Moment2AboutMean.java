package net.imglib2.ops.descriptors.firstorder.moments;

import java.util.Iterator;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.Mean;
import net.imglib2.ops.descriptors.geometric.area.Area;
import net.imglib2.type.numeric.RealType;

public class Moment2AboutMean extends AbstractFeatureModule
{

	@ModuleInput
	Iterable< ? extends RealType< ? > > iterable;

	@ModuleInput
	Mean mean;

	@ModuleInput
	Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Moment 2 About Mean";
	}

	@Override
	protected double calculateFeature()
	{

		final double mean = this.mean.value();
		final double area = this.area.value();

		double res = 0.0;

		final Iterator< ? extends RealType< ? > > it = iterable.iterator();
		while ( it.hasNext() )
		{
			final double val = it.next().getRealDouble() - mean;
			res += val * val;
		}

		return res / area;
	}
}
