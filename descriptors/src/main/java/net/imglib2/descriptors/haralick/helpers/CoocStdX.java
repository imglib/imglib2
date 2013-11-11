package net.imglib2.descriptors.haralick.helpers;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;

public class CoocStdX extends AbstractFeatureModule
{

	@ModuleInput
	private CoocPX coocPX = new CoocPX();

	@ModuleInput
	private CoocMeanX coocMeanX = new CoocMeanX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		double res = 0;

		double meanx = coocMeanX.value();
		double[] px = coocPX.get();

		for ( int i = 0; i < px.length; i++ )
		{
			res += ( i - meanx ) * ( i - meanx ) * px[ i ];
		}

		res = Math.sqrt( res );

		return res;
	}

	@Override
	public String name()
	{
		return "CoocStdX";
	}

}
