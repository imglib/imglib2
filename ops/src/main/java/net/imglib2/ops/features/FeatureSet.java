package net.imglib2.ops.features;

import java.util.List;

import net.imglib2.Sampler;

/**
 * Unoptimized set of {@link Feature}s which contains _all_ {@link Feature}s and
 * {@link Sampler}s which are required for the calculation
 * 
 * @param <T>
 * @param <V>
 */
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
	 * List of all non-public, but required {@link CachedSampler}s
	 */
	List< CachedSampler< ? > > required();
}
