package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.firstorder.moments.Moment3AboutMean;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Skewness< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private Moment3AboutMean< T > moment3;

	@RequiredFeature
	private StdDeviation< T > stdDev;

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
	public Skewness< T > copy()
	{
		return new Skewness< T >();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleType recompute()
	{

		double moment3 = this.moment3.get().get();
		double std = this.stdDev.get().get();

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
