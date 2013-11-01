package net.imglib2.ops.features;

import java.util.Iterator;

import net.imglib2.Pair;

public interface FeatureSetProcessor< T, V >
{

	/**
	 * Retrieve {@link Iterator} over active {@link Feature}s (which were
	 * directly added, not indirectly via dependencies). {@link Feature}s are
	 * calculated before hands so if you call .get() on a feature twice without
	 * any update, this should be cached.
	 * 
	 * @param objectOfInterest
	 *            Object on which the {@link Feature}s will be calculated
	 * 
	 * @return {@link Iterator} over resulting {@link Feature}s
	 */
	Iterator< Pair< String, Feature< V >>> iterator( T objectOfInterest );
}
