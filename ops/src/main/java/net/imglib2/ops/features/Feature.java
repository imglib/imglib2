package net.imglib2.ops.features;

import net.imglib2.Sampler;

// Feature is simply a Sampler with some additional method
public interface Feature< A > extends Sampler< A >
{
	/**
	 * Name of the feature
	 * 
	 * @return
	 */
	String name();

	/**
	 * Create a copy of the {@link Feature}.
	 */
	Feature< A > copy();

	/**
	 * Notify the {@link Feature} that it should be updated
	 */
	void update();

	/**
	 * HashCode implementation
	 * 
	 * @return
	 */
	int hashCode();
}
