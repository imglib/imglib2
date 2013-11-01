package net.imglib2.ops.features;

/**
 * Processes one feature or a complete feature set
 * 
 * @param <T>Input for the features of this processor
 * @param <V>
 *            Output of the features of this processor
 * 
 */
public interface FeatureProcessorBuilder< T, V >
{

	/**
	 * Register a {@link FeatureSet} which will be entirely calculated
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

	/**
	 * Checks whether a given {@link Feature} is available
	 * 
	 * @param clazz
	 * @return
	 */
	Feature< ? > getFeature( Class< ? extends Feature > clazz );
}
