package net.imglib2.ops.features;

import java.util.Iterator;

import net.imglib2.Pair;

/**
 * Processes one feature or a complete feature set
 * 
 * @param <T>Input for the features of this processor
 * @param <V>
 *            Output of the features of this processor
 * 
 */
public interface FeatureProcessor< T, V >
{
	/**
	 * Register a {@link FeatureSet} which will be calculated
	 * 
	 * 
	 * @param setName
	 *            Name of the corresponding {@link FeatureSet}. Can be any
	 *            string.
	 * @param feature
	 *            The {@link Feature} to be added
	 * 
	 */
	void register( Feature< V > feature, String setName );

	/**
	 * Register a {@link FeatureSet} which will be entirely calculated
	 * 
	 * @param feature
	 */
	void register( FeatureSet< T, V > featureSet );

	/**
	 * Retrieve iterator over active {@link Feature}s (which were directly
	 * added, not indirectly via dependencies). {@link Feature}s are calculated
	 * before hands so if you call .get() on a feature twice without any update,
	 * this should be cached.
	 * 
	 * @param objectOfInterest
	 *            Object on which the {@link Feature}s will be calculated
	 * 
	 * @return {@link Iterator} over resulting {@link Feature}s
	 */
	Iterator< Pair< String, Feature< V >>> iterator( T objectOfInterest );
}
