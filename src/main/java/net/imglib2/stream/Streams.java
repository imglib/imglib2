package net.imglib2.stream;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.LocalizableSampler;
import net.imglib2.RealLocalizableSampler;

/**
 * Utilities for creating "localizable Streams".
 */
public class Streams
{
	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element locations are computed on demand only, which is suited for usages
	 * where location is only needed for a few elements.
	 * (Also see {@link #localizing(IterableRealInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @param <T> pixel type
	 *
	 * @return a {@code Stream<RealLocalizableSampler<T>>} over the elements of
	 *  	the given interval.
	 */
	public static < T > Stream< RealLocalizableSampler< T > > localizable( IterableRealInterval< T > interval )
	{
		return StreamSupport.stream( new RealLocalizableSamplerWrapper<>( interval.spliterator() ), false );
	}

	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element location is tracked preemptively on every step, which is suited for usages
	 * where location is needed for all elements.
	 * (Also see {@link #localizable(IterableRealInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @param <T> pixel type
	 *
	 * @return a {@code Stream<RealLocalizableSampler<T>>} over the elements of
	 *  	the given interval.
	 */
	public static < T > Stream< RealLocalizableSampler< T > > localizing( IterableRealInterval< T > interval )
	{
		return StreamSupport.stream( new RealLocalizableSamplerWrapper<>( interval.localizingSpliterator() ), false );
	}

	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element locations are computed on demand only, which is suited for usages
	 * where location is only needed for a few elements.
	 * (Also see {@link #localizing(IterableInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @return a {@code Stream<LocalizableSampler<T>>} over the elements of the
	 *  	given interval.
	 *
	 * @param <T> pixel type
	 */
	public static < T > Stream< LocalizableSampler< T > > localizable( IterableInterval< T > interval )
	{
		return StreamSupport.stream( new LocalizableSamplerWrapper<>( interval.spliterator() ), false );
	}

	/**
	 * Create a sequential {@code Stream} of the elements (value and location)
	 * of the given {@code interval}.
	 * <p>
	 * Element location is tracked preemptively on every step, which is suited for usages
	 * where location is needed for all elements.
	 * (Also see {@link #localizable(IterableInterval)}).
	 *
	 * @param interval
	 * 		interval over which to provide a {@code Stream}.
	 * @return a {@code Stream<LocalizableSampler<T>>} over the elements of the
	 *  	given interval.
	 *
	 * @param <T> pixel type
	 */
	public static < T > Stream< LocalizableSampler< T > > localizing( IterableInterval< T > interval )
	{
		return StreamSupport.stream( new LocalizableSamplerWrapper<>( interval.localizingSpliterator() ), false );
	}



	public static < T > Stream< RealLocalizableSampler< T > > localizing_( IterableRealInterval< T > interval )
	{
		return new SamplerStream<>( localizing_( interval ) );
	}
	public static < T > Stream< RealLocalizableSampler< T > > localizable_( IterableRealInterval< T > interval )
	{
		return new SamplerStream<>( localizable( interval ) );
	}
	public static < T > Stream< LocalizableSampler< T > > localizable_( IterableInterval< T > interval )
	{
		return new SamplerStream<>( localizable( interval ) );
	}
	public static < T > Stream< LocalizableSampler< T > > localizing_( IterableInterval< T > interval )
	{
		return new SamplerStream<>( localizing( interval ) );
	}

}
