package net.imglib2.ops.features.firstorder.moments;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.firstorder.Mean;
import net.imglib2.ops.features.geometric.Area;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Moment3AboutMean< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private GetIterableInterval< T > ii;

	@RequiredFeature
	private Mean< T > mean;

	@RequiredFeature
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
	public Moment3AboutMean< T > copy()
	{
		return new Moment3AboutMean< T >();
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

		Iterator< T > it = ii.get().iterator();
		while ( it.hasNext() )
		{
			final double val = it.next().getRealDouble() - mean;
			res += val * val * val;
		}

		return new DoubleType( res / area );
	}
}
