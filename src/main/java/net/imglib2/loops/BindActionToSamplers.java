/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.Sampler;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Package-private utility class that's used by {@link LoopBuilder}.
 */
final class BindActionToSamplers
{

	private static final List< ClassCopyProvider< Runnable > > factories = Arrays.asList(
			new ClassCopyProvider<>( ConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( BiConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( TriConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( FourConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( FiveConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( SixConsumerRunnable.class, Runnable.class ));

	/**
	 * For example.: Given a BiConsumer and two Samplers:
	 *
	 * <pre>
	 * {@code
	 *
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
	 * {@code
	 *
	 * Runnable result = () -> {
	 *     biConsumer.accept( sampleA.get(), samplerB.get() );
	 * };
	 * }
	 * </pre>
	 *
	 * It does it in such manner, that the returned {@link Runnable} can be
	 * gracefully optimised by the Java just-in-time compiler.
	 *
	 * @param action
	 *            This must be an instance of {@link Consumer},
	 *            {@link BiConsumer} of {@link LoopBuilder.TriConsumer}.
	 *            {@link LoopBuilder.FourConsumer}, {@link LoopBuilder.FourConsumer},
	 *            or {@link LoopBuilder.SixConsumer}.
	 * @param samplers
	 *            A list of {@link Sampler}, the size of the list must fit
	 *            the consumer given by {@param operation}.
	 * @throws IllegalArgumentException
	 *             if the number of sampler does not fit the given consumer.
	 */
	public static Runnable bindActionToSamplers( final Object action, final List< ? extends Sampler< ? > > samplers )
	{
		final Object[] arguments = ListUtils.concatAsArray( action, samplers );
		final ClassCopyProvider< Runnable > factory = factories.get( samplers.size() - 1 );
		final List< Class< ? extends Object > > key = ListUtils.map( Object::getClass, arguments );
		return factory.newInstanceForKey( key, arguments );
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

		private final LoopBuilder.TriConsumer< A, B, C > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		public TriConsumerRunnable( final LoopBuilder.TriConsumer< A, B, C > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC )
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

	public static class FourConsumerRunnable< A, B, C, D > implements Runnable
	{

		private final LoopBuilder.FourConsumer< A, B, C, D > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		public FourConsumerRunnable( final LoopBuilder.FourConsumer< A, B, C, D > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get() );
		}
	}

	public static class FiveConsumerRunnable< A, B, C, D, E > implements Runnable
	{

		private final LoopBuilder.FiveConsumer< A, B, C, D, E > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		private final Sampler< E > samplerE;

		public FiveConsumerRunnable( final LoopBuilder.FiveConsumer< A, B, C, D, E > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD, Sampler< E > samplerE )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
			this.samplerE = samplerE;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get(), samplerE.get() );
		}
	}

	public static class SixConsumerRunnable< A, B, C, D, E, F > implements Runnable
	{

		private final LoopBuilder.SixConsumer< A, B, C, D, E, F > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		private final Sampler< E > samplerE;

		private final Sampler< F > samplerF;

		public SixConsumerRunnable( final LoopBuilder.SixConsumer< A, B, C, D, E, F > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD, final Sampler< E > samplerE, final Sampler< F > samplerF )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
			this.samplerE = samplerE;
			this.samplerF = samplerF;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get(), samplerE.get(), samplerF.get() );
		}
	}
}
