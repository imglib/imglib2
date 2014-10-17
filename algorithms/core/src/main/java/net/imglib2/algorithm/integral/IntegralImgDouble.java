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

package net.imglib2.algorithm.integral;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Special implementation for double using the basic type to sum up the
 * individual lines.
 * 
 * @param <R>
 * @author Stephan Preibisch
 */
public class IntegralImgDouble< R extends NumericType< R > > extends IntegralImg< R, DoubleType >
{

	public IntegralImgDouble( final RandomAccessibleInterval< R > img, final DoubleType type, final Converter< R, DoubleType > converter )
	{
		super( img, type, converter );
	}

	@Override
	protected void integrateLineDim0( final Converter< R, DoubleType > converter, final RandomAccess< R > cursorIn, final RandomAccess< DoubleType > cursorOut, final DoubleType sum, final DoubleType tmpVar, final long size )
	{
		// compute the first pixel
		converter.convert( cursorIn.get(), sum );
		cursorOut.get().set( sum );

		double sum2 = sum.get();

		for ( int i = 2; i < size; ++i )
		{
			cursorIn.fwd( 0 );
			cursorOut.fwd( 0 );

			converter.convert( cursorIn.get(), tmpVar );
			sum2 += tmpVar.get();
			cursorOut.get().set( sum2 );
		}
	}

	@Override
	protected void integrateLine( final int d, final RandomAccess< DoubleType > cursor, final DoubleType sum, final long size )
	{
		// init sum on first pixel that is not zero
		double sum2 = cursor.get().get();

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum2 += cursor.get().get();
			cursor.get().set( sum2 );
		}
	}

}
