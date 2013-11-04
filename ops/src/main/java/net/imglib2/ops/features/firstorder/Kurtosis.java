package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.firstorder.moments.Moment4AboutMean;
import net.imglib2.type.numeric.real.DoubleType;

public class Kurtosis extends AbstractFeature
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
	public Kurtosis copy()
	{
		return new Kurtosis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{
		final double std = this.stdDev.get().get();
		final double moment4 = this.moment4.get().get();

		if ( std != 0 )
		{
			return new DoubleType( ( moment4 ) / ( std * std * std * std ) );
		}
		else
		{
			// no Kurtosis in case std = 0
			return new DoubleType( 0 );
		}
	}

}
