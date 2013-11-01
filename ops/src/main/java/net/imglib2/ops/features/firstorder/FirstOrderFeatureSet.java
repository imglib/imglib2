package net.imglib2.ops.features.firstorder;

import java.util.Set;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.AbstractFeatureSet;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.firstorder.moments.Moment1AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment2AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment3AboutMean;
import net.imglib2.ops.features.firstorder.moments.Moment4AboutMean;
import net.imglib2.ops.features.firstorder.sums.Sum;
import net.imglib2.ops.features.firstorder.sums.SumOfInverses;
import net.imglib2.ops.features.firstorder.sums.SumOfLogs;
import net.imglib2.ops.features.firstorder.sums.SumOfSquares;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.perimeter.Perimeter2DPolygon;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class FirstOrderFeatureSet< T extends RealType< T >> extends AbstractFeatureSet< IterableInterval< T >, DoubleType >
{
	public FirstOrderFeatureSet( Set< String > active )
	{
		super( new GetIterableInterval< T >(), active );

		register( new Mean< T >() );
		register( new Variance< T >() );
		register( new StdDeviation< T >() );
		register( new Skewness< T >() );
		register( new Kurtosis< T >() );
		register( new Sum< T >() );
		register( new SumOfInverses< T >() );
		register( new SumOfLogs< T >() );
		register( new SumOfSquares< T >() );
		register( new Moment1AboutMean< T >() );
		register( new Moment2AboutMean< T >() );
		register( new Moment3AboutMean< T >() );
		register( new Moment4AboutMean< T >() );
		register( new GeometricMean< T >() );
		register( new HarmonicMean< T >() );
		register( new Max< T >() );
		register( new Min< T >() );
		register( new Perimeter2DPolygon() );
		register( new Circularity< T >() );
	}

	@Override
	public String name()
	{
		return "First Order Statistics";
	}

}
