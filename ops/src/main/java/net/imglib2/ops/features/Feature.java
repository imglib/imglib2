package net.imglib2.ops.features;

import net.imglib2.Sampler;

// Feature is simply a Sampler with some additional method
public interface Feature< A > extends Sampler< A >
{

	// Name of the feature
	String name();

	// Create a copy of the feature (maybe intersting later for multi-threading
	// purposes)
	Feature< A > copy();

	// tell the feature that the source was updated
	void update();

	// hash code
	int hashCode();
}
