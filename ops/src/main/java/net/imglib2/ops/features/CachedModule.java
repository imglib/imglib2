package net.imglib2.ops.features;

/**
 * Sampler which caches a result until marked as dirty. then the result is
 * recalculated
 * 
 * @param <T>
 */
public interface CachedModule< T > extends Module< T >
{

}
