package net.imglib2.ops.features.firstorder.sums;

import java.util.Iterator;

import net.imglib2.ops.features.AbstractFeatureModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class SumIterable extends AbstractFeatureModule
{
	@ModuleInput
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
	public DoubleType compute()
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
