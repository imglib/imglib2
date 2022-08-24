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

package net.imglib2;

/**
 *
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public interface RandomAccess< T > extends Localizable, Positionable, Sampler< T >
{
	// NB: Ideally, we would utilize covariant inheritance to narrow the return
	// type of a single copy() method here, rather than needing separate methods
	// copy(), copyCursor(), copyRandomAccess() and copyRealRandomAccess().
	// Unfortunately, due to a Javac bug with multiple interface inheritance,
	// we must avoid doing so for now. For details, see:
	// http://bugs.sun.com/view_bug.do?bug_id=6656332
	// The bug is fixed in JDK7.
	@Deprecated
	public default RandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

	@Override
	RandomAccess< T > copy();

	/**
	 * Convenience method that moves the {@link RealRandomAccess} to the given
	 * position and gets the value at that position. It's a shortcut for:
	 * <p>
	 * <pre>
	 * {@code
	 * setPosition( position );
	 * get();
	 * }
	 * </pre>
	 * <p>
	 * WARNING: The return value is invalidated by next call to
	 * {@link #setPositionAndGet}  or {@link #setPosition}.
	 * <p>
	 * <pre>
	 * {@code
	 * // This is wrong!!!
	 * a = randomAccess.setPositionAndGet( positionA );
	 * b = randomAccess.setPositionAndGet( positionB ); // this invalidates "a" !!!
	 * wrongDifference = a.getRealDouble() - b.getRealDouble();
	 *
	 * // Correct:
	 * // Use individual RandomAccesses to query a and b
	 * a = randomAccess_A.setPositionAndGet( positionA );
	 * b = randomAccess_B.setPositionAndGet( positionB ); // this is fine because a different RandomAccess is used
	 * difference = a.getRealDouble() - b.getRealDouble();
	 * }
	 * </pre>
	 */
	default T setPositionAndGet( final long... position )
	{
		assert position.length >= numDimensions();

		setPosition( position );
		return get();
	}

	/**
	 * Convenience method that moves the {@link RealRandomAccess} to the given
	 * position and gets the value at that position. It's a shortcut for:
	 * <p>
	 * <pre>
	 * {@code
	 * setPosition( position );
	 * get();
	 * }
	 * </pre>
	 * <p>
	 * WARNING: The return value is invalidated by next call to
	 * {@link #setPositionAndGet}  or {@link #setPosition}.
	 * <p>
	 * <pre>
	 * {@code
	 * // This is wrong!!!
	 * a = randomAccess.setPositionAndGet( positionA );
	 * b = randomAccess.setPositionAndGet( positionB ); // this invalidates "a" !!!
	 * wrongDifference = a.getRealDouble() - b.getRealDouble();
	 *
	 * // Correct:
	 * // Use individual RandomAccesses to query a and b
	 * a = randomAccess_A.setPositionAndGet( positionA );
	 * b = randomAccess_B.setPositionAndGet( positionB ); // this is fine because a different RandomAccess is used
	 * difference = a.getRealDouble() - b.getRealDouble();
	 * }
	 * </pre>
	 */
	default T setPositionAndGet( final int... position )
	{
		assert position.length >= numDimensions();

		setPosition( position );
		return get();
	}

	/**
	 * Convenience method that moves the {@link RealRandomAccess} to the given
	 * position and gets the value at that position. It's a shortcut for:
	 * <p>
	 * <pre>
	 * {@code
	 * setPosition( position );
	 * get();
	 * }
	 * </pre>
	 * <p>
	 * WARNING: The return value is invalidated by next call to
	 * {@link #setPositionAndGet}  or {@link #setPosition}.
	 * <p>
	 * <pre>
	 * {@code
	 * // This is wrong!!!
	 * a = randomAccess.setPositionAndGet( positionA );
	 * b = randomAccess.setPositionAndGet( positionB ); // this invalidates "a" !!!
	 * wrongDifference = a.getRealDouble() - b.getRealDouble();
	 *
	 * // Correct:
	 * // Use individual RandomAccesses to query a and b
	 * a = randomAccess_A.setPositionAndGet( positionA );
	 * b = randomAccess_B.setPositionAndGet( positionB ); // this is fine because a different RandomAccess is used
	 * difference = a.getRealDouble() - b.getRealDouble();
	 * }
	 * </pre>
	 */
	default T setPositionAndGet( final Localizable position )
	{
		assert position.numDimensions() >= numDimensions();

		setPosition( position );
		return get();
	}

}
