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

package net.imglib2.ops.data;

import net.imglib2.type.numeric.RealType;

/**
 * @author Felix Schoenenberger (University of Konstanz)
 */
public final class Histogram2D
{

	private final int[] m_hist;

	private final int m_size;

	private final double m_min;

	private final double m_max;

	private final double m_scale;

	public Histogram2D( final int size, final double min, final double max )
	{
		m_size = size;
		m_hist = new int[ size * size ];
		m_min = min;
		m_max = max;
		m_scale = ( m_size - 1 ) / ( m_max - m_min );
	}

	public < T extends RealType< T >> Histogram2D( final int bins, final T type )
	{
		this( bins, type.getMinValue(), type.getMaxValue() );
	}

	public < T extends RealType< T >> Histogram2D( final T type )
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

	public final int numBins()
	{
		return m_size;
	}

	public final double min()
	{
		return m_min;
	}

	public final double max()
	{
		return m_max;
	}

	public final int get( final int x, final int y )
	{
		return m_hist[ y * m_size + x ];
	}

	public final void inc( final int x, final int y )
	{
		++m_hist[ y * m_size + x ];
	}

	public final int getByValue( final double x, final double y )
	{
		return get( valueToBin( x ), valueToBin( y ) );
	}

	public final void incByValue( final double x, final double y )
	{
		inc( valueToBin( x ), valueToBin( y ) );
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
