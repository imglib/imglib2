package net.imglib2.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.imglib2.RealLocalizableSampler;
import net.imglib2.Sampler;
import net.imglib2.util.Cast;

class SamplerStream< T, S extends RealLocalizableSampler< T > > implements Stream< S >
{
	private final Stream< S > delegate;

	SamplerStream( Stream< S > delegate )
	{
		this.delegate = delegate;
	}

	private static < T, S extends RealLocalizableSampler< T > > SamplerStream< T, S > wrap( Stream< S > delegate )
	{
		return new SamplerStream<>( delegate );
	}

	@Override
	public Stream< S > filter( final Predicate< ? super S > predicate )
	{
		return wrap( delegate.filter( predicate ) );
	}

	@Override
	public < R > Stream< R > map( final Function< ? super S, ? extends R > mapper )
	{
		return delegate.map( mapper );
	}

	@Override
	public IntStream mapToInt( final ToIntFunction< ? super S > mapper )
	{
		return delegate.mapToInt( mapper );
	}

	@Override
	public LongStream mapToLong( final ToLongFunction< ? super S > mapper )
	{
		return delegate.mapToLong( mapper );
	}

	@Override
	public DoubleStream mapToDouble( final ToDoubleFunction< ? super S > mapper )
	{
		return delegate.mapToDouble( mapper );
	}

	@Override
	public < R > Stream< R > flatMap( final Function< ? super S, ? extends Stream< ? extends R > > mapper )
	{
		return delegate.flatMap( mapper );
	}

	@Override
	public IntStream flatMapToInt( final Function< ? super S, ? extends IntStream > mapper )
	{
		return delegate.flatMapToInt( mapper );
	}

	@Override
	public LongStream flatMapToLong( final Function< ? super S, ? extends LongStream > mapper )
	{
		return delegate.flatMapToLong( mapper );
	}

	@Override
	public DoubleStream flatMapToDouble( final Function< ? super S, ? extends DoubleStream > mapper )
	{
		return delegate.flatMapToDouble( mapper );
	}

	Stream< S > materialize()
	{
		return delegate.map( s -> Cast.unchecked( s.copy() ) );
	}

	@Override
	public Stream< S > distinct()
	{
		return wrap( materialize().distinct() );
	}

	@Override
	public Stream< S > sorted()
	{
		return wrap( materialize().sorted() );
	}

	@Override
	public Stream< S > sorted( final Comparator< ? super S > comparator )
	{
		return wrap( materialize().sorted( comparator ) );
	}

	@Override
	public Stream< S > peek( final Consumer< ? super S > action )
	{
		return wrap( delegate.peek( action ) );
	}

	@Override
	public Stream< S > limit( final long maxSize )
	{
		return wrap( delegate.limit( maxSize ) );
	}

	@Override
	public Stream< S > skip( final long n )
	{
		return wrap( delegate.skip( n ) );
	}

	@Override
	public void forEach( final Consumer< ? super S > action )
	{
		delegate.forEach( action );
	}

	@Override
	public void forEachOrdered( final Consumer< ? super S > action )
	{
		delegate.forEachOrdered( action );
	}

	@Override
	public Object[] toArray()
	{
		return materialize().toArray();
	}

	@Override
	public < A > A[] toArray( final IntFunction< A[] > generator )
	{
		return materialize().toArray( generator );
	}




	@Override
	public S reduce( final S identity, final BinaryOperator< S > accumulator )
	{
		// TODO: Should this make a case-distinction based on accumulator type?
		//       If the accumulator somehow is known to be proxy-aware, just call delegate.
		//       Otherwise, materialize()?
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public Optional< S > reduce( final BinaryOperator< S > accumulator )
	{
		// TODO: Should this make a case-distinction based on accumulator type?
		//       If the accumulator somehow is known to be proxy-aware:
		//         Optional.ofNullable( delegate.reduce( null, accumulator ) );
		//       Otherwise, materialize()?
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public < U > U reduce( final U identity, final BiFunction< U, ? super S, U > accumulator, final BinaryOperator< U > combiner )
	{
		// TODO
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public < R > R collect( final Supplier< R > supplier, final BiConsumer< R, ? super S > accumulator, final BiConsumer< R, R > combiner )
	{
		// TODO
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public < R, A > R collect( final Collector< ? super S, A, R > collector )
	{
		// TODO
		throw new UnsupportedOperationException( "TODO" );
	}





	@Override
	public Optional< S > min( final Comparator< ? super S > comparator )
	{
		return Optional.ofNullable( delegate.reduce( null, minBy( comparator ) ) );
	}

	private static < S extends Sampler< ? > > BinaryOperator< S > minBy( Comparator< ? super S > comparator )
	{
		Objects.requireNonNull( comparator );

		return ( result, element ) ->
				( element == null ) || ( result != null && comparator.compare( result, element ) <= 0 )
						? result
						: Cast.unchecked( element.copy() );
	}

	@Override
	public Optional< S > max( final Comparator< ? super S > comparator )
	{
		return Optional.ofNullable( delegate.reduce( null, maxBy( comparator ) ) );
	}

	private static < S extends Sampler< ? > > BinaryOperator< S > maxBy( Comparator< ? super S > comparator )
	{
		Objects.requireNonNull( comparator );

		return ( result, element ) ->
				( element == null ) || ( result != null && comparator.compare( result, element ) >= 0 )
						? result
						: Cast.unchecked( element.copy() );
	}

	@Override
	public long count()
	{
		return delegate.count();
	}

	@Override
	public boolean anyMatch( final Predicate< ? super S > predicate )
	{
		return delegate.anyMatch( predicate );
	}

	@Override
	public boolean allMatch( final Predicate< ? super S > predicate )
	{
		return delegate.allMatch( predicate );
	}

	@Override
	public boolean noneMatch( final Predicate< ? super S > predicate )
	{
		return delegate.noneMatch( predicate );
	}

	@Override
	public Optional< S > findFirst()
	{
		return delegate.findFirst();
	}

	@Override
	public Optional< S > findAny()
	{
		return delegate.findAny();
	}

	@Override
	public Iterator< S > iterator()
	{
		return delegate.iterator();
	}

	@Override
	public Spliterator< S > spliterator()
	{
		return delegate.spliterator();
	}

	@Override
	public boolean isParallel()
	{
		return delegate.isParallel();
	}

	@Override
	public Stream< S > sequential()
	{
		return wrap( delegate.sequential() );
	}

	@Override
	public Stream< S > parallel()
	{
		return wrap( delegate.parallel() );
	}

	@Override
	public Stream< S > unordered()
	{
		return wrap( delegate.unordered() );
	}

	@Override
	public Stream< S > onClose( final Runnable closeHandler )
	{
		return wrap( delegate.onClose( closeHandler ) );
	}

	@Override
	public void close()
	{
		delegate.close();
	}
}
