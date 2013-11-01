package net.imglib2.ops.features.firstorder;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Max< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private GetIterableInterval< T > ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Maximum";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Max< T > copy()
	{
		return new Max< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		double max = Double.MIN_VALUE;

		Iterator< T > it = ii.get().iterator();
		while ( it.hasNext() )
		{
			double val = it.next().getRealDouble();
			max = val > max ? val : max;
		}

		return new DoubleType( max );
	}
}
