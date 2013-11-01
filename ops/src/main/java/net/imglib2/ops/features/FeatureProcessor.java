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
	 * @param feature
	 * 
	 */
	void register( Feature< V > feature, String setName );

	/**
	 * @param feature
	 * 
	 */
	void register( FeatureSet< T, V > feature );

	/**
	 * Retrieve iterator over active features (which were directly added, not
	 * indirectly via dependencies). Features are calculated before hands so if
	 * you call .get() on a feature twice without any update, this should be
	 * cached.
	 * 
	 * @param objectOfInterest
	 * @return Result Cursor
	 */
	Iterator< Pair< String, Feature< V >>> iterator( T objectOfInterest );
}
