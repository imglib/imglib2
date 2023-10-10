package net.imglib2.stream;

import java.util.Spliterator;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

public interface RealLocalizableSpliterator< T > extends Spliterator< T >, RealLocalizable, Sampler< T >
{
	@Override
	RealLocalizableSpliterator< T > trySplit();

	@Override
	RealLocalizableSpliterator< T > copy();
}
