package net.imglib2.ops.features.firstorder;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Min extends AbstractFeature
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
	public Min copy()
	{
		return new Min();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		double min = Double.MAX_VALUE;

		final Iterator< ? extends RealType< ? > > it = ii.iterator();
		while ( it.hasNext() )
		{
			double val = it.next().getRealDouble();
			min = val < min ? val : min;
		}

		return new DoubleType( min );
	}
}
