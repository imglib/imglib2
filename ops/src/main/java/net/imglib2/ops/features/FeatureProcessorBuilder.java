package net.imglib2.ops.features;

/**
 * Optimizes the calculation of many combined featureSets
 * 
 * @param <T>Input for the features of this processor
 * @param <V>
 *            Output of the features of this processor
 * 
 */
public interface FeatureProcessorBuilder< T, V >
{

	/**
	 * Register a {@link FeatureSet}
	 * 
	 * @param feature
	 */
	void registerFeatureSet( FeatureSet< T, V > featureSet );

	/**
	 * Get the optimized processor
	 * 
	 * @return
	 */
	FeatureSetProcessor< T, V > build();
}
