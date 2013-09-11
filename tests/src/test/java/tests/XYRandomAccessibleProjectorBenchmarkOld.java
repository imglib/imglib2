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

package tests;

import ij.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.projectors.AbstractProjector2D;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class XYRandomAccessibleProjectorBenchmarkOld
{
	final Img< UnsignedByteType > img;

	final Img< ARGBType > argbImg;

	public XYRandomAccessibleProjectorBenchmarkOld( final String filename ) throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		img = new ImgOpener().openImg( filename, factory, new UnsignedByteType() );
		argbImg = new ArrayImgFactory< ARGBType >().create( img, new ARGBType() );
		convert( img, argbImg );

		ImageJFunctions.show( argbImg );
	}

	public void convert( final Img< UnsignedByteType > in, final Img< ARGBType > out )
	{
		final XYRandomAccessibleProjector< UnsignedByteType, ARGBType > projector = new XYRandomAccessibleProjector< UnsignedByteType, ARGBType >( in, out, new RealARGBConverter< UnsignedByteType >(0, 1000) );
		for ( int iteration = 0; iteration < 10; ++iteration )
		{
			final long start = System.currentTimeMillis();
			for ( int i = 0; i < 50; ++i )
				projector.map();
			final long end = System.currentTimeMillis();
			System.out.println( ( end - start ) + " ms (iteration " + iteration + ")" );
		}
	}

	public static void main( final String[] args ) throws IncompatibleTypeException, ImgIOException
	{
		new ImageJ();
		new XYRandomAccessibleProjectorBenchmarkOld( "DrosophilaWing.tif" );
	}

/**
 * TODO
 *
 * @author ImgLib2 developers
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
	class XYRandomAccessibleProjector< A, B > extends AbstractProjector2D< A, B >
	{
		final protected RandomAccessibleInterval< B > target;
		private RandomAccessible<A> source;
		private Converter<? super A, B> converter;
	
		public XYRandomAccessibleProjector( final RandomAccessible< A > source, final RandomAccessibleInterval< B > target, final Converter< ? super A, B > converter )
		{
			super( source.numDimensions() );
			this.target = target;
			this.source = source;
			this.converter = converter;
		}
	
		@Override
		public void map()
		{
			for ( int d = 2; d < position.length; ++d )
				min[ d ] = max[ d ] = position[ d ];
	
			min[ 0 ] = target.min( 0 );
			min[ 1 ] = target.min( 1 );
			max[ 0 ] = target.max( 0 );
			max[ 1 ] = target.max( 1 );
			final FinalInterval sourceInterval = new FinalInterval( min, max );
	
			final long cr = -target.dimension( 0 );
	
			final RandomAccess< B > targetRandomAccess = target.randomAccess( target );
			final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
	
			final long width = target.dimension( 0 );
			final long height = target.dimension( 1 );
	
			sourceRandomAccess.setPosition( min );
			targetRandomAccess.setPosition( min[ 0 ], 0 );
			targetRandomAccess.setPosition( min[ 1 ], 1 );
			for ( long y = 0; y < height; ++y )
			{
				for ( long x = 0; x < width; ++x )
				{
					converter.convert( sourceRandomAccess.get(), targetRandomAccess.get() );
					sourceRandomAccess.fwd( 0 );
					targetRandomAccess.fwd( 0 );
				}
				sourceRandomAccess.move( cr, 0 );
				targetRandomAccess.move( cr, 0 );
				sourceRandomAccess.fwd( 1 );
				targetRandomAccess.fwd( 1 );
			}
		}
	}
}
