package net.imglib2.ops.features;

import net.imglib2.Sampler;

public interface Feature< A > extends Sampler< A >
{

	String name();

	Feature< A > copy();

	void update();

	int hashCode();
}
