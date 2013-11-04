package net.imglib2.ops.features;

import java.util.List;

import net.imglib2.Sampler;

/**
 * Unoptimized set of {@link NumericFeature}s which contains _all_
 * {@link NumericFeature}s and {@link Sampler}s which are required for the
 * calculation
 * 
 * @param <T>
 * @param <V>
 */
public interface DescriptorSet
{
	/**
	 * @return the number of {@link NumericFeature}s
	 */
	int numFeatures();

	/**
	 * Returns a unique identifier of this {@link DescriptorSet}. If the
	 * {@link DescriptorSet} is initialized with parameters, it should be
	 * included in the returned id.
	 * 
	 * @return a unique id
	 */
	String name();

	/**
	 * All {@link NumericFeature}s
	 * 
	 * @return
	 */
	List< Class< ? extends Descriptor > > descriptors();
}
