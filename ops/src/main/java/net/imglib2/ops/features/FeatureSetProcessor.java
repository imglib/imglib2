package net.imglib2.ops.features;

import java.util.Iterator;

import net.imglib2.Pair;

public interface FeatureSetProcessor< T, V >
{

	/**
	 * Retrieve {@link Iterator} over active {@link Feature}s (which were
	 * directly added, not indirectly via dependencies or as required
	 * {@link CachedSampler}s).
	 * 
	 * @param objectOfInterest
	 *            Object on which the {@link Feature}s will be calculated
	 * 
	 * @return {@link Iterator} over resulting {@link Feature}s
	 */
	Iterator< Pair< String, Feature >> iterator( T objectOfInterest );
}
