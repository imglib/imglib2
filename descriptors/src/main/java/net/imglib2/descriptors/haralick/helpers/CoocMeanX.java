package net.imglib2.descriptors.haralick.helpers;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;

public class CoocMeanX extends AbstractFeatureModule
{
	@ModuleInput
	private CoocPX coocPX;

	/**
	 * /** {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		double res = 0;
		double[] px = coocPX.get();
		for ( int i = 0; i < px.length; i++ )
		{
			res += i * px[ i ];
		}
		return res;
	}

	@Override
	public String name()
	{
		return "CoocMeanX";
	}

}
