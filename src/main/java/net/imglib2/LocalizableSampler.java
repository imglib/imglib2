package net.imglib2;

public interface LocalizableSampler< T > extends Localizable, RealLocalizableSampler< T >
{
	@Override
	LocalizableSampler< T > copy();
}
