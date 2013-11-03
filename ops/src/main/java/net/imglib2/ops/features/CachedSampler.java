package net.imglib2.ops.features;

import net.imglib2.Sampler;

/**
 * Sampler which caches a result until marked as dirty. then the result is
 * recalculated
 * 
 * @param <T>
 */
public interface CachedSampler< T > extends Sampler< T >
{

	/**
	 * check whether the given class is compatible with the samplers result
	 * 
	 * @param isCompatible
	 * @return
	 */
	boolean isCompatible( Class< ? > isCompatible );

	/**
	 * Mark the sampler as dirty, i.e. force recalculation
	 */
	void markDirty();
}
