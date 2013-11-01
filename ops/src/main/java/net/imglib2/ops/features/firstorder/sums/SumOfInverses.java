package net.imglib2.ops.features.firstorder.sums;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.sources.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class SumOfInverses< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private GetIterableInterval< T > ii;

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
	public SumOfInverses< T > copy()
	{
		return new SumOfInverses< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		Iterator< T > it = ii.get().iterator();
		double result = 0.0;

		while ( it.hasNext() )
		{
			result += ( 1 / it.next().getRealDouble() );
		}
		return new DoubleType( result );
	}

}
