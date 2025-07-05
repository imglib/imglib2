/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.interpolation.randomaccess;

import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.NumericType;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class NLinearInterpolator1D< T extends NumericType< T > > extends NLinearInterpolator< T >
{
	protected NLinearInterpolator1D( final NLinearInterpolator1D< T > interpolator )
	{
		super( interpolator );
	}

	protected NLinearInterpolator1D( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible, type );
	}

	protected NLinearInterpolator1D( final RandomAccessible< T > randomAccessible )
	{
		this( randomAccessible, randomAccessible.getType() );
	}

	@Override
	final public int numDimensions()
	{
		return 1;
	}

	@Override
	public T get()
	{
		final double w0 = position[ 0 ] - target.getLongPosition( 0 );

		accumulator.set( target.get() );
		accumulator.mul( 1.0d - w0 );
		target.fwd( 0 );
		tmp.set( target.get() );
		tmp.mul( w0 );
		accumulator.add( tmp );
		target.bck( 0 );

		return accumulator;
	}

	@Override
	public NLinearInterpolator1D< T > copy()
	{
		return new NLinearInterpolator1D<>( this );
	}
}
