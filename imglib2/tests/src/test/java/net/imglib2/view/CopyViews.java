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

package net.imglib2.view;

import ij.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class CopyViews
{
	public static < T extends Type< T > > void copy( RandomAccessible< T > src, RandomAccessibleInterval< T > dst )
	{
		final RandomAccessibleIntervalCursor< T > dstCursor = new RandomAccessibleIntervalCursor< T >( dst );
		final RandomAccess< T > srcAccess = src.randomAccess( dst );		
		while ( dstCursor.hasNext() )
		{
			dstCursor.fwd();
			srcAccess.setPosition( dstCursor );
			dstCursor.get().set( srcAccess.get() );
		}	
	}

	public static < T extends Type< T > > void copySrc( RandomAccessibleInterval< T > src, RandomAccessible< T > dst )
	{
		final RandomAccessibleIntervalCursor< T > srcCursor = new RandomAccessibleIntervalCursor< T >( src );
		final RandomAccess< T > dstAccess = dst.randomAccess( src );		
		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			dstAccess.setPosition( srcCursor );
			dstAccess.get().set( srcCursor.get() );
		}	
	}

	final static public void main( final String[] args )
	{
		new ImageJ();
		ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();

		Img< FloatType > inputImg = null;
		try
		{
			final ImgOpener io = new ImgOpener();
			inputImg = io.openImg( "/home/tobias/workspace/data/wingclip.tif", imgFactory, new FloatType() );
			//inputImg = io.openImg( ImgIOUtils.cacheId( "http://www.wv.inf.tu-dresden.de/~tobias/wingclip.tif" ), imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		final long w = inputImg.dimension( 0 );
		final long h = inputImg.dimension( 1 );

		final long[] dim = new long[] { w * 2, h * 2 };
		Img< FloatType > outputImg = imgFactory.create( dim, new FloatType() );
		
		copy( inputImg, Views.superIntervalView( outputImg, new long[] {0,0}, new long[] {w,h} ) );
		copy( Views.flippedView( inputImg, 0 ), Views.superIntervalView( outputImg, new long[] {w,0}, new long[] {w,h} ) );
		copy( Views.flippedView( inputImg, 1 ), Views.superIntervalView( outputImg, new long[] {0,h}, new long[] {w,h} ) );
		copy( Views.flippedView( Views.flippedView( inputImg, 1 ), 0 ), Views.superIntervalView( outputImg, new long[] {w,h}, new long[] {w,h} ) );

		ImageJFunctions.show( outputImg );

//		final long w = inputImg.dimension( 0 );
//		final long h = inputImg.dimension( 1 );
//
//		final long[] dim = new long[] { w * 5, h * 5 };
//		Img< FloatType > outputImg = imgFactory.create( dim, new FloatType() );
//		
//		copySrc(
//				Views.interval( inputImg, new long[] {50,0}, new long[] {100, 100} ), 
//				Views.offsetInterval( outputImg, new long[] {2 * w, 2 * h}, new long[] {1, 1} ) );
//		copySrc( Views.rotate( 
//				Views.interval( inputImg, new long[] {50,0}, new long[] {100, 100} ), 
//			0, 1 ), Views.offsetInterval( outputImg, new long[] {2 * w, 2 * h}, new long[] {1, 1} ) );
//		copySrc( Views.rotate( 
//				Views.interval( inputImg, new long[] {50,0}, new long[] {100, 100} ), 
//			1, 0 ), Views.offsetInterval( outputImg, new long[] {2 * w, 2 * h}, new long[] {1, 1} ) );

//		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )outputImg.dimension( 0 ), ( int )outputImg.dimension( 1 ) );
//		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( outputImg, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
//		projector.map();
//
//		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
//		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
//		imp.show();		
	}
}
