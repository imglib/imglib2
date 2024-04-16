/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.util;

/**
 * Static utility methods to convert from a value to a bin, useful for dealing
 * with histograms and LUTs.
 * 
 * @author Aivar Grislis
 */
public class Binning
{

	// -- Utility methods --

	/**
	 * Convert value to bin number.
	 * <p>
	 * This variant is inclusive, it assigns all values to the range
	 * 0..(bins-1).
	 * 
	 * @param bins
	 * @param min
	 * @param max
	 * @param value
	 * @return bin number 0...(bins-1)
	 */
	public static int valueToBin( final int bins, final double min, final double max, final double value )
	{
		int bin = exclusiveValueToBin( bins, min, max, value );
		bin = Math.max( bin, 0 );
		bin = Math.min( bin, bins - 1 );
		return bin;
	}

	/**
	 * Convert value to bin number.
	 * <p>
	 * This variant is exclusive, not all values map to the range 0...(bins-1).
	 */
	public static int exclusiveValueToBin( final int bins, final double min, final double max, final double value )
	{
		int bin;
		if ( max != min )
		{
			if ( value != max )
			{
				// convert in-range values to 0.0...1.0
				final double temp = ( value - min ) / ( max - min );

				// note multiply by bins, not (bins - 1)
				// note floor is needed so that small negative values go to -1
				bin = ( int ) Math.floor( temp * bins );
			}
			else
			{
				// value == max, special case, otherwise 1.0 * bins is bins
				bin = bins - 1;
			}
		}
		else
		{
			// max == min, degenerate case
			bin = bins / 2;
		}
		return bin;
	}

	/**
	 * Returns array of left edge values for each bin.
	 */
	public static double[] edgeValuesPerBin( final int bins, final double min, final double max )
	{
		final double[] edgeValues = new double[ bins ];

		for ( int i = 0; i < bins; ++i )
		{
			edgeValues[ i ] = min + ( max - min ) * i / bins;
		}
		return edgeValues;
	}

	/**
	 * Returns array of center values for each bin.
	 */
	public static double[] centerValuesPerBin( final int bins, final double min, final double max )
	{
		final double[] edgeValues = edgeValuesPerBin( bins, min, max );
		final double[] centerValues = new double[ bins ];

		// average the edge values to get centers
		for ( int i = 0; i < bins - 1; ++i )
		{
			centerValues[ i ] = ( edgeValues[ i ] + edgeValues[ i + 1 ] ) / 2;
		}

		// special case for last bin
		centerValues[ bins - 1 ] = ( edgeValues[ bins - 1 ] + max ) / 2;

		return centerValues;
	}
}
