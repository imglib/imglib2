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

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Provides a view (read-only) of the underlying data mapped to a display range.
 * 
 * Maps a given value to the desired range within [0, T.getMaxValue()] delimited by [min, max].
 * In other words, any value within [min, max] is mapped to [0, T.getMaxValue()],
 * with values below min becoming zero, and values above max becoming T.getMaxValue().
 * 
 * The operations are performed in the O type, with a {@link DoubleType} being recommended.
 * 
 * @author Albert Cardona
 */
public class LinearRangeTypedOpConverter< T extends RealType< T >, O extends RealType< O > > implements Converter< T, T >
{
	private final T min, max, var;
	private final O op, range, maxValue;
	private final Converter< O, T > fromOp;
	private final Converter< T, O > toOp;

	/**
	 * @param min The start of the display range.
	 * @param max The end of the display range.
	 * @param op The type to use for computing the operation; DoubleType recommended.
	 * @param toOp A function to convert from T to O.
	 * @param fromOp A function to convert from O to T.
	 */
	public LinearRangeTypedOpConverter(
			final T min,
			final T max,
			final O op,
			final Converter< T, O > toOp,
			final Converter< O, T > fromOp )
	{
		this.min = min.copy();
		this.max = max.copy();
		this.var = min.createVariable();
		this.op = op.createVariable();
		this.toOp = toOp;
		this.fromOp = fromOp;
		this.range = op.createVariable();
		final T rangeT = max.copy();
		rangeT.sub( min );
		this.toOp.convert( rangeT, this.range );
		this.maxValue = op.createVariable();
		this.maxValue.setReal( max.getMaxValue() );
	}
	
	/**
	 * Convenience method to instantiate with DoubleType-based operations.
	 * 
	 * @param min The start of the display range.
	 * @param max The end of the display range.
	 * @return LinearDisplayRangeConverter2< T, DoubleType >
	 */
	static public < T extends RealType< T > > LinearRangeTypedOpConverter< T, DoubleType > create( final T min, final T max )
	{
		return new LinearRangeTypedOpConverter< T, DoubleType >(
				min,
				max,
				new DoubleType(),
				new Converter< T, DoubleType >()
				{
					@Override
					public final void convert( final T input, final DoubleType output )
					{
						output.setReal( input.getRealDouble() );
					}
				},
				new Converter< DoubleType, T >()
				{
					@Override
					public final void convert( final DoubleType input, final T output )
					{
						output.setReal( input.getRealDouble() );
					}
				});
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
		
		// Compute the fraction within the min-max range and multiply by the max value of T
		this.var.set( input );
		this.var.sub( this.min );
		this.toOp.convert( this.var, this.op );
		this.op.div( this.range );
		this.op.mul( this.maxValue );
		this.fromOp.convert( this.op, output );
	}
}