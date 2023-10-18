package net.imglib2;

/**
 * Combination of {@code RealLocalizable} and {@code Sampler} interfaces. In
 * general, it is preferable to use {@code RealLocalizable & Sampler<T>} where
 * possible.
 *
 * @param <T> pixel type
 */
public interface RealLocalizableSampler< T > extends RealLocalizable, Sampler< T >
{
	@Override
	RealLocalizableSampler< T > copy();
}
