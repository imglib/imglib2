package net.imglib2.ops.features.datastructures;

import net.imglib2.Sampler;

public interface CachedSampler< T > extends Sampler< T >
{
	boolean isCompatible( Class< ? > isCompatible );

	void markDirty();
}
