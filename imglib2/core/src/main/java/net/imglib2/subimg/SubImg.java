package net.imglib2.subimg;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

public class SubImg< T extends Type< T > > extends IterableRandomAccessibleInterval< T > implements Img< T >
{
	private static final < T extends Type< T > > RandomAccessibleInterval< T > getView( final Img< T > srcImg, final Interval interval, final boolean keepDimsWithSizeOne )
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

	private final Img< T > m_srcImg;

	private final long[] m_origin;

	public SubImg( final Img< T > srcImg, final Interval interval, final boolean keepDimsWithSizeOne )
	{
		super( getView( srcImg, interval, keepDimsWithSizeOne ) );
		m_srcImg = srcImg;
		m_origin = new long[ interval.numDimensions() ];
		interval.min( m_origin );
	}

	public final void getOrigin( final long[] origin )
	{
		for ( int d = 0; d < origin.length; d++ )
			origin[ d ] = m_origin[ d ];
	}

	public final long getOrigin( final int d )
	{
		return m_origin[ d ];
	}

	/**
	 * @return
	 */
	public Img< T > getImg()
	{
		return m_srcImg;
	}

	// Img implementation
	@Override
	public ImgFactory< T > factory()
	{
		return m_srcImg.factory();
	}

	@Override
	public Img< T > copy()
	{
		final Img< T > copy = m_srcImg.factory().create( this, m_srcImg.firstElement() );
		// TODO: copy...
		return copy;
	}
}
