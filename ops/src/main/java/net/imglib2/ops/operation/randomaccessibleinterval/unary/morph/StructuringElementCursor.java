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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.morph;

import java.util.ArrayList;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.iterable.unary.Max;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.IntegerType;

/**
 * Something like a neighborhood cursor. Neighborhood is defined a long[][] of
 * offsets.
 * 
 * @author Felix Schoenenberger (University of Konstanz)
 * 
 * @param <T>
 */
public class StructuringElementCursor< T extends Type< T >> extends AbstractCursor< T >
{

	// TODO:
	public static < I extends IntegerType< I >> long[][] createElementFromImg( Img< I > kernel )
	{
		final int max = new Max< I, I >().compute( kernel.cursor(), kernel.firstElement().createVariable() ).getInteger();
		final long[] origin = new long[ kernel.numDimensions() ];
		boolean customOrigin = false;
		if ( max > 1 )
		{
			Cursor< I > c = kernel.cursor();
			while ( c.hasNext() )
			{
				if ( c.next().getInteger() > 1 )
				{
					c.localize( origin );
					customOrigin = true;
					break;
				}
			}
		}
		if ( max == 1 || !customOrigin )
		{
			kernel.dimensions( origin );
			for ( int i = 0; i < origin.length; i++ )
			{
				origin[ i ] = origin[ i ] / 2;
			}
		}
		long[] off = new long[ kernel.numDimensions() ];
		ArrayList< long[] > offsets = new ArrayList< long[] >();
		Cursor< I > c = kernel.cursor();
		while ( c.hasNext() )
		{
			if ( c.next().getInteger() > 0 )
			{
				c.localize( off );
				for ( int i = 0; i < off.length; i++ )
				{
					off[ i ] = off[ i ] - origin[ i ];
				}
				offsets.add( off.clone() );
			}
		}
		long[][] struc = new long[ offsets.size() ][];
		offsets.toArray( struc );
		return struc;
	}

	public static boolean is4Connected( long[][] struct )
	{
		for ( int i = 0; i < struct.length; i++ )
		{
			if ( i > 0 && struct[ i - 1 ].length != struct[ i ].length )
				return false;
			double sum = 0;
			for ( int j = 0; j < struct.length; j++ )
			{
				sum += struct[ i ][ j ] * struct[ i ][ j ];
			}
			if ( Math.sqrt( sum ) > 1 )
				return false;
		}
		return struct.length == 2 * struct[ 0 ].length + 1;
	}

	public static boolean is8Connected( long[][] struct )
	{
		for ( int i = 0; i < struct.length; i++ )
		{
			if ( i > 0 && struct[ i - 1 ].length != struct[ i ].length )
				return false;
			double sum = 0;
			for ( int j = 0; j < struct.length; j++ )
			{
				sum += struct[ i ][ j ] * struct[ i ][ j ];
			}
			if ( Math.sqrt( sum ) > Math.sqrt( 2 ) )
				return false;
		}
		return struct.length == ( int ) Math.pow( 3, struct[ 0 ].length );
	}

	private final RandomAccess< T > m_access;

	private final long[] m_center;

	private final long[][] m_struc;

	private final int m_numStrucDimensions;

	private int m_i;

	public StructuringElementCursor( RandomAccessible< T > accessible, Img< BitType > structuringElement )
	{
		this( accessible, createElementFromImg( structuringElement ) );
	}

	public StructuringElementCursor( RandomAccess< T > access, Img< BitType > structuringElement )
	{
		this( access, createElementFromImg( structuringElement ) );
	}

	public StructuringElementCursor( RandomAccessible< T > accessible, StructuringElementCursor< ? > structuringCursor )
	{
		this( accessible, structuringCursor.m_struc );
	}

	public StructuringElementCursor( RandomAccess< T > access, StructuringElementCursor< ? > structuringCursor )
	{
		this( access, structuringCursor.m_struc );
	}

	public StructuringElementCursor( RandomAccessible< T > accessible, long[][] structuringElement )
	{
		this( accessible.randomAccess(), structuringElement );
	}

	public StructuringElementCursor( RandomAccess< T > access, long[][] structuringElement )
	{
		super( access.numDimensions() );
		m_access = access;
		m_center = new long[ access.numDimensions() ];
		m_struc = structuringElement;
		m_numStrucDimensions = m_struc[ 0 ].length;
	}

	public void relocate( Localizable center )
	{
		center.localize( m_center );
		reset();
	}

	public void relocate( long[] center )
	{
		System.arraycopy( center, 0, m_center, 0, m_center.length );
		reset();
	}

	@Override
	public T get()
	{
		return m_access.get();
	}

	@Override
	public void fwd()
	{
		++m_i;
		for ( int i = 0; i < m_numStrucDimensions; i++ )
		{
			m_access.setPosition( m_center[ i ] + m_struc[ m_i ][ i ], i );
		}
	}

	@Override
	public void reset()
	{
		m_access.setPosition( m_center );
		m_i = -1;
	}

	@Override
	public boolean hasNext()
	{
		return m_i < m_struc.length - 1;
	}

	@Override
	public void localize( long[] position )
	{
		m_access.localize( position );
	}

	@Override
	public long getLongPosition( int d )
	{
		return m_access.getLongPosition( d );
	}

	@Override
	public AbstractCursor< T > copy()
	{
		return copyCursor();
	}

	@Override
	public AbstractCursor< T > copyCursor()
	{
		return new StructuringElementCursor< T >( m_access.copyRandomAccess(), m_struc );
	}
}
