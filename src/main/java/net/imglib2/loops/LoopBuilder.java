/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.loops;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;
import net.imglib2.util.Intervals;

/**
 * {@link LoopBuilder} provides an easy way to write fast loops on
 * {@link RandomAccessibleInterval}s. For example, this is a loop that
 * calculates the sum of two images:
 *
 * <pre>
 * {@code
 * RandomAccessibleInterval<DoubleType> imageA = ...
 * RandomAccessibleInterval<DoubleType> imageB = ...
 * RandomAccessibleInterval<DoubleType> sum = ...
 *
 * LoopBuilder.setImages(imageA, imageB, sum).run(
 *     (a, b, s) -> {
 *          s.setReal(a.getRealDouble() + b.getRealDouble());
 *     }
 * );
 * }
 * </pre>
 *
 * The {@link RandomAccessibleInterval}s {@code imageA}, {@code imageB} and
 * {@code sum} must have equal dimensions, but the bounds of there
 * {@link Intervals} can differ.
 *
 * @author Matthias Arzt
 */
public class LoopBuilder< T >
{

	private final Dimensions dimensions;

	private final RandomAccessibleInterval< ? >[] images;

	private LoopBuilder( final RandomAccessibleInterval< ? >... images )
	{
		this.images = images;
		this.dimensions = new FinalInterval( images[ 0 ] );
		Arrays.asList( images ).forEach( this::checkDimensions );
	}

	private void checkDimensions( final Interval interval )
	{
		final long[] a = Intervals.dimensionsAsLongArray( dimensions );
		final long[] b = Intervals.dimensionsAsLongArray( interval );
		if ( !Arrays.equals( a, b ) )
			throw new IllegalArgumentException( "Dimensions do not fit." );
	}

	public static < A > LoopBuilder< Consumer< A > > setImages( final RandomAccessibleInterval< A > a )
	{
		return new LoopBuilder<>( a );
	}

	public static < A, B > LoopBuilder< BiConsumer< A, B > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b )
	{
		return new LoopBuilder<>( a, b );
	}

	public static < A, B, C > LoopBuilder< TriConsumer< A, B, C > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b, final RandomAccessibleInterval< C > c )
	{
		return new LoopBuilder<>( a, b, c );
	}

	public void forEachPixel( final T action )
	{
		Objects.requireNonNull( action );
		final List< RandomAccess< ? > > samplers = Stream.of( images ).map( this::initRandomAccess ).collect( Collectors.toList() );
		final Positionable synced = SyncedPositionables.create( samplers );
		LoopUtils.createIntervalLoop( synced, dimensions, RunnableFactory.bindActionToSamplers( action, samplers ) ).run();
	}

	private RandomAccess< ? > initRandomAccess( final RandomAccessibleInterval< ? > image )
	{
		final RandomAccess< ? > ra = image.randomAccess();
		ra.setPosition( Intervals.minAsLongArray( image ) );
		return ra;
	}

	public interface TriConsumer< A, B, C >
	{
		void accept( A a, B b, C c );
	}

	private static class RunnableFactory
	{

		private static final List< ClassCopyProvider< Runnable > > factories = Arrays.asList(
				new ClassCopyProvider<>( ConsumerRunnable.class, Runnable.class ),
				new ClassCopyProvider<>( BiConsumerRunnable.class, Runnable.class ),
				new ClassCopyProvider<>( TriConsumerRunnable.class, Runnable.class ) );

		/**
		 * For example.: Given a BiConsumer and two Samplers:
		 *
		 * <pre>
		 * {@code
		 * BiConsumer<A, B> biConsumer = ... ;
		 * Sampler<A> samplerA = ... ;
		 * Sampler<B> samplerB = ... ;
		 * }
		 * </pre>
		 *
		 * This method
		 * {@code bindConsumerToSamplers(biConsumer, Arrays.asList(samplerA, samplerB))}
		 * will return a Runnable that is functionally equivalent to:
		 *
		 * <pre>
		 * {
		 * 	&#64;code
		 * 	Runnable result = () -> {
		 * 		biConsumer.accept( sampleA.get(), samplerB.get() );
		 * 	};
		 * }
		 * </pre>
		 *
		 * It does it in such manner, that the returned {@link Runnable} can be
		 * gracefully optimised by the Java just-in-time compiler.
		 *
		 * @param action
		 *            This must be an instance of {@link Consumer},
		 *            {@link BiConsumer} of {@link TriConsumer}.
		 * @param samplers
		 *            A list of {@link Sampler}, the size of the list must fit
		 *            the consumer given by {@param operation}.
		 * @throws IllegalArgumentException
		 *             if the number of sampler does not fit the given consumer.
		 */
		public static Runnable bindActionToSamplers( final Object action, final List< ? extends Sampler< ? > > samplers )
		{
			final Object[] arguments = Stream.concat( Stream.of( action ), samplers.stream() ).toArray();
			for ( final ClassCopyProvider< Runnable > factory : factories )
				if ( factory.matches( arguments ) )
				{
					final List< Class< ? extends Object > > key = Stream.of( arguments ).map( Object::getClass ).collect( Collectors.toList() );
					return factory.newInstanceForKey( key, arguments );
				}
			throw new IllegalArgumentException();
		}

		public static class ConsumerRunnable< A > implements Runnable
		{

			private final Consumer< A > action;

			private final Sampler< A > samplerA;

			public ConsumerRunnable( final Consumer< A > action, final Sampler< A > samplerA )
			{
				this.action = action;
				this.samplerA = samplerA;
			}

			@Override
			public void run()
			{
				action.accept( samplerA.get() );
			}
		}

		public static class BiConsumerRunnable< A, B > implements Runnable
		{

			private final BiConsumer< A, B > action;

			private final Sampler< A > samplerA;

			private final Sampler< B > samplerB;

			public BiConsumerRunnable( final BiConsumer< A, B > action, final Sampler< A > samplerA, final Sampler< B > samplerB )
			{
				this.action = action;
				this.samplerA = samplerA;
				this.samplerB = samplerB;
			}

			@Override
			public void run()
			{
				action.accept( samplerA.get(), samplerB.get() );
			}
		}

		public static class TriConsumerRunnable< A, B, C > implements Runnable
		{

			private final TriConsumer< A, B, C > action;

			private final Sampler< A > samplerA;

			private final Sampler< B > samplerB;

			private final Sampler< C > samplerC;

			public TriConsumerRunnable( final TriConsumer< A, B, C > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC )
			{
				this.action = action;
				this.samplerA = samplerA;
				this.samplerB = samplerB;
				this.samplerC = samplerC;
			}

			@Override
			public void run()
			{
				action.accept( samplerA.get(), samplerB.get(), samplerC.get() );
			}
		}
	}
}
