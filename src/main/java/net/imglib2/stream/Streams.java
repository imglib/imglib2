package net.imglib2.stream;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.LocalizableSampler;
import net.imglib2.RealLocalizableSampler;

public class Streams
{
	public static < T > Stream< RealLocalizableSampler< T > > localizable( IterableRealInterval< T > interval )
	{
		return StreamSupport.stream( new RealLocalizableSamplerWrapper<>( interval.spliterator() ), false );
	}

	public static < T > Stream< LocalizableSampler< T > > localizable( IterableInterval< T > interval )
	{
		return StreamSupport.stream( new LocalizableSamplerWrapper<>( interval.spliterator() ), false );
	}

	public static < T > Stream< RealLocalizableSampler< T > > localizing( IterableRealInterval< T > interval )
	{
		return StreamSupport.stream( new RealLocalizableSamplerWrapper<>( interval.localizingSpliterator() ), false );
	}

	public static < T > Stream< LocalizableSampler< T > > localizing( IterableInterval< T > interval )
	{
		return StreamSupport.stream( new LocalizableSamplerWrapper<>( interval.localizingSpliterator() ), false );
	}
}
