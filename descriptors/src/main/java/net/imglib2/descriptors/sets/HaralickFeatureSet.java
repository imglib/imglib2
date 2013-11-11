package net.imglib2.descriptors.sets;

import net.imglib2.descriptors.AbstractDescriptorSet;
import net.imglib2.descriptors.haralick.features.ASM;
import net.imglib2.descriptors.haralick.features.ClusterPromenence;
import net.imglib2.descriptors.haralick.features.ClusterShade;
import net.imglib2.descriptors.haralick.features.Contrast;
import net.imglib2.descriptors.haralick.features.Correlation;
import net.imglib2.descriptors.haralick.features.DifferenceEntropy;
import net.imglib2.descriptors.haralick.features.DifferenceVariance;
import net.imglib2.descriptors.haralick.features.Entropy;
import net.imglib2.descriptors.haralick.features.ICM1;
import net.imglib2.descriptors.haralick.features.ICM2;
import net.imglib2.descriptors.haralick.features.IFDM;
import net.imglib2.descriptors.haralick.features.SumAverage;
import net.imglib2.descriptors.haralick.features.SumVariance;
import net.imglib2.descriptors.haralick.features.Variance;

public class HaralickFeatureSet extends AbstractDescriptorSet
{
	public HaralickFeatureSet()
	{
		// Feature registered
		registerFeature( ASM.class );
		registerFeature( ClusterPromenence.class );
		registerFeature( ClusterShade.class );
		registerFeature( Contrast.class );
		registerFeature( Correlation.class );
		registerFeature( DifferenceEntropy.class );
		registerFeature( DifferenceVariance.class );
		registerFeature( Entropy.class );
		registerFeature( ICM1.class );
		registerFeature( ICM2.class );
		registerFeature( IFDM.class );
		registerFeature( SumAverage.class );
		registerFeature( SumVariance.class );
		registerFeature( Variance.class );
	}

	@Override
	public String name()
	{
		return "Haralick Features";
	}
}
