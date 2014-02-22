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

package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.histogram.Histogram1d;
import net.imglib2.histogram.Real1dBinMapper;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;

/**
 * 
 * @author dietzc (University of Konstanz)
 * 
 * @param <T>
 */
public final class MakeHistogram< T extends RealType< T >> implements UnaryOutputOperation< Iterable< T >, Histogram1d< T > >
{

	private final int m_numBins;

	private final boolean m_calculateMinMax;

	public MakeHistogram()
	{
		this( -1, false );
	}

	public MakeHistogram( int numBins )
	{
		this( numBins, false );
	}

	/**
	 * @param calculateMinMax
	 *            Calculates the real min and max values of the image and uses
	 *            them for creation of the histogram
	 */
	public MakeHistogram( boolean calculateMinMax )
	{
		this( -1, calculateMinMax );
	}

	/**
	 * 
	 * @param numBins
	 * @param calculateMinMax
	 *            Calculates the real min and max values of the image and uses
	 *            them for creation of the histogram
	 */
	public MakeHistogram( int numBins, boolean calculateMinMax )
	{
		m_numBins = numBins;
		m_calculateMinMax = calculateMinMax;
	}

	@Override
	public final Histogram1d< T > createEmptyOutput( Iterable< T > op )
	{
		T type = op.iterator().next().createVariable();

		double min = type.getMinValue();
		double max = type.getMaxValue();

		if ( m_calculateMinMax )
		{
			ValuePair< T, T > minMaxPair = new MinMax< T >().compute( op );
			min = minMaxPair.getA().getRealDouble();
			max = minMaxPair.getB().getRealDouble();
		}

		if ( m_numBins <= 0 )
		{
			return new Histogram1d< T >( new Real1dBinMapper< T >( min, max, 256, false ) );
		}
		else
		{
			return new Histogram1d< T >( new Real1dBinMapper< T >( min, max, m_numBins, false ) );
		}
	}

	@Override
	public final Histogram1d< T > compute( Iterable< T > op, Histogram1d< T > r )
	{
		r.resetCounters();
		r.addData( op );

		return r;
	}

	@Override
	public Histogram1d< T > compute( Iterable< T > op )
	{
		return compute( op, createEmptyOutput( op ) );
	}

	@Override
	public UnaryOutputOperation< Iterable< T >, Histogram1d< T > > copy()
	{
		return new MakeHistogram< T >( m_numBins );
	}
}
