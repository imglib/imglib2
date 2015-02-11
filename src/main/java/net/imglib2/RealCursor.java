/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
 * A RealCursor iterates over a set of RealLocalizable elements, for example
 * intensity values sampled at a finite set of arbitrary real positions.
 * 
 * <p>
 * RealCursor is a combination of several interfaces to achieve this. The
 * {@link Iterator} interface is used to iterate the set. Use
 * {@link Iterator#fwd()} to advance the cursor and {@link Iterator#hasNext()}
 * to check whether there are more elements. Note, that the Cursor starts
 * <em>before</em> the first element, i.e., you have to call {@code fwd()} once
 * to move to the first element.
 * </p>
 * 
 * <p>
 * The {@link RealLocalizable} interface provides access to the position of the
 * current element. The {@link Sampler#get()} method of the {@link Sampler}
 * interface provides access to the value of the current element.
 * </p>
 * 
 * <p>
 * For convenience, Cursor also extends the {@link java.util.Iterator} interface
 * so that you are able to use Cursors in for-each loops. Calling the
 * {@link java.util.Iterator#next()} method is equivalent to calling
 * {@code fwd()} and {@code get()}. That is, after {@code next()} the Cursor is
 * on the element returned by {@code next()}. {@code get()} can be used to
 * obtain that element (again), and {@code getPosition()} to obtain its
 * position. The {@link java.util.Iterator#remove()} method is not supported by
 * imglib Cursors, in general.
 * </p>
 * 
 * <saalfeld@mpi-cbg.de>
 * 
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public interface RealCursor< T > extends RealLocalizable, Sampler< T >, Iterator, java.util.Iterator< T >
{
	// NB: Ideally, we would utilize covariant inheritance to narrow the return
	// type of a single copy() method here, rather than needing separate methods
	// copy(), copyCursor(), copyRandomAccess() and copyRealRandomAccess().
	// Unfortunately, due to a Javac bug with multiple interface inheritance,
	// we must avoid doing so for now. For details, see:
	// http://bugs.sun.com/view_bug.do?bug_id=6656332
	// The bug is fixed in JDK7.
	public RealCursor< T > copyCursor();
//@Override
//public RealCursor< T > copy();
}
