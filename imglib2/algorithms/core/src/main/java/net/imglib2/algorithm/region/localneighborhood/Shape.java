package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;

/**
 * A factory for Accessibles on {@link Neighborhood Neighborhoods}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface Shape
{
	/**
	 * Get an {@link IterableInterval} that contains all {@link Neighborhood
	 * Neighborhoods} of the source image.
	 * 
	 * <p>
	 * A {@link Cursor} on the resulting accessible can be used to access the
	 * {@link Neighborhood neighborhoods}. As usual, when the cursor is moved, a
	 * neighborhood {@link Sampler#get() obtained} previously from the cursor
	 * should be considered invalid.
	 * </p>
	 * 
	 * <p>
	 * <em>The {@link Neighborhood neighborhoods} that are obtained from the
	 * resulting accessible are unsafe in the following sense:</em> Every time,
	 * a {@link Cursor} is requested (using {@link Neighborhood#cursor()} etc)
	 * the same {@link Cursor} instance is re-used. If you require to have more
	 * than one {@link Cursor} into the current neighborhood at a given time you
	 * have several options:
	 * <ol>
	 * <li> {@link Cursor#copy()} the cursor you obtained using
	 * {@link Neighborhood#cursor()}.
	 * <li>use multiple parent cursors, i.e., cursors on the
	 * {@link IterableInterval}<{@link Neighborhood}>.
	 * <li>use {@link #neighborhoodsSafe(RandomAccessibleInterval)} which
	 * returns a new {@link Neighborhood#cursor()} every time (but therefore is
	 * not recommended if you want to use enhanced for loops).
	 * </ol>
	 * </p>
	 * 
	 * @param source
	 *            source image.
	 * @return an {@link IterableInterval} that contains all
	 *         {@link Neighborhood Neighborhoods} of the source image.
	 */
	public < T > IterableInterval< Neighborhood< T > > neighborhoods( final RandomAccessibleInterval< T > source );

	/**
	 * Get an {@link RandomAccessibleInterval} that contains all
	 * {@link Neighborhood Neighborhoods} of the source image.
	 * 
	 * <p>
	 * A {@link RandomAccess} on the resulting accessible can be used to access
	 * the {@link Neighborhood neighborhoods}. As usual, when the access is
	 * moved, a neighborhood {@link Sampler#get() obtained} previously from the
	 * access should be considered invalid.
	 * </p>
	 * 
	 * <p>
	 * <em>The {@link Neighborhood neighborhoods} that are obtained from the
	 * resulting accessible are unsafe in the following sense:</em> Every time,
	 * a {@link Cursor} is requested (using {@link Neighborhood#cursor()} etc)
	 * the same {@link Cursor} instance is re-used. If you require to have more
	 * than one {@link Cursor} into the current neighborhood at a given time you
	 * have several options:
	 * <ol>
	 * <li> {@link Cursor#copy()} the cursor you obtained using
	 * {@link Neighborhood#cursor()}.
	 * <li>use multiple parent {@link RandomAccess RandomAccesses}, i.e.,
	 * accesses on the {@link RandomAccessibleInterval}<{@link Neighborhood}>.
	 * <li>use
	 * {@link #neighborhoodsRandomAccessibleSafe(RandomAccessibleInterval)}
	 * which returns a new {@link Neighborhood#cursor()} every time (but
	 * therefore is not recommended if you want to use enhanced for loops).
	 * </ol>
	 * </p>
	 * 
	 * @param source
	 *            source image.
	 * @return an {@link RandomAccessibleInterval} that contains all
	 *         {@link Neighborhood Neighborhoods} of the source image.
	 */
	public < T > RandomAccessibleInterval< Neighborhood< T > > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source );

	/**
	 * Get an {@link IterableInterval} that contains all {@link Neighborhood
	 * Neighborhoods} of the source image.
	 * 
	 * <p>
	 * A {@link Cursor} on the resulting accessible can be used to access the
	 * {@link Neighborhood neighborhoods}. As usual, when the cursor is moved, a
	 * neighborhood {@link Sampler#get() obtained} previously from the cursor
	 * should be considered invalid.
	 * </p>
	 * 
	 * <p>
	 * Every time, a {@link Cursor} is requested from a {@link Neighborhood}
	 * (where the neighborhood in turn is obtained from a cursor on the
	 * IterableInterval returned by this method) a new {@link Cursor} instance
	 * is created. If you want to use enhanced for loops on the
	 * {@link Neighborhood neighborhoods}, consider using
	 * {@link #neighborhoods(RandomAccessibleInterval)} which re-uses the same
	 * instance every time (but therefore has to be used carefully).
	 * </p>
	 * 
	 * @param source
	 *            source image.
	 * @return an {@link IterableInterval} that contains all
	 *         {@link Neighborhood Neighborhoods} of the source image.
	 */
	public < T > IterableInterval< Neighborhood< T > > neighborhoodsSafe( final RandomAccessibleInterval< T > source );

	/**
	 * Get an {@link RandomAccessibleInterval} that contains all
	 * {@link Neighborhood Neighborhoods} of the source image.
	 * 
	 * <p>
	 * A {@link RandomAccess} on the resulting accessible can be used to access
	 * the {@link Neighborhood neighborhoods}. As usual, when the access is
	 * moved, a neighborhood {@link Sampler#get() obtained} previously from the
	 * access should be considered invalid.
	 * </p>
	 * 
	 * <p>
	 * Every time, a {@link Cursor} is requested from a {@link Neighborhood}
	 * (where the neighborhood in turn is obtained from a cursor on the
	 * IterableInterval returned by this method) a new {@link Cursor} instance
	 * is created. If you want to use enhanced for loops on the
	 * {@link Neighborhood neighborhoods}, consider using
	 * {@link #neighborhoods(RandomAccessibleInterval)} which re-uses the same
	 * instance every time (but therefore has to be used carefully).
	 * </p>
	 * 
	 * @param source
	 *            source image.
	 * @return an {@link RandomAccessibleInterval} that contains all
	 *         {@link Neighborhood Neighborhoods} of the source image.
	 */
	public < T > RandomAccessibleInterval< Neighborhood< T > > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source );
}
