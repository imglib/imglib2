package net.imglib2.descriptors.firstorder.moments;

import java.util.Iterator;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.Mean;
import net.imglib2.descriptors.geometric.area.Area;
import net.imglib2.type.numeric.RealType;

public class Moment4AboutMean extends AbstractFeatureModule
{

	@ModuleInput
	private Iterable< ? extends RealType< ? >> i;

	@ModuleInput
	private Mean mean;

	@ModuleInput
	private Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Moment 4 About Mean";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		final double mean = this.mean.value();
		final double area = this.area.value();
		double res = 0.0;

		final Iterator< ? extends RealType< ? >> it = i.iterator();
		while ( it.hasNext() )
		{
			final double val = it.next().getRealDouble() - mean;
			res += val * val * val * val;
		}

		return res / area;
	}
}
