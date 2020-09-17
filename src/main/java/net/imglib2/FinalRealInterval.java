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

import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

import java.util.Arrays;

/**
 * Implementation of the {@link RealInterval} interface.
 *
 *
 * @author Stephan Preibisch
 */
public final class FinalRealInterval extends AbstractRealInterval
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
	 * @param copy
	 *            flag indicating whether min and max arrays should be duplicated.
	 */
	public FinalRealInterval( final double[] min, final double[] max, final boolean copy )
	{
		super( min, max, copy );
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 *
	 * @param min
	 * @param max
	 */
	public FinalRealInterval( final double[] min, final double[] max )
	{
		this( min, max, true );
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 *
	 * @param min
	 * @param max
	 */
	public FinalRealInterval( final RealLocalizable min, final RealLocalizable max )
	{
		super( min, max );
	}

	/**
	 * THIS METHOD WILL BE REMOVED IN A FUTURE RELEASE. It was mistakenly
	 * introduced, analogous to {@link FinalInterval#createMinSize(long...)} for
	 * integer intervals. Dimension is not defined for {@link RealInterval} and
	 * computing the <em>max</em> as <em>min + dim - 1</em> does not make sense.
	 *
	 * <p>
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
	@Deprecated
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

	/**
	 * Create a {@link FinalRealInterval} that stores its min and max in the provided arrays.
	 *
	 * @param min
	 *            array for storing the position of the first elements in each dimension
	 * @param max
	 *            array for storing the the position of the last elements in each dimension
	 */
	public static FinalRealInterval wrap( final double[] min, final double[] max )
	{
		return new FinalRealInterval( min, max, false );
	}

	@Override
	public boolean equals( final Object obj )
	{
		return obj instanceof FinalRealInterval &&
				Intervals.equals( this, ( RealInterval ) obj, 0.0 );

	}

	@Override
	public int hashCode()
	{
		return Util.combineHash( Arrays.hashCode( min ), Arrays.hashCode( max ) );
	}
}
