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

package net.imglib2.img.subimg;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * Helper class to create a subview on an {@link Img} which behaves exactly as
 * an {@link Img}.
 * 
 * @author Tobias Pietzsch, Christian Dietz
 */
public class SubImg< T extends Type< T > > extends IterableRandomAccessibleInterval< T > implements Img< T >
{
	/**
	 * View on interval of a source img. If needed, dims with size one can be
	 * removed.
	 * 
	 * @param srcImg
	 *            Source img for the view
	 * @param interval
	 *            Interval which will be used for to create this view
	 * @param keepDimsWithSizeOne
	 *            If false, dimensions with size one will be virtually removed
	 *            from the resulting view
	 * @return
	 */
	public static final < T extends Type< T > > RandomAccessibleInterval< T > getView( final RandomAccessibleInterval< T > srcImg, final Interval interval, final boolean keepDimsWithSizeOne )
	{
		if ( !Util.contains( srcImg, interval ) )
			throw new IllegalArgumentException( "In SubImgs the interval min and max must be inside the dimensions of the SrcImg" );

		RandomAccessibleInterval< T > slice = Views.offsetInterval( srcImg, interval );
		if ( !keepDimsWithSizeOne )
			for ( int d = interval.numDimensions() - 1; d >= 0; --d )
				if ( interval.dimension( d ) == 1 && slice.numDimensions() > 1 )
					slice = Views.hyperSlice( slice, d, 0 );
		return slice;
	}

	// src img
	private final Img< T > m_srcImg;

	// origin of source img
	private final long[] m_origin;

	/**
	 * SubImg is created. View on {@link Img} which is defined by a given
	 * Interval, but still is an {@link Img}
	 * 
	 * @param srcImg
	 *            Source img for the view
	 * @param interval
	 *            Interval which will be used for to create this view
	 * @param keepDimsWithSizeOne
	 *            If false, dimensions with size one will be virtually removed
	 *            from the resulting view
	 */
	public SubImg( final Img< T > srcImg, final Interval interval, final boolean keepDimsWithSizeOne )
	{
		super( getView( srcImg, interval, keepDimsWithSizeOne ) );
		m_srcImg = srcImg;
		m_origin = new long[ interval.numDimensions() ];
		interval.min( m_origin );
	}

	/**
	 * Origin in the source img
	 * 
	 * @param origin
	 */
	public final void getOrigin( final long[] origin )
	{
		for ( int d = 0; d < origin.length; d++ )
			origin[ d ] = m_origin[ d ];
	}

	/**
	 * Origin of dimension d in the source img
	 */
	public final long getOrigin( final int d )
	{
		return m_origin[ d ];
	}

	/**
	 * @return Source image
	 */
	public Img< T > getImg()
	{
		return m_srcImg;
	}

	@Override
	public ImgFactory< T > factory()
	{
		return m_srcImg.factory();
	}

	@Override
	public Img< T > copy()
	{
		final Img< T > copy = m_srcImg.factory().create( this, m_srcImg.firstElement() );

		Cursor< T > srcCursor = localizingCursor();
		RandomAccess< T > resAccess = copy.randomAccess();

		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			resAccess.setPosition( srcCursor );
			resAccess.get().set( srcCursor.get() );

		}

		return copy;
	}
}
