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

package net.imglib2.converter;

import net.imglib2.type.numeric.RealType;

/** 
 * Maps an input to the desired range within [0, T.getMaxValue()] delimited by [min, max].
 * In other words, any value within [min, max] is mapped to [0, T.getMaxValue()],
 * with values below min becoming zero, and values above max becoming T.getMaxValue().
 * 
 * @author Albert Cardona
 */
public class LinearRangeConverter< T extends RealType< T > > implements Converter< T, T >
{
	private final T min, max;
	private final double minVal, range;

	/**
	 * @param min The start of the display range.
	 * @param max The end of the display range.
	 */
	public LinearRangeConverter(
			final T min,
			final T max )
	{
		this.min = min;
		this.max = max;
		this.minVal = min.getRealDouble();
		this.range = max.getRealDouble() - this.minVal;
	}

	@Override
	public void convert( final T input, final T output ) {
		
		if ( input.compareTo( this.min ) <= 0 )
		{
			output.setZero();
			return;
		}
		
		if ( input.compareTo( this.max ) >= 0 )
		{
			output.setReal( input.getMaxValue() );
			return;
		}
		
		// Compute the fraction within the min-max range and multiply by the max value of the type
		output.setReal( ( ( input.getRealDouble() - this.minVal ) / this.range ) * input.getMaxValue() );
	}
}
