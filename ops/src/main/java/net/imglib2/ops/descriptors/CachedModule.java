package net.imglib2.ops.descriptors;

import net.imglib2.Sampler;

/**
 * Something like cached {@link Sampler}
 * 
 * @param <O>
 */
public interface CachedModule< O > extends Module< O >
{

	/**
	 * Mark {@link CachedModule} to be recomputed
	 */
	void markDirty();

	/**
	 * Is {@link CachedModule} dirty?
	 * 
	 * @return
	 */
	boolean isDirty();
}
