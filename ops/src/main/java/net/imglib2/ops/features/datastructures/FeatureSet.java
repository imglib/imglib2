package net.imglib2.ops.features.datastructures;

import java.util.List;

public interface FeatureSet< T, V >
{

	/**
	 * @return the number of {@link Feature}s
	 */
	int numFeatures();

	/**
	 * Returns a unique identifier of this {@link FeatureSet}. If the
	 * {@link FeatureSet} is initialized with parameters, it should be included
	 * in the returned id.
	 * 
	 * @return a unique id
	 */
	String name();

	/**
	 * List of all {@link Feature}s
	 */
	List< Feature > features();

	// /**
	// * List of {@link Source}s
	// */
	// List< Source< ? > > sources();

	/**
	 * List of all non-public, but required {@link Feature}s
	 */
	List< CachedSampler< ? > > required();
}
