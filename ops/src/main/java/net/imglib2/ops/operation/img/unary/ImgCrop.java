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

package net.imglib2.ops.operation.img.unary;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RealInterval;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

/**
 * Crops an image.
 * 
 * @author dietzc, hornm, University of Konstanz
 */
public class ImgCrop< T extends Type< T >> implements UnaryOperation< Img< T >, Img< T >>
{

	/**
	 * The interval to be cropped
	 */
	private final Interval m_interval;

	/**
	 * Crops the intervaled defined by origin and extend
	 * 
	 * @param origin
	 *            origin of the interval
	 * 
	 * @param extend
	 *            extend of the interval
	 */
	public ImgCrop( long[] origin, long[] extend )
	{
		long[] max = new long[ extend.length ];
		for ( int i = 0; i < max.length; i++ )
		{
			max[ i ] = origin[ i ] + extend[ i ] - 1;
		}
		m_interval = new FinalInterval( origin, max );
	}

	/**
	 * Crops the intervaled defined by origin and extend
	 * 
	 * @param origin
	 *            origin of the interval
	 * 
	 * @param extend
	 *            extend of the interval
	 */
	public ImgCrop( int[] origin, int[] extend )
	{
		long[] max = new long[ extend.length ];
		long[] loffset = new long[ extend.length ];
		for ( int i = 0; i < max.length; i++ )
		{
			max[ i ] = origin[ i ] + extend[ i ] - 1;
			loffset[ i ] = origin[ i ];
		}
		m_interval = new FinalInterval( loffset, max );
	}

	/**
	 * Crops an interval
	 * 
	 * @param interval
	 */
	public ImgCrop( Interval interval )
	{
		m_interval = interval;
	}

	/**
	 * 
	 * @param interval
	 * @param imgFac
	 */
	public ImgCrop( RealInterval interval )
	{
		long[] min = new long[ interval.numDimensions() ];
		long[] max = new long[ interval.numDimensions() ];
		for ( int i = 0; i < max.length; i++ )
		{
			min[ i ] = ( long ) Math.floor( interval.realMin( i ) );
			max[ i ] = ( long ) Math.ceil( interval.realMax( i ) );
		}
		m_interval = new FinalInterval( min, max );
	}

	@Override
	public Img< T > compute( Img< T > op, Img< T > r )
	{
		Cursor< T > rc = r.localizingCursor();
		RandomAccess< T > opc = op.randomAccess();
		while ( rc.hasNext() )
		{
			rc.next();
			for ( int d = 0; d < m_interval.numDimensions(); d++ )
			{
				opc.setPosition( rc.getLongPosition( d ) + m_interval.min( d ), d );
			}
			rc.get().set( opc.get() );
		}

		return r;
	}

	@Override
	public UnaryOperation< Img< T >, Img< T >> copy()
	{
		return new ImgCrop< T >( m_interval );
	}
}
