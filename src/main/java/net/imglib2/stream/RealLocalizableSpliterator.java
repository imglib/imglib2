package net.imglib2.stream;

import java.util.Spliterator;
import net.imglib2.IterableRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * A {@code Spliterator<T>} which is Localizable similar to a Cursor.
 * <p>
 * The location of the Spliterator reflects the location of the element passed
 * to the {@code Consumer} in {@link #tryAdvance} or {@link #forEachRemaining}
 * (at the time the element is passed, and until the next element is passed).
 * <p>
 * Similar to {@code RealCursor}, {@code RealLocalizableSpliterator} usually
 * comes in two flavors:
 * <ul>
 *     <li>{@link IterableRealInterval#spliterator()} computes location only on demand, and</li>
 *     <li>{@link IterableRealInterval#localizingSpliterator()} preemptively tracks location on every step.</li>
 * </ul>
 * (Which one is more efficient depends on how often location is actually needed.)
 * <p>
 * To make the {@code RealLocalizable} property available in a {@code Stream},
 * use the {@link Streams} utility class to create {@code
 * Stream<RealLocalizableSampler<T>>} (which internally wraps {@code
 * RealLocalizableSpliterator}).
 * <p>
 * Corresponding to the {@code RealLocalizableSpliterator} flavors,
 * <ul>
 *     <li>{@link Streams#localizable(IterableRealInterval)} computes element location only on demand, and
 *     <li>{@link Streams#localizing(IterableRealInterval)} tracks location on every step.</li>
 * </ul>
 *
 * @param <T> pixel type
 */
public interface RealLocalizableSpliterator< T > extends Spliterator< T >, RealLocalizable, Sampler< T >
{
	@Override
	RealLocalizableSpliterator< T > trySplit();

	@Override
	RealLocalizableSpliterator< T > copy();
}
