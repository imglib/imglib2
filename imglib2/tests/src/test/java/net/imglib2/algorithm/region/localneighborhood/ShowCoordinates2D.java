package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.Type;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Helper class to debug neighborhoods.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class ShowCoordinates2D
{
	public static class CoordinateType extends Point implements Type< CoordinateType >
	{
		public CoordinateType( final Localizable l )
		{
			super( l );
		}

		public CoordinateType( final int n )
		{
			super( n );
		}

		public CoordinateType( final long... position )
		{
			super( position );
		}

		@Override
		public CoordinateType createVariable()
		{
			return new CoordinateType( numDimensions() );
		}

		@Override
		public CoordinateType copy()
		{
			return new CoordinateType( this );
		}

		@Override
		public void set( final CoordinateType c )
		{
			setPosition( c );
		}
	}

	public static void main( final String[] args )
	{
		final int n = 2;
		final long[] dimensions = new long[] { 5, 5 };
		final ImgFactory< CoordinateType > f = new ListImgFactory< CoordinateType >();
		final CoordinateType type = new CoordinateType( n );
		final Img< CoordinateType > img = f.create( dimensions, type );
		final Cursor< CoordinateType > c = img.localizingCursor();
		while ( c.hasNext() )
			c.next().setPosition( c );
		// c.reset();
		// while ( c.hasNext() )
		// System.out.println( c.next() );

//		final Point center = new Point( 2l, 2l );
//		final LocalNeighborhood2< CoordinateType > neighborhood = new LocalNeighborhood2< CoordinateType >( img, center );
//		final Cursor< CoordinateType > nc = neighborhood.cursor();
//		while ( nc.hasNext() )
//			System.out.println( nc.next() );

		final Interval span = Intervals.createMinMax( -1, -1, 1, 1 );
		final Cursor< Neighborhood< CoordinateType > > n3 = new RectangleNeighborhoodCursor< CoordinateType >( Views.interval( img, Intervals.expand( img, -1 ) ), span, RectangleNeighborhoodSkipCenter.< CoordinateType >factory() );
		while ( n3.hasNext() )
		{
			for ( final CoordinateType t : n3.next() )
				System.out.println( t );
			System.out.println( "-----" );
		}
	}
}
