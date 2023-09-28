package net.imglib2.stream;

import net.imglib2.Localizable;

public interface LocalizableSpliterator< T > extends RealLocalizableSpliterator< T >, Localizable
{
	@Override
	LocalizableSpliterator< T > trySplit();

	@Override
	LocalizableSpliterator< T > copy();
}
