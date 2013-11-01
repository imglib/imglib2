package net.imglib2.ops.features.testing;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.features.Feature;
import net.imglib2.ops.features.IterableIntervalFeatureProcessorBuilder;
import net.imglib2.ops.features.firstorder.FirstOrderFeatureSet;
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
import net.imglib2.ops.features.firstorder.sums.SumOfInverses;
import net.imglib2.ops.features.firstorder.sums.SumOfLogs;
import net.imglib2.ops.features.firstorder.sums.SumOfSquares;
import net.imglib2.ops.features.geometric.Circularity;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.ops.features.geometric.perimeter.Perimeter2DPolygon;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.features.ASM;
import net.imglib2.ops.features.haralick.features.ClusterPromenence;
import net.imglib2.ops.features.haralick.features.ClusterShade;
import net.imglib2.ops.features.haralick.features.Contrast;
import net.imglib2.ops.features.haralick.features.Correlation;
import net.imglib2.ops.features.haralick.features.DifferenceVariance;
import net.imglib2.ops.features.haralick.features.Entropy;
import net.imglib2.ops.features.haralick.features.ICM1;
import net.imglib2.ops.features.haralick.features.ICM2;
import net.imglib2.ops.features.haralick.features.IFDM;
import net.imglib2.ops.features.haralick.features.SumAverage;
import net.imglib2.ops.features.haralick.features.SumEntropy;
import net.imglib2.ops.features.haralick.features.SumVariance;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class FeatureFactoryTests< T extends RealType< T >>
{

	private IterableIntervalFeatureProcessorBuilder< T > builder;

	public FeatureFactoryTests()
	{
		FirstOrderFeatureSet< T > firstOrderFeatureSet = new FirstOrderFeatureSet< T >( null );
		builder = new IterableIntervalFeatureProcessorBuilder< T >();
		builder.registerFeatureSet( firstOrderFeatureSet );
		builder.build();

	}

	public void runFirstOrderTest( final IterableInterval< T > ii )
	{
		Iterator< Pair< String, Feature< DoubleType >>> iterator = builder.iterator( ii );
		while ( iterator.hasNext() )
		{
			Pair< String, Feature< DoubleType >> next = iterator.next();
			System.out.println( next.getA() + ": " + next.getB().get() );
		}
	}
}
