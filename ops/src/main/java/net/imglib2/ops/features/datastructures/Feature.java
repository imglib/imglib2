package net.imglib2.ops.features.datastructures;

import net.imglib2.type.numeric.real.DoubleType;

// Feature is simply a Sampler with some additional method
public interface Feature extends CachedSampler< DoubleType >
{

	/**
	 * Name of the feature
	 * 
	 * @return
	 */
	String name();

	/**
	 * HashCode implementation
	 * 
	 * @return
	 */
	int hashCode();
}
