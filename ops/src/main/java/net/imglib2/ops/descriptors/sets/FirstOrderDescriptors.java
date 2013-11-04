package net.imglib2.ops.descriptors.sets;

import net.imglib2.ops.descriptors.AbstractDescriptorSet;
import net.imglib2.ops.descriptors.firstorder.Mean;
import net.imglib2.ops.descriptors.geometric.centerofgravity.CenterOfGravity;

/**
 * Needs IterableInterval as source
 */
public class FirstOrderDescriptors extends AbstractDescriptorSet
{
	public FirstOrderDescriptors()
	{
		super();

		registerFeature( Mean.class );
		registerFeature( CenterOfGravity.class );

		// registerFeature( new SumOfLogs() );
		// registerFeature( new SumOfSquares() );
		// registerFeature( new Moment1AboutMean() );
		// registerFeature( new Moment2AboutMean() );
		// registerFeature( new SumIterable() );
		// registerFeature( new Variance() );
		// registerFeature( new StdDeviation() );
		// registerFeature( new Skewness() );
		// registerFeature( new Kurtosis() );
		// registerFeature( new Moment3AboutMean() );
		// registerFeature( new Moment4AboutMean() );
		// registerFeature( new GeometricMean() );
		// registerFeature( new HarmonicMean() );
		// registerFeature( new Max() );
		// registerFeature( new Min() );
	}

	@Override
	public String name()
	{
		return "First Order Statistics";
	}

}
