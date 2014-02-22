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

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;

/**
 * TODO
 * 
 * @author dietzc, hornm, zinsmaierm (University of Konstanz)
 * 
 * @param <T>
 *            TODO
 */
public final class MinMaxWithSaturation< T extends RealType< T >> implements UnaryOutputOperation< IterableInterval< T >, ValuePair< T, T >>
{

	private double m_saturation;

	private MakeHistogram< T > m_histOp;

	public MinMaxWithSaturation( double saturation, T type )
	{
		m_saturation = saturation;

		if ( saturation != 0 )
		{

			int bins;
			if ( !( type.getMaxValue() < Integer.MAX_VALUE ) )
			{
				bins = Short.MAX_VALUE * 2;
			}
			else
			{
				bins = ( int ) ( type.getMaxValue() - type.getMinValue() + 1 );
			}

			m_histOp = new MakeHistogram< T >( bins );
		}
	}

	public MinMaxWithSaturation()
	{
		this( 0, null );
	}

	@Override
	public ValuePair< T, T > compute( IterableInterval< T > op, ValuePair< T, T > r )
	{

		if ( m_saturation == 0 )
		{
			final Iterator< T > it = op.iterator();
			r.a.setReal( r.a.getMaxValue() );
			r.b.setReal( r.b.getMinValue() );
			while ( it.hasNext() )
			{
				T i = it.next();
				if ( r.a.compareTo( i ) > 0 )
					r.a.set( i );
				if ( r.b.compareTo( i ) < 0 )
					r.b.set( i );
			}
		}
		else
		{
			calcMinMaxWithSaturation( op, r, m_histOp.compute( op, m_histOp.compute( op ) ) );
		}

		return r;
	}

	private void calcMinMaxWithSaturation( IterableInterval< T > interval, ValuePair< T, T > r, Histogram1d< T > hist )
	{
		long histMin = 0, histMax;
		int threshold = ( int ) ( interval.size() * m_saturation / 200.0 );

		// find min
		int pCount = 0;
		for ( int i = 0; i < hist.getBinCount(); i++ )
		{
			pCount += hist.frequency( i );
			if ( pCount > threshold )
			{
				histMin = i;
				break;
			}
		}

		// find max
		pCount = 0;
		histMax = hist.getBinCount() - 1;
		for ( long i = hist.getBinCount() - 1; i >= 0; i-- )
		{
			pCount += hist.frequency( i );
			if ( pCount > threshold )
			{
				histMax = i;
				break;
			}
		}
		r.a.setReal( ( histMin * ( ( r.a.getMaxValue() - r.a.getMinValue() ) / hist.getBinCount() ) ) + r.a.getMinValue() );
		r.b.setReal( ( histMax * ( ( r.a.getMaxValue() - r.a.getMinValue() ) / hist.getBinCount() ) ) + r.a.getMinValue() );
	}

	@Override
	public UnaryOutputOperation< IterableInterval< T >, ValuePair< T, T >> copy()
	{
		return new MinMaxWithSaturation< T >();
	}

	@Override
	public ValuePair< T, T > createEmptyOutput( IterableInterval< T > in )
	{
		final T t = in.iterator().next();
		return new ValuePair< T, T >( t.createVariable(), t.createVariable() );
	}

	@Override
	public ValuePair< T, T > compute( IterableInterval< T > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}
}
