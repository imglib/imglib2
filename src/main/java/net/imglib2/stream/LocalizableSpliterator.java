package net.imglib2.stream;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;

/**
 * A {@code Spliterator<T>} which is Localizable similar to a Cursor.
 * <p>
 * The location of the Spliterator reflects the location of the element passed
 * to the {@code Consumer} in {@link #tryAdvance} or {@link #forEachRemaining}
 * (at the time the element is passed, and until the next element is passed).
 * <p>
 * Similar to {@code Cursor}, {@code LocalizableSpliterator} usually
 * comes in two flavors:
 * <ul>
 *     <li>{@link IterableInterval#spliterator()} computes location only on demand, and</li>
 *     <li>{@link IterableInterval#localizingSpliterator()} preemptively tracks location on every step.</li>
 * </ul>
 * (Which one is more efficient depends on how often location is actually needed.)
 * <p>
 * To make the {@code Localizable} property available in a {@code Stream}, use
 * the {@link Streams} utility class to create {@code
 * Stream<LocalizableSampler<T>>} (which internally wraps {@code
 * LocalizableSpliterator}).
 * <p>
 * Corresponding to the {@code LocalizableSpliterator} flavors,
 * <ul>
 *     <li>{@link Streams#localizable(IterableInterval)} computes element location only on demand, and
 *     <li>{@link Streams#localizing(IterableInterval)} tracks location on every step.</li>
 * </ul>
 *
 * @param <T> pixel type
 */
public interface LocalizableSpliterator< T > extends RealLocalizableSpliterator< T >, Localizable
{
	@Override
	LocalizableSpliterator< T > trySplit();

	@Override
	LocalizableSpliterator< T > copy();
}
