package net.imglib2.descriptors.firstorder;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.moments.Moment3AboutMean;

public class Skewness extends AbstractFeatureModule
{

	@ModuleInput
	private Moment3AboutMean moment3;

	@ModuleInput
	private StdDeviation stdDev;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Skewness Feature";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{

		final double moment3 = this.moment3.value();
		final double std = this.stdDev.value();

		if ( std != 0 )
		{
			return ( moment3 ) / ( std * std * std );
		}
		else
			// no skewness in case of std = 0
			return 0.0;
	}
}
