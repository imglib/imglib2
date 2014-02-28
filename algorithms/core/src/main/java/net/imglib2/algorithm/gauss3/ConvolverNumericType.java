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

package net.imglib2.algorithm.gauss3;

import java.lang.reflect.Array;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.NumericType;

/**
 * A 1-dimensional line convolver that operates on all {@link NumericType}. It
 * implemented using a shifting window buffer that is stored in a T[] array.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @see ConvolverFactory
 * 
 * @param <T>
 *            input and output type
 */
public class ConvolverNumericType< T extends NumericType< T > > implements Runnable
{
	/**
	 * @return a {@link ConvolverFactory} producing {@link ConvolverNumericType}
	 *         .
	 */
	public static < T extends NumericType< T > > ConvolverNumericTypeFactory< T > factory( final T type )
	{
		return new ConvolverNumericTypeFactory< T >( type );
	}

	public static final class ConvolverNumericTypeFactory< T extends NumericType< T > > implements ConvolverFactory< T, T >
	{
		final private T type;

		public ConvolverNumericTypeFactory( final T type )
		{
			this.type = type;
		}

		@Override
		public Runnable create( final double[] halfkernel, final RandomAccess< T > in, final RandomAccess< T > out, final int d, final long lineLength )
		{
			return new ConvolverNumericType< T >( halfkernel, in, out, d, lineLength, type );
		}
	}

	final private double[] kernel;

	final private RandomAccess< T > in;

	final private RandomAccess< T > out;

	final private int d;

	final private int k;

	final private int k1;

	final private int k1k1;

	final private long linelen;

	final T[] buf;

	final T tmp;

	@SuppressWarnings( "unchecked" )
	private ConvolverNumericType( final double[] kernel, final RandomAccess< T > in, final RandomAccess< T > out, final int d, final long lineLength, final T type )
	{
		this.kernel = kernel;
		this.in = in;
		this.out = out;
		this.d = d;

		k = kernel.length;
		k1 = k - 1;
		k1k1 = k1 + k1;
		linelen = lineLength;

		final int buflen = 2 * k - 1;
		buf = ( T[] ) Array.newInstance( type.getClass(), buflen );
		for ( int i = 0; i < buflen; ++i )
			buf[ i ] = type.createVariable();

		tmp = type.createVariable();
	}

	private void next()
	{
		// move buf contents down
		final T first = buf[ 0 ];
		for ( int i = 0; i < k1k1; ++i )
			buf[ i ] = buf[ i + 1 ];
		buf[ k1k1 ] = first;

		// add new values
		final T w = in.get();

		// center
		tmp.set( w );
		tmp.mul( kernel[ 0 ] );
		buf[ k1 ].add( tmp );

		// loop
		for ( int j = 1; j < k1; ++j )
		{
			tmp.set( w );
			tmp.mul( kernel[ j ] );
			buf[ k1 + j ].add( tmp );
			buf[ k1 - j ].add( tmp );
		}

		// outer-most
		tmp.set( w );
		tmp.mul( kernel[ k1 ] );
		buf[ k1k1 ].set( tmp );

		in.fwd( d );
	}

	@Override
	public void run()
	{
		for ( int i = 0; i < k1k1; ++i )
			next();
		for ( long i = 0; i < linelen; ++i )
		{
			next();
			tmp.add( buf[ 0 ] );
			out.get().set( tmp );
			out.fwd( d );
		}
	}
}
