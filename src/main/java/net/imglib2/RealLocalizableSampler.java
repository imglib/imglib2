package net.imglib2;

public interface RealLocalizableSampler< T > extends RealLocalizable, Sampler< T >
{
	@Override
	RealLocalizableSampler< T > copy();
}
