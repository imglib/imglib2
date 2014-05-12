/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
 * Implementation of the {@link RealInterval} interface.
 * 
 * 
 * @author Stephan Preibisch
 */
public class FinalRealInterval extends AbstractRealInterval
{
	/**
	 * Creates a new {@link AbstractRealInterval} using an existing
	 * {@link RealInterval}
	 * 
	 * @param interval
	 */
	public FinalRealInterval( final RealInterval interval )
	{
		super( interval );
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 * 
	 * @param min
	 * @param max
	 */
	public FinalRealInterval( final double[] min, final double[] max )
	{
		super( min, max );
	}

	/**
	 * Create a {@link FinalRealInterval} from a parameter list comprising
	 * minimum coordinates and size. For example, to create a 2D interval from
	 * (10, 10) to (20, 40) use createMinSize( 10, 10, 11, 31 ).
	 * 
	 * @param minsize
	 *            a list of <em>2*n</em> parameters to create a <em>n</em>
	 *            -dimensional interval. The first <em>n</em> parameters specify
	 *            the minimum of the interval, the next <em>n</em> parameters
	 *            specify the dimensions of the interval.
	 * @return interval with the specified boundaries
	 */
	public static FinalRealInterval createMinSize( final double... minsize )
	{
		final int n = minsize.length / 2;
		final double[] min = new double[ n ];
		final double[] max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = minsize[ d ];
			max[ d ] = min[ d ] + minsize[ d + n ] - 1;
		}
		return new FinalRealInterval( min, max );
	}

	/**
	 * Create a {@link FinalRealInterval} from a parameter list comprising
	 * minimum and maximum coordinates. For example, to create a 2D interval
	 * from (10, 10) to (20, 40) use createMinMax( 10, 10, 20, 40 ).
	 * 
	 * @param minmax
	 *            a list of <em>2*n</em> parameters to create a <em>n</em>
	 *            -dimensional interval. The first <em>n</em> parameters specify
	 *            the minimum of the interval, the next <em>n</em> parameters
	 *            specify the maximum of the interval.
	 * @return interval with the specified boundaries
	 */
	public static FinalRealInterval createMinMax( final double... minmax )
	{
		final int n = minmax.length / 2;
		final double[] min = new double[ n ];
		final double[] max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = minmax[ d ];
			max[ d ] = minmax[ d + n ];
		}
		return new FinalRealInterval( min, max );
	}
}
