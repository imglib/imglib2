/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.Cursor;
import net.imglib2.loops.LoopBuilder.FiveConsumer;
import net.imglib2.loops.LoopBuilder.FourConsumer;
import net.imglib2.loops.LoopBuilder.SixConsumer;
import net.imglib2.loops.LoopBuilder.TriConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 * A package-private utility class that's used by {@link LoopBuilder}.
 *
 * @see FastCursorLoops#createLoop(Object, List)
 */
public final class FastCursorLoops
{
	private FastCursorLoops()
	{
		// prevent from instantiation
	}

	private static final List< ClassCopyProvider< LongConsumer > > factories = Arrays.asList(
			new ClassCopyProvider<>( OneCursorLoop.class, LongConsumer.class ),
			new ClassCopyProvider<>( TwoCursorLoop.class, LongConsumer.class ),
			new ClassCopyProvider<>( ThreeCursorLoop.class, LongConsumer.class ),
			new ClassCopyProvider<>( FourCursorLoop.class, LongConsumer.class ),
			new ClassCopyProvider<>( FiveCursorLoop.class, LongConsumer.class ),
			new ClassCopyProvider<>( SixCursorLoop.class, LongConsumer.class ) );

	/**
	 * For example.: Given a BiConsumer and two Cursors:
	 * <pre>
	 * {@code
	 *
	 * BiConsumer<A, B> biConsumer = ... ;
	 * Cursor<A> cursorA = ... ;
	 * Cursor<B> cursorB = ... ;
	 * }
	 * </pre>
	 * This method
	 * {@code createLoop(biConsumer, Arrays.asList(cursorA, cursorB))}
	 * will return a LongConsumer that is functionally equivalent to:
	 * <pre>
	 * {@code
	 *
	 * LongConsumer loop = n -> {
	 *     for ( long i = 0; i < n; i++ )
	 *         biConsumer.accept( cursorA.next(), cursorB.next() );
	 * };
	 * }
	 * </pre>
	 * The returned {@link LongConsumer} is created in a way, that it can be
	 * gracefully optimised by the Java just-in-time compiler.
	 *
	 * @param action  This must be an instance of {@link Consumer},
	 *                {@link BiConsumer}, {@link TriConsumer}, {@link FourConsumer},
	 *                {@link FiveConsumer} or {@link SixConsumer}.
	 * @param cursors A list of {@link Cursor}, the size of the list must fit
	 *                given action.
	 * @throws IllegalArgumentException if the number of cursor does not fit the given consumer.
	 */
	public static LongConsumer createLoop( final Object action, final List< ? extends Cursor< ? > > cursors )
	{
		final Object[] arguments = ListUtils.concatAsArray( action, cursors );
		ClassCopyProvider< LongConsumer > factory = factories.get( cursors.size() - 1 );
		final List< Class< ? > > key = ListUtils.map( Object::getClass, arguments );
		return factory.newInstanceForKey( key, arguments );
	}

	public static class OneCursorLoop< A > implements LongConsumer
	{

		private final Consumer< A > action;

		private final Cursor< A > cursorA;

		public OneCursorLoop( final Consumer< A > action, final Cursor< A > cursorA )
		{
			this.action = action;
			this.cursorA = cursorA;
		}

		@Override
		public void accept( long n )
		{
			while ( --n >= 0 )
				action.accept( cursorA.next() );
		}
	}

	public static class TwoCursorLoop< A, B > implements LongConsumer
	{

		private final BiConsumer< A, B > action;

		private final Cursor< A > cursorA;

		private final Cursor< B > cursorB;

		public TwoCursorLoop( final BiConsumer< A, B > action, final Cursor< A > cursorA, final Cursor< B > cursorB )
		{
			this.action = action;
			this.cursorA = cursorA;
			this.cursorB = cursorB;
		}

