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
	RandomAccess< T > copyRandomAccess();
//	@Override
//	RandomAccess< T > copy();

	/**
	 * 
	 * Convenience method to query a {@code RandomAccess} for the value at a
	 * position and is shortcut for
	 * 
	 * <pre>
	 * {@code
	 * setPosition( position );
	 * get();
	 * }
	 * </pre>
	 * 
	 * This is designed for convenience only. Avoid use in tight loops or other
	 * scenarios where efficiency is crucial.
	 * 
	 * @param position
	 * @return value of the the {@code RandomAccess} at {@code position}.
	 */
	default T getAt( long... position )
	{
		assert position.length == numDimensions();

		setPosition( position );
		return get();
	}

	/**
	 * 
	 * Convenience method to query a {@code RandomAccess} for the value at a
	 * position and is shortcut for
	 * 
	 * <pre>
	 * {@code
	 * setPosition( position );
	 * get();
	 * }
	 * </pre>
	 * 
	 * This is designed for convenience only. Avoid use in tight loops or other
	 * scenarios where efficiency is crucial.
	 * 
	 * @param position
	 * @return value of the the {@code RandomAccess} at {@code position}.
	 */
	default T getAt( int... position )
	{
		assert position.length == numDimensions();

		setPosition( position );
		return get();
	}

	/**
	 * 
	 * Convenience method to query a {@code RandomAccess} for the value at a
	 * position and is shortcut for
	 * 
	 * <pre>
	 * {@code
	 * setPosition( position );
	 * get();
	 * }
	 * </pre>
	 * 
	 * This is designed for convenience only. Avoid use in tight loops or other
	 * scenarios where efficiency is crucial.
	 * 
	 * @param position
	 * @return value of the the {@code RandomAccess} at {@code position}.
	 */
	default T getAt( Localizable position )
	{
		assert position.numDimensions() == numDimensions();

		setPosition( position );
		return get();
	}

}
