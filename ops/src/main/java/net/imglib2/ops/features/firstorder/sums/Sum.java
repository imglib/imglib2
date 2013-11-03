package net.imglib2.ops.features.firstorder.sums;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Sum extends AbstractFeature
{
	@RequiredInput
	private Iterable< ? extends RealType< ? >> ii;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Sum";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sum copy()
	{
		return new Sum();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleType recompute()
	{
		double result = 0.0f;

		Iterator< ? extends RealType< ? > > it = ii.iterator();
		while ( it.hasNext() )
		{
			result += it.next().getRealDouble();
		}

		return new DoubleType( result );
	}
}
