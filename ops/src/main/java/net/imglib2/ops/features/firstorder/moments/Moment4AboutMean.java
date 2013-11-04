package net.imglib2.ops.features.firstorder.moments;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.firstorder.Mean;
import net.imglib2.ops.features.geometric.area.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Moment4AboutMean extends AbstractFeature
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
	public Moment4AboutMean copy()
	{
		return new Moment4AboutMean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		final double mean = this.mean.get().get();
		final double area = this.area.get().get();
		double res = 0.0;

		Iterator< ? extends RealType< ? >> it = i.iterator();
		while ( it.hasNext() )
		{
			final double val = it.next().getRealDouble() - mean;
			res += val * val * val * val;
		}

		return new DoubleType( res / area );
	}
}
