/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.type.numeric.RealType;

public final class OpsHistogram
{

	private final int[] m_hist;

	private final int m_bins;

	private final double m_min;

	private final double m_max;

	private final double m_scale;

	public OpsHistogram( final int[] hist, final double min, final double max )
	{
		m_bins = hist.length;
		m_hist = hist;
		m_min = min;
		m_max = max;
		m_scale = ( m_bins - 1 ) / ( m_max - m_min );
	}

	public OpsHistogram( final int bins, final double min, final double max )
	{
		this( new int[ bins ], min, max );
	}

	public < T extends RealType< T >> OpsHistogram( final int bins, final T type )
	{
		this( new int[ bins ], type.getMinValue(), type.getMaxValue() );
	}

	public < T extends RealType< T >> OpsHistogram( final T type )
	{
		this( 256, type );
	}

	public final void clear()
	{
		for ( int i = 0; i < m_hist.length; i++ )
		{
			m_hist[ i ] = 0;
		}
	}

	public final int[] hist()
	{
		return m_hist;
	}

	public final int numBins()
	{
		return m_bins;
	}

	public final double min()
	{
		return m_min;
	}

	public final double max()
	{
		return m_max;
	}

	public final int get( final int i )
	{
		return m_hist[ i ];
	}

	public final void inc( final int i )
	{
		++m_hist[ i ];
	}

	public final int getByValue( final double v )
	{
		return get( valueToBin( v ) );
	}

	public final void incByValue( final double v )
	{
		inc( valueToBin( v ) );
	}

	public final int valueToBin( final double v )
	{
		return ( int ) ( ( v - m_min ) * m_scale );
	}

	public final double binToValue( final int i )
	{
		return ( i / m_scale ) + m_min;
	}

}
