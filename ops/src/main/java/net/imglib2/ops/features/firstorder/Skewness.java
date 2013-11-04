package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.firstorder.moments.Moment3AboutMean;
import net.imglib2.type.numeric.real.DoubleType;

public class Skewness extends AbstractFeature
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
	public Skewness copy()
	{
		return new Skewness();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleType compute()
	{

		final double moment3 = this.moment3.get().get();
		final double std = this.stdDev.get().get();

		if ( std != 0 )
		{
			return new DoubleType( ( moment3 ) / ( std * std * std ) );
		}
		else
		{
			// no skewness in case of std = 0
			return new DoubleType( 0 );
		}
	}

}
