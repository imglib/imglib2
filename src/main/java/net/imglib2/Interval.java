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
 * <p>
 * {x&isin;Z<sup><em>n</em></sup>|<em>min<sub>d</sub></em>&le;
 * <em>x<sub>d</sub></em>&le;<em>max<sub>d</sub></em>;<em>d</em>&isin;{0&hellip;
 * <em>n</em>-1}}
 * </p>
 * 
 * <p>
 * An {@link Interval} over the discrete source domain. <em>Note</em> that this
 * does <em>not</em> imply that for <em>all</em> coordinates in the
 * {@link Interval} function values exist or can be generated. It only defines
 * where the minimum and maximum source coordinates are. E.g. an
 * {@link IterableInterval} has a limited number of values and a source
 * coordinate for each. By that, minimum and maximum are defined but the
 * {@link Interval} does not define a value for all coordinates in between.
 * </p>
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @author Stephan Preibisch
 */
public interface Interval extends RealInterval, Dimensions
{
	/**
	 * Get the minimum in dimension d.
	 * 
	 * @param d
	 *            dimension
	 * @return minimum in dimension d.
	 */
	public long min( final int d );

	/**
	 * Write the minimum of each dimension into long[].
	 * 
	 * @param min
	 */
	public void min( long[] min );

	/**
	 * Sets a {@link Positionable} to the minimum of this {@link Interval}
	 * 
	 * @param min
	 */
	public void min( Positionable min );

	/**
	 * Get the maximum in dimension d.
	 * 
	 * @param d
	 *            dimension
	 * @return maximum in dimension d.
	 */
	public long max( final int d );

	/**
	 * Write the maximum of each dimension into long[].
	 * 
	 * @param max
	 */
	public void max( long[] max );

	/**
	 * Sets a {@link Positionable} to the maximum of this {@link Interval}
	 * 
	 * @param max
	 */
	public void max( Positionable max );
}
