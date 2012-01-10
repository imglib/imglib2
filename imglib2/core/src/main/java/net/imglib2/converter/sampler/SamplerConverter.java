package net.imglib2.converter.sampler;

import net.imglib2.Sampler;

public interface SamplerConverter< A, B >
{
	public B convert( Sampler< A > sampler );
}
