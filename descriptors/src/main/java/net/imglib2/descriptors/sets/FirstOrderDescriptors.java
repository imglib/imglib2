package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.firstorder.Max;
import net.imglib2.descriptors.firstorder.Mean;
import net.imglib2.descriptors.firstorder.Min;
import net.imglib2.descriptors.firstorder.percentile.Percentile25;
import net.imglib2.descriptors.firstorder.percentile.Percentile50;
import net.imglib2.descriptors.firstorder.percentile.Percentile75;
import net.imglib2.descriptors.firstorder.percentile.PercentileGeneric;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;

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
		registerFeature( Percentile25.class );
		registerFeature( Percentile50.class );
		registerFeature( Percentile75.class );
		registerFeature( PercentileGeneric.class );

		registerFeature( Min.class );
		registerFeature( Max.class );

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