		@Override
		public void accept( long n )
		{
			while ( --n >= 0 )
				action.accept( cursorA.next(), cursorB.next() );
		}
	}

	public static class ThreeCursorLoop< A, B, C > implements LongConsumer
	{

		private final TriConsumer< A, B, C > action;

		private final Cursor< A > cursorA;

		private final Cursor< B > cursorB;

		private final Cursor< C > cursorC;

		public ThreeCursorLoop( final TriConsumer< A, B, C > action, final Cursor< A > cursorA, final Cursor< B > cursorB, final Cursor< C > cursorC )
		{
			this.action = action;
			this.cursorA = cursorA;
			this.cursorB = cursorB;
			this.cursorC = cursorC;
		}

		@Override
		public void accept( long n )
		{
			while ( --n >= 0 )
				action.accept( cursorA.next(), cursorB.next(), cursorC.next() );
		}
	}

	public static class FourCursorLoop< A, B, C, D > implements LongConsumer
	{

		private final FourConsumer< A, B, C, D > action;

		private final Cursor< A > cursorA;

		private final Cursor< B > cursorB;

		private final Cursor< C > cursorC;

		private final Cursor< D > cursorD;

		public FourCursorLoop( final FourConsumer< A, B, C, D > action, final Cursor< A > cursorA, final Cursor< B > cursorB, final Cursor< C > cursorC, final Cursor< D > cursorD )
		{
			this.action = action;
			this.cursorA = cursorA;
			this.cursorB = cursorB;
			this.cursorC = cursorC;
			this.cursorD = cursorD;
		}

		@Override
		public void accept( long n )
		{
			while ( --n >= 0 )
				action.accept( cursorA.next(), cursorB.next(), cursorC.next(), cursorD.next() );
		}
	}

	public static class FiveCursorLoop< A, B, C, D, E > implements LongConsumer
	{

		private final FiveConsumer< A, B, C, D, E > action;

		private final Cursor< A > cursorA;

		private final Cursor< B > cursorB;

		private final Cursor< C > cursorC;

		private final Cursor< D > cursorD;

		private final Cursor< E > cursorE;

		public FiveCursorLoop( final FiveConsumer< A, B, C, D, E > action, final Cursor< A > cursorA, final Cursor< B > cursorB, final Cursor< C > cursorC, final Cursor< D > cursorD, Cursor< E > cursorE )
		{
			this.action = action;
			this.cursorA = cursorA;
			this.cursorB = cursorB;
			this.cursorC = cursorC;
			this.cursorD = cursorD;
			this.cursorE = cursorE;
		}

		@Override
		public void accept( long n )
		{
			while ( --n >= 0 )
				action.accept( cursorA.next(), cursorB.next(), cursorC.next(), cursorD.next(), cursorE.next() );
		}
	}

	public static class SixCursorLoop< A, B, C, D, E, F > implements LongConsumer
	{

		private final SixConsumer< A, B, C, D, E, F > action;

		private final Cursor< A > cursorA;

		private final Cursor< B > cursorB;

		private final Cursor< C > cursorC;

		private final Cursor< D > cursorD;

		private final Cursor< E > cursorE;

		private final Cursor< F > cursorF;

		public SixCursorLoop( final SixConsumer< A, B, C, D, E, F > action, final Cursor< A > cursorA, final Cursor< B > cursorB, final Cursor< C > cursorC, final Cursor< D > cursorD, final Cursor< E > cursorE, final Cursor< F > cursorF )
		{
			this.action = action;
			this.cursorA = cursorA;
			this.cursorB = cursorB;
			this.cursorC = cursorC;
			this.cursorD = cursorD;
			this.cursorE = cursorE;
			this.cursorF = cursorF;
		}

		@Override
		public void accept( long n )
		{
			while ( --n >= 0 )
				action.accept( cursorA.next(), cursorB.next(), cursorC.next(), cursorD.next(), cursorE.next(), cursorF.next() );
		}
	}
}
