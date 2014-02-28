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

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

/**
 * A 1-dimensional line convolver that operates on all {@link RealType}. It
 * implemented using a line buffer that is stored in a double[] array that is
 * (approximately) as big as one output line. This works for images, where a
 * single line has no more than {@link Integer#MAX_VALUE} elements. For larger
 * images {@link DoubleConvolverRealType} can be used.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @see ConvolverFactory
 * 
 * @param <S>
 *            input type
 * @param <T>
 *            output type
 */
public final class DoubleConvolverRealTypeBuffered< S extends RealType< S >, T extends RealType< T > > implements Runnable
{
	/**
	 * @return a {@link ConvolverFactory} producing
	 *         {@link DoubleConvolverRealTypeBuffered}.
	 */
	public static < S extends RealType< S >, T extends RealType< T > > ConvolverFactory< S, T > factory()
	{
		return new ConvolverFactory< S, T >()
		{
			@Override
			public Runnable create( final double[] halfkernel, final RandomAccess< S > in, final RandomAccess< T > out, final int d, final long lineLength )
			{
				return new DoubleConvolverRealTypeBuffered< S, T >( halfkernel, in, out, d, lineLength );
			}
		};
	}

	final private double[] kernel;

	final private RandomAccess< S > in;

	final private RandomAccess< T > out;

	final private int d;

	final private int k;

	final private int k1;

	final private int k1k1;

	final private int buflen;

	final private double[] buf;

	private DoubleConvolverRealTypeBuffered( final double[] kernel, final RandomAccess< S > in, final RandomAccess< T > out, final int d, final long lineLength )
	{
		this.kernel = kernel;
		this.in = in;
		this.out = out;
		this.d = d;

		k = this.kernel.length;
		k1 = k - 1;
		k1k1 = k1 + k1;

		buflen = ( int ) lineLength + 2 * k1k1;
		buf = new double[ buflen ];
	}

	@Override
	public void run()
	{
		final int max = buflen - k1;
		for ( int i = k1; i < max; ++i )
		{
			final double w = in.get().getRealDouble();

			// center
			buf[ i ] += w * kernel[ 0 ];

			// loop
			for ( int j = 1; j < k1; ++j )
			{
				final double wk = w * kernel[ j ];
				buf[ i + j ] += wk;
				buf[ i - j ] += wk;
			}

			// outer-most
			final double wk = w * kernel[ k1 ];
			buf[ i - k1 ] += wk;
			buf[ i + k1 ] = wk;

			in.fwd( d );
		}

		writeLine();
	}

	private void writeLine()
	{
		final int max = buflen - k1k1;
		for ( int i = k1k1; i < max; ++i )
		{
			out.get().setReal( buf[ i ] );
			out.fwd( d );
		}
	}
}
