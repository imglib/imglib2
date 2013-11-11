package net.imglib2.descriptors;

import net.imglib2.Sampler;

/**
 * Something like cached {@link Sampler}
 * 
 * @param <O>
 */
public interface Module< O >
{
	/**
	 * Get the output of this {@link CachedModule} which is cached if possible.
	 * 
	 * @return
	 */
	O get();

	/**
	 * If output is just another implementation of this {@link CachedModule},
	 * this method should return true;
	 * 
	 * @param output
	 * @return
	 */
	boolean isEquivalentModule( Module< ? > output );

	/**
	 * Checks whether output of this module is compatible to clazz
	 * 
	 * @param annotatedType
	 * @return
	 */
	boolean hasCompatibleOutput( Class< ? > clazz );

	/**
	 * Priority of this module. High, if this module should be preferred
	 * 
	 * @return
	 */
	double priority();
}
