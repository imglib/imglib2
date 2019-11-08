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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

/**
 * Package-private utility class that's used by {@link LoopBuilder}.
 */
final class BindActionToSamplers
{

	private static final List< ClassCopyProvider< Runnable > > LOCALIZING_FACTORIES = Arrays.asList(
			new ClassCopyProvider<>( ConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( BiConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( TriConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( FourConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( FiveConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( SixConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( SevenConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( EightConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( NineConsumerLocalizingRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( TenConsumerLocalizingRunnable.class, Runnable.class ));

	private static final List< ClassCopyProvider< Runnable > > FACTORIES = Arrays.asList(
			new ClassCopyProvider<>( ConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( BiConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( TriConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( FourConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( FiveConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( SixConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( SevenConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( EightConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( NineConsumerRunnable.class, Runnable.class ),
			new ClassCopyProvider<>( TenConsumerRunnable.class, Runnable.class ));

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
	 *            {@link LoopBuilder.FourConsumer}, {@link LoopBuilder.FiveConsumer},
	 *            or {@link LoopBuilder.SixConsumer}.
	 * @param samplers
	 *            A list of {@link Sampler}, the size of the list must fit
	 *            the consumer given by {@code action}.
	 * @param localizing
	 *            If true, action operates on {@link RandomAccess} objects typed
	 *            the same as type {@link Sampler}s; if false, action operates
	 *            on {@link Sampler} elements directly.
	 * @throws IllegalArgumentException
	 *             if the number of sampler does not fit the given consumer.
	 */
	public static Runnable bindActionToSamplers( final Object action, final List< ? extends Sampler< ? > > samplers, final boolean localizing )
	{
		final Object[] arguments;
		final List< ClassCopyProvider< Runnable > > factories;
		if ( localizing )
		{
			arguments = Stream.concat( Stream.of( action ), samplers.stream() ).toArray();
			factories = LOCALIZING_FACTORIES;
		}
		else
		{
			arguments = Stream.concat( Stream.of( action ), samplers.stream() ).toArray();
			factories = FACTORIES;
		}
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

	public static class SevenConsumerRunnable< A, B, C, D, E, F, G > implements Runnable
	{

		private final LoopBuilder.SevenConsumer< A, B, C, D, E, F, G > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		private final Sampler< E > samplerE;

		private final Sampler< F > samplerF;

		private final Sampler< G > samplerG;

		public SevenConsumerRunnable( final LoopBuilder.SevenConsumer< A, B, C, D, E, F, G > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD, final Sampler< E > samplerE, final Sampler< F > samplerF, final Sampler< G > samplerG )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
			this.samplerE = samplerE;
			this.samplerF = samplerF;
			this.samplerG = samplerG;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get(), samplerE.get(), samplerF.get(), samplerG.get() );
		}
	}

	public static class EightConsumerRunnable< A, B, C, D, E, F, G, H > implements Runnable
	{

		private final LoopBuilder.EightConsumer< A, B, C, D, E, F, G, H > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		private final Sampler< E > samplerE;

		private final Sampler< F > samplerF;

		private final Sampler< G > samplerG;

		private final Sampler< H > samplerH;

		public EightConsumerRunnable( final LoopBuilder.EightConsumer< A, B, C, D, E, F, G, H > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD, final Sampler< E > samplerE, final Sampler< F > samplerF, final Sampler< G > samplerG, final Sampler< H > samplerH )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
			this.samplerE = samplerE;
			this.samplerF = samplerF;
			this.samplerG = samplerG;
			this.samplerH = samplerH;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get(), samplerE.get(), samplerF.get(), samplerG.get(), samplerH.get() );
		}
	}

	public static class NineConsumerRunnable< A, B, C, D, E, F, G, H, I > implements Runnable
	{

		private final LoopBuilder.NineConsumer< A, B, C, D, E, F, G, H, I > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		private final Sampler< E > samplerE;

		private final Sampler< F > samplerF;

		private final Sampler< G > samplerG;

		private final Sampler< H > samplerH;

		private final Sampler< I > samplerI;

		public NineConsumerRunnable( final LoopBuilder.NineConsumer< A, B, C, D, E, F, G, H, I > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD, final Sampler< E > samplerE, final Sampler< F > samplerF, final Sampler< G > samplerG, final Sampler< H > samplerH, final Sampler< I > samplerI )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
			this.samplerE = samplerE;
			this.samplerF = samplerF;
			this.samplerG = samplerG;
			this.samplerH = samplerH;
			this.samplerI = samplerI;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get(), samplerE.get(), samplerF.get(), samplerG.get(), samplerH.get(), samplerI.get() );
		}
	}

	public static class TenConsumerRunnable< A, B, C, D, E, F, G, H, I, J > implements Runnable
	{

		private final LoopBuilder.TenConsumer< A, B, C, D, E, F, G, H, I, J > action;

		private final Sampler< A > samplerA;

		private final Sampler< B > samplerB;

		private final Sampler< C > samplerC;

		private final Sampler< D > samplerD;

		private final Sampler< E > samplerE;

		private final Sampler< F > samplerF;

		private final Sampler< G > samplerG;

		private final Sampler< H > samplerH;

		private final Sampler< I > samplerI;

		private final Sampler< J > samplerJ;

		public TenConsumerRunnable( final LoopBuilder.TenConsumer< A, B, C, D, E, F, G, H, I, J > action, final Sampler< A > samplerA, final Sampler< B > samplerB, final Sampler< C > samplerC, final Sampler< D > samplerD, final Sampler< E > samplerE, final Sampler< F > samplerF, final Sampler< G > samplerG, final Sampler< H > samplerH, final Sampler< I > samplerI, final Sampler< J > samplerJ )
		{
			this.action = action;
			this.samplerA = samplerA;
			this.samplerB = samplerB;
			this.samplerC = samplerC;
			this.samplerD = samplerD;
			this.samplerE = samplerE;
			this.samplerF = samplerF;
			this.samplerG = samplerG;
			this.samplerH = samplerH;
			this.samplerI = samplerI;
			this.samplerJ = samplerJ;
		}

		@Override
		public void run()
		{
			action.accept( samplerA.get(), samplerB.get(), samplerC.get(), samplerD.get(), samplerE.get(), samplerF.get(), samplerG.get(), samplerH.get(), samplerI.get(), samplerJ.get() );
		}
	}

	public static class ConsumerLocalizingRunnable< A > implements Runnable
	{

		private final Consumer< RandomAccess< A > > action;

		private final RandomAccess< A > accessA;

		public ConsumerLocalizingRunnable( final Consumer< RandomAccess< A > > action, final RandomAccess< A > accessA )
		{
			this.action = action;
			this.accessA = accessA;
		}

		@Override
		public void run()
		{
			action.accept( accessA );
		}
	}

	public static class BiConsumerLocalizingRunnable< A, B > implements Runnable
	{

		private final BiConsumer< RandomAccess< A >, RandomAccess< B > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		public BiConsumerLocalizingRunnable( final BiConsumer< RandomAccess< A >, RandomAccess< B > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB );
		}
	}

	public static class TriConsumerLocalizingRunnable< A, B, C > implements Runnable
	{

		private final LoopBuilder.TriConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		public TriConsumerLocalizingRunnable( final LoopBuilder.TriConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC );
		}
	}

	public static class FourConsumerLocalizingRunnable< A, B, C, D > implements Runnable
	{

		private final LoopBuilder.FourConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		public FourConsumerLocalizingRunnable( final LoopBuilder.FourConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD );
		}
	}

	public static class FiveConsumerLocalizingRunnable< A, B, C, D, E > implements Runnable
	{

		private final LoopBuilder.FiveConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D >, RandomAccess< E > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		private final RandomAccess< E > accessE;

		public FiveConsumerLocalizingRunnable( final LoopBuilder.FiveConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D >, RandomAccess< E > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD, final RandomAccess< E > accessE )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
			this.accessE = accessE;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD, accessE );
		}
	}

	public static class SixConsumerLocalizingRunnable< A, B, C, D, E, F > implements Runnable
	{

		private final LoopBuilder.SixConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		private final RandomAccess< E > accessE;

		private final RandomAccess< F > accessF;

		public SixConsumerLocalizingRunnable( final LoopBuilder.SixConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD, final RandomAccess< E > accessE, final RandomAccess< F > accessF )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
			this.accessE = accessE;
			this.accessF = accessF;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD, accessE, accessF );
		}
	}

	public static class SevenConsumerLocalizingRunnable< A, B, C, D, E, F, G > implements Runnable
	{

		private final LoopBuilder.SevenConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		private final RandomAccess< E > accessE;

		private final RandomAccess< F > accessF;

		private final RandomAccess< G > accessG;

		public SevenConsumerLocalizingRunnable( final LoopBuilder.SevenConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD, final RandomAccess< E > accessE, final RandomAccess< F > accessF, final RandomAccess< G > accessG )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
			this.accessE = accessE;
			this.accessF = accessF;
			this.accessG = accessG;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD, accessE, accessF, accessG );
		}
	}

	public static class EightConsumerLocalizingRunnable< A, B, C, D, E, F, G, H > implements Runnable
	{

		private final LoopBuilder.EightConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G >, RandomAccess< H > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		private final RandomAccess< E > accessE;

		private final RandomAccess< F > accessF;

		private final RandomAccess< G > accessG;

		private final RandomAccess< H > accessH;

		public EightConsumerLocalizingRunnable( final LoopBuilder.EightConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G >, RandomAccess< H > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD, final RandomAccess< E > accessE, final RandomAccess< F > accessF, final RandomAccess< G > accessG, final RandomAccess< H > accessH )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
			this.accessE = accessE;
			this.accessF = accessF;
			this.accessG = accessG;
			this.accessH = accessH;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD, accessE, accessF, accessG, accessH );
		}
	}

	public static class NineConsumerLocalizingRunnable< A, B, C, D, E, F, G, H, I > implements Runnable
	{

		private final LoopBuilder.NineConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G >, RandomAccess< H >, RandomAccess< I > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		private final RandomAccess< E > accessE;

		private final RandomAccess< F > accessF;

		private final RandomAccess< G > accessG;

		private final RandomAccess< H > accessH;

		private final RandomAccess< I > accessI;

		public NineConsumerLocalizingRunnable( final LoopBuilder.NineConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G >, RandomAccess< H >, RandomAccess< I > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD, final RandomAccess< E > accessE, final RandomAccess< F > accessF, final RandomAccess< G > accessG, final RandomAccess< H > accessH, final RandomAccess< I > accessI )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
			this.accessE = accessE;
			this.accessF = accessF;
			this.accessG = accessG;
			this.accessH = accessH;
			this.accessI = accessI;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD, accessE, accessF, accessG, accessH, accessI );
		}
	}

	public static class TenConsumerLocalizingRunnable< A, B, C, D, E, F, G, H, I, J > implements Runnable
	{

		private final LoopBuilder.TenConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess< C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G >, RandomAccess< H >, RandomAccess< I >, RandomAccess< J > > action;

		private final RandomAccess< A > accessA;

		private final RandomAccess< B > accessB;

		private final RandomAccess< C > accessC;

		private final RandomAccess< D > accessD;

		private final RandomAccess< E > accessE;

		private final RandomAccess< F > accessF;

		private final RandomAccess< G > accessG;

		private final RandomAccess< H > accessH;

		private final RandomAccess< I > accessI;

		private final RandomAccess< J > accessJ;

		public TenConsumerLocalizingRunnable( final LoopBuilder.TenConsumer< RandomAccess< A >, RandomAccess< B >, RandomAccess < C >, RandomAccess< D >, RandomAccess< E >, RandomAccess< F >, RandomAccess< G >, RandomAccess< H >, RandomAccess< I >, RandomAccess< J > > action, final RandomAccess< A > accessA, final RandomAccess< B > accessB, final RandomAccess< C > accessC, final RandomAccess< D > accessD, final RandomAccess< E > accessE, final RandomAccess< F > accessF, final RandomAccess< G > accessG, final RandomAccess< H > accessH, final RandomAccess< I > accessI, final RandomAccess< J > accessJ )
		{
			this.action = action;
			this.accessA = accessA;
			this.accessB = accessB;
			this.accessC = accessC;
			this.accessD = accessD;
			this.accessE = accessE;
			this.accessF = accessF;
			this.accessG = accessG;
			this.accessH = accessH;
			this.accessI = accessI;
			this.accessJ = accessJ;
		}

		@Override
		public void run()
		{
			action.accept( accessA, accessB, accessC, accessD, accessE, accessF, accessG, accessH, accessI, accessJ );
		}
	}
}
