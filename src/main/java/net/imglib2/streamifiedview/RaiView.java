package net.imglib2.streamifiedview;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RaiView< T > extends RaView< T, RaiView< T > >, RandomAccessibleInterval< T >
{
	default RaiView< T > expandValue( final T value, long... border )
	{
		return wrap( Views.expandValue( delegate(), value, border ) );
	}

	default RaiView< T > permute( final int from, final int to )
	{
		return wrap( Views.permute( delegate(), from, to ) );
	}

	default RaiView< T > translate( long... translation )
	{
		return wrap( Views.translate( delegate(), translation ) );
	}

	default RaiView< T >  zeroMin()
	{
		return wrap( Views.zeroMin( delegate() ) );
	}

	default RaiView< T > rotate( int fromAxis, int toAxis )
	{
		return wrap( Views.rotate( delegate(), fromAxis, toAxis ) );
	}

	default RaiView< T > hyperSlice( int d, long pos )
	{
		return wrap( Views.hyperSlice( delegate(), d, pos ) );
	}

	default RaView< T, ? > extendBorder()
	{
		return RaView.wrap( Views.extendBorder( delegate() ) );
	}

	@Override
	RandomAccessibleInterval< T > delegate();

	static < T > RaiView< T > wrap( final RandomAccessibleInterval< T > delegate )
	{
		return new RaiWrapper<>( delegate );
	}

	@Override
	default RaiView< T > view()
	{
		return this;
	}

	@Override
	default T getType()
	{
		return delegate().getType();
	}

	// TODO: Delegate all methods of RandomAccessibleInterval, also those that
	//       have a default implementations ...

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}

	@Override
	default long min( final int d )
	{
		return delegate().min( d );
	}

	@Override
	default long max( final int d )
	{
		return delegate().max( d );
	}

	@Override
	default RandomAccess< T > randomAccess()
	{
		return delegate().randomAccess();
	}

	@Override
	default RandomAccess< T > randomAccess( final Interval interval )
	{
		return delegate().randomAccess(interval);
	}

	@Override
	default Cursor< T > cursor()
	{
		return delegate().cursor();
	}

	@Override
	default Cursor< T > localizingCursor()
	{
		return delegate().localizingCursor();
	}

	@Override
	default long size()
	{
		return delegate().size();
	}

	@Override
	default Object iterationOrder()
	{
		return delegate().iterationOrder();
	}
}
