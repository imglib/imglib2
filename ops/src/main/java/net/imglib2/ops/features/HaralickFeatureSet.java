package net.imglib2.ops.features;

import net.imglib2.IterableInterval;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.features.datastructures.AbstractFeatureSet;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.features.ASM;
import net.imglib2.ops.features.haralick.features.ClusterPromenence;
import net.imglib2.ops.features.haralick.features.ClusterShade;
import net.imglib2.ops.features.haralick.features.Contrast;
import net.imglib2.ops.features.haralick.features.Correlation;
import net.imglib2.ops.features.haralick.features.DifferenceEntropy;
import net.imglib2.ops.features.haralick.features.DifferenceVariance;
import net.imglib2.ops.features.haralick.features.Entropy;
import net.imglib2.ops.features.haralick.features.ICM1;
import net.imglib2.ops.features.haralick.features.ICM2;
import net.imglib2.ops.features.haralick.features.IFDM;
import net.imglib2.ops.features.haralick.features.SumAverage;
import net.imglib2.ops.features.haralick.features.SumVariance;
import net.imglib2.ops.features.haralick.features.Variance;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class HaralickFeatureSet< I extends IterableInterval< ? extends RealType< ? >>> extends AbstractFeatureSet< I, DoubleType >
{
	public HaralickFeatureSet( int nrGrayLevels, int distance, MatrixOrientation orientation )
	{
		super();

		// Register required
		registerRequired( new HaralickCoocMatrix( nrGrayLevels, distance, orientation ) );

		// Feature registered
		registerFeature( new ASM() );
		registerFeature( new ClusterPromenence() );
		registerFeature( new ClusterShade() );
		registerFeature( new Contrast() );
		registerFeature( new Correlation() );
		registerFeature( new DifferenceEntropy() );
		registerFeature( new DifferenceVariance() );
		registerFeature( new Entropy() );
		registerFeature( new ICM1() );
		registerFeature( new ICM2() );
		registerFeature( new IFDM() );
		registerFeature( new SumAverage() );
		registerFeature( new SumVariance() );
		registerFeature( new Variance() );
	}

	@Override
	public String name()
	{
		return "Haralick Features";
	}
}
