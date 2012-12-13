package net.imglib2.img.list;

import java.util.ArrayList;

import net.imglib2.FlatIterationOrder;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;

/**
 * Abstract base class for {@link Img} that store pixels in a single linear
 * array (an {@link ArrayList} or similar).In principle, the number of entities
 * stored is limited to {@link Integer#MAX_VALUE}.
 *
 * Derived classes need to implement the {@link #get(int)} and
 * {@link #set(int, Object)} methods that are used by accessors to access
 * pixels. These could be implemented to fetch pixels from an {@link ArrayList},
 * create them on the fly, cache to disk, etc.
 *
 * @param <T>
 *            The value type of the pixels. You can us {@link Type}s or
 *            arbitrary {@link Object}s. If you use non-{@link Type} pixels,
 *            note, that you cannot use {@link Type#set(Type)} to change the
 *            value stored in every reference. Instead, you can use the
 *            {@link ListCursor#set(Object)} and
 *            {@link ListRandomAccess#set(Object)} methods to alter pixels.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractListImg< T > extends AbstractImg< T >
{
	final protected int[] step;

	final protected int[] dim;

	protected AbstractListImg( final long[] dim )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, step );
	}

	protected abstract T get( final int index );

	protected abstract void set( final int index, final T value );

	@Override
	public ListCursor< T > cursor()
	{
		return new ListCursor< T >( this );
	}

	@Override
	public ListLocalizingCursor< T > localizingCursor()
	{
		return new ListLocalizingCursor< T >( this );
	}

	@Override
	public ListRandomAccess< T > randomAccess()
	{
		return new ListRandomAccess< T >( this );
	}

	@Override
	public ListImgFactory< T > factory()
	{
		return new ListImgFactory< T >();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}
}
