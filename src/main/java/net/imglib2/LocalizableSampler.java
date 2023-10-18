package net.imglib2;

/**
 * Combination of {@code Localizable} and {@code Sampler} interfaces. In
 * general, it is preferable to use {@code Localizable & Sampler<T>} where
 * possible.
 *
 * @param <T> pixel type
 */
public interface LocalizableSampler< T > extends Localizable, RealLocalizableSampler< T >
{
	@Override
	LocalizableSampler< T > copy();
}
