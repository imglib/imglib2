package net.imglib2.ops.features.sets;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractFeatureSet;
import net.imglib2.ops.features.firstorder.GeometricMean;
import net.imglib2.ops.features.firstorder.HarmonicMean;
import net.imglib2.ops.features.firstorder.Kurtosis;
import net.imglib2.ops.features.firstorder.Max;
import net.imglib2.ops.features.firstorder.Mean;
import net.imglib2.ops.features.firstorder.Min;
import net.imglib2.ops.features.firstorder.Skewness;
import net.imglib2.ops.features.firstorder.StdDeviation;
import net.imglib2.ops.features.firstorder.Variance;
import net.imglib2.ops.features.firstorder.moments.Moment1AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment2AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment3AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment4AboutMean;
import net.imglib2.ops.features.firstorder.sums.Sum;
import net.imglib2.ops.features.firstorder.sums.SumOfInverses;
import net.imglib2.ops.features.firstorder.sums.SumOfLogs;
import net.imglib2.ops.features.firstorder.sums.SumOfSquares;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class FirstOrderFeatureSet< I extends IterableInterval< ? extends RealType< ? >>> extends AbstractFeatureSet< I, DoubleType >
{
	public FirstOrderFeatureSet()
	{
		super();

		// Register required
		registerRequired( new AreaIterableInterval() );

		// Feature registered
		registerFeature( new Sum() );
		registerFeature( new Mean() );
		registerFeature( new SumOfInverses() );
		registerFeature( new SumOfLogs() );
		registerFeature( new SumOfSquares() );
		registerFeature( new Moment1AboutMean() );
		registerFeature( new Moment2AboutMean() );
		registerFeature( new Sum() );
		registerFeature( new Variance() );
		registerFeature( new StdDeviation() );
		registerFeature( new Skewness() );
		registerFeature( new Kurtosis() );
		registerFeature( new Moment3AboutMean() );
		registerFeature( new Moment4AboutMean() );
		registerFeature( new GeometricMean() );
		registerFeature( new HarmonicMean() );
		registerFeature( new Max() );
		registerFeature( new Min() );
	}

	@Override
	public String name()
	{
		return "First Order Statistics";
	}
}
