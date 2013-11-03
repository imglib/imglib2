package net.imglib2.ops.features.firstorder.moments;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.ops.features.firstorder.Mean;
import net.imglib2.ops.features.geometric.area.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Moment3AboutMean extends AbstractFeature
{

	@RequiredInput
	private Iterable< ? extends RealType< ? > > ii;

	@RequiredInput
	private Mean mean;

	@RequiredInput
	private Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Moment 3 About Mean";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Moment3AboutMean copy()
	{
		return new Moment3AboutMean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		final double mean = this.mean.get().get();
		final double area = this.area.get().get();
		double res = 0.0;

		Iterator< ? extends RealType< ? > > it = ii.iterator();
		while ( it.hasNext() )
		{
			final double val = it.next().getRealDouble() - mean;
			res += val * val * val;
		}

		return new DoubleType( res / area );
	}
}
