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
public class NLinearInterpolator3D< T extends NumericType< T > > extends NLinearInterpolator< T >
{
	protected NLinearInterpolator3D( final NLinearInterpolator3D< T > interpolator )
	{
		super( interpolator );
	}

	protected NLinearInterpolator3D( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible, type );
	}

	protected NLinearInterpolator3D( final RandomAccessible< T > randomAccessible )
	{
		super( randomAccessible );
	}

	@Override
	final public int numDimensions()
	{
		return 3;
	}

	@Override
	protected void fillWeights()
	{
		final double w0 = position[ 0 ] - target.getLongPosition( 0 );
		final double w0Inv = 1.0d - w0;
		final double w1 = position[ 1 ] - target.getLongPosition( 1 );
		final double w1Inv = 1.0d - w1;
		final double w2 = position[ 2 ] - target.getLongPosition( 2 );
		final double w2Inv = 1.0d - w2;

		weights[ 0 ] = w0Inv * w1Inv * w2Inv;
		weights[ 1 ] = w0 * w1Inv * w2Inv;
		weights[ 2 ] = w0Inv * w1 * w2Inv;
		weights[ 3 ] = w0 * w1 * w2Inv;
		weights[ 4 ] = w0Inv * w1Inv * w2;
		weights[ 5 ] = w0 * w1Inv * w2;
		weights[ 6 ] = w0Inv * w1 * w2;
		weights[ 7 ] = w0 * w1 * w2;
	}

	@Override
	public T get()
	{
		fillWeights();

		accumulator.set( target.get() );
		accumulator.mul( weights[ 0 ] );
		target.fwd( 0 );
		tmp.set( target.get() );
		tmp.mul( weights[ 1 ] );
		accumulator.add( tmp );
		target.fwd( 1 );
		tmp.set( target.get() );
		tmp.mul( weights[ 3 ] );
		accumulator.add( tmp );
		target.bck( 0 );
		tmp.set( target.get() );
		tmp.mul( weights[ 2 ] );
		accumulator.add( tmp );
		target.fwd( 2 );
		tmp.set( target.get() );
		tmp.mul( weights[ 6 ] );
		accumulator.add( tmp );
		target.fwd( 0 );
		tmp.set( target.get() );
		tmp.mul( weights[ 7 ] );
		accumulator.add( tmp );
		target.bck( 1 );
		tmp.set( target.get() );
		tmp.mul( weights[ 5 ] );
		accumulator.add( tmp );
		target.bck( 0 );
		tmp.set( target.get() );
		tmp.mul( weights[ 4 ] );
		accumulator.add( tmp );
		target.bck( 2 );

		return accumulator;
	}

	@Override
	public NLinearInterpolator3D< T > copy()
	{
		return new NLinearInterpolator3D< T >( this );
	}
}
