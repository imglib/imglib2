/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
 * The {@link RealLocalizable} interface can localize itself in an n-dimensional
 * real space.
 * 
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public interface RealLocalizable extends EuclideanSpace
{
	/**
	 * Write the current position into the passed array.
	 * 
	 * @param position
	 *            receives current position
	 */
	default void localize( final float[] position )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			position[ d ] = getFloatPosition( d );
	}

	/**
	 * Write the current position into the passed array.
	 *
	 * @param position
	 *            receives current position
	 */
	default void localize( final double[] position )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			position[ d ] = getDoublePosition( d );
	}

	/**
	 * Allocate and return a double array with the position.
	 * 
	 * @return the position
	 */
	default double[] locationToDoubleArray()
	{
		final double[] out = new double[ numDimensions() ];
		localize( out );
		return out;	
	}

	/**
	 * Allocate and return a {@link RealPoint} with the current position.
	 * 
	 * @return the position
	 */
	default RealPoint locationToRealPoint()
	{
		return new RealPoint( this );
	}

	/**
	 * Return the current position in a given dimension.
	 * 
	 * @param d
	 *            dimension
	 * @return dimension of current position
	 */
	default float getFloatPosition( final int d )
	{
		return ( float ) getDoublePosition( d );
	}

	/**
	 * Return the current position in a given dimension.
	 * 
	 * @param d
	 *            dimension
	 * @return dimension of current position
	 */
	public double getDoublePosition( int d );

}
