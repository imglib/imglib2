/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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
