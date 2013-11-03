package net.imglib2.ops.features;

import net.imglib2.type.numeric.real.DoubleType;

/**
 * Giving the {@link Feature} a name
 */
public interface Feature extends CachedSampler< DoubleType >
{

	/**
	 * Name of the feature
	 * 
	 * @return
	 */
	String name();
}
