package net.imglib2.ops.descriptors.firstorder;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.moments.Moment4AboutMean;

public class Kurtosis extends AbstractFeatureModule
{

	@ModuleInput
	StdDeviation stdDev;

	@ModuleInput
	Moment4AboutMean moment4;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Kurtosis Feature";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		final double std = this.stdDev.value();
		final double moment4 = this.moment4.value();

		if ( std != 0 )
		{
			return ( moment4 ) / ( std * std * std * std );
		}
		else
		{
			// no Kurtosis in case std = 0
			return 0.0;
		}
	}

}
