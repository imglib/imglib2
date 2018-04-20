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

import net.imglib2.Dimensions;
import net.imglib2.Positionable;

/**
 * {@link LoopUtils} contains methods to simplify writing a loop over an image
 * line or image interval.
 *
 * @author Matthias Arzt
 */
final public class LoopUtils
{

	private LoopUtils()
	{
		// prevent from instantiation.
	}

	private static ClassCopyProvider< Runnable > factory = new ClassCopyProvider<>( LineProcessor.class, Runnable.class );

	/**
	 * <p>
	 * Returns a loop, that moves the given positonable along a line, and
	 * executes the given operation for each pixel of the line. The method uses
	 * {@link ClassCopyProvider}, such that the returned loop can be optimised
	 * gracefully by the java just-in-time compiler. Aside from that, the result
	 * is functionally equivalent to:
	 * </p>
	 *
	 * <pre>
	 * {@code Runnable result = () -> {
	 *     for (long i = 0; i < length; i++) {
	 *         operation.run();
	 *         positionable.fwd(dimension);
	 *     }
	 *     positionable.move(- length, dimension);
	 * }
	 * }
	 * </pre>
	 *
	 * @param positionable
	 *            Positionable that is moved (along a line). Defines the
	 *            starting point of the line. After the loops execution the
	 *            positionable is moved back to the starting point.
	 * @param length
	 *            Length of the line.
	 * @param dimension
	 *            Direction of the line.
	 * @param action
	 *            Operation that is executed for each pixel along the line.
	 * @return A {@link Runnable} that is functionally equivalent to:
	 */
	public static Runnable createLineLoop( final Positionable positionable, final long length, final int dimension, final Runnable action )
	{
		final List< Object > key = Arrays.asList( action.getClass() );
		return factory.newInstanceForKey( key, action, positionable, length, dimension );
	}

	/**
	 * Returns a {@link Runnable} containing a loop. The loop moves the given
	 * positionable over all the pixels of an interval. For each pixel of the
	 * interval the given operation is executed.
	 *
	 * @param positionable
	 *            Positionable that is moved. Defines the minimum point of the
	 *            interval. After the loops execution the positionable is moved
	 *            back to the starting point.
	 * @param dimensions
	 *            Dimensions of the interval.
	 * @param action
	 *            Operation that is executed for each pixel of the interval.
	 */
	public static Runnable createIntervalLoop( final Positionable positionable, final Dimensions dimensions, Runnable action )
	{
		for ( int i = 0; i < dimensions.numDimensions(); i++ )
		{
			final long dimension = dimensions.dimension( i );
			if ( dimension > 1 )
				action = createLineLoop( positionable, dimension, i, action );
			else if ( dimension <= 0 )
				action = () -> {};
		}
		return action;
	}

	public static class LineProcessor implements Runnable
	{

		private final Runnable action;

		private final Positionable positionable;

		private final long lineLength;

		private final int dimension;

		public LineProcessor( final Runnable action, final Positionable positionable, final long length, final int dimension )
		{
			this.action = action;
			this.positionable = positionable;
			this.lineLength = length;
			this.dimension = dimension;
		}

		@Override
		public void run()
		{
			for ( long i = 0; i < lineLength; i++ )
			{
				action.run();
				positionable.fwd( dimension );
			}
			positionable.move( -lineLength, dimension );
		}
	}
}
