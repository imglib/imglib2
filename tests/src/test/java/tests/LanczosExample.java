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

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.util.Timer;
import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.meta.ImgPlus;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class LanczosExample
{	
	final static public void main( final String[] args )
		throws ImgIOException
	{
		new ImageJ();
		
		final ImgOpener io = new ImgOpener();
		//final RandomAccessibleInterval< UnsignedShortType > img = io.openImg( "/home/saalfeld/phd/light-microscopy/presentation/saalfeld-05-05-4-DPX-05_L1_Sum.lsm", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		final ImgPlus< UnsignedShortType > imgPlus = io.openImg( "/home/saalfeld/Desktop/l1-cns.tif", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		
		final double[][] matrix = new double[][]{
				{ 0.5, 0, 0, imgPlus.dimension( 0 ) * 0.25 },
				{ 0, 0.5 * imgPlus.axis( 1 ).calibration() / imgPlus.axis( 0 ).calibration(), 0, imgPlus.dimension( 1 ) * 0.25 },
				{ 0, 0, 0.5 * imgPlus.axis( 0 ).calibration() / imgPlus.axis( 0 ).calibration(), 0 }
		};
//		final AffineTransform affine = new AffineTransform( new Matrix( matrix ) );
		final AffineTransform3D affine = new AffineTransform3D();
		affine.set( matrix[ 0 ][ 0 ], matrix[ 0 ][ 1 ], matrix[ 0 ][ 2 ], matrix[ 0 ][ 3 ], matrix[ 1 ][ 0 ], matrix[ 1 ][ 1 ], matrix[ 1 ][ 2 ], matrix[ 1 ][ 3 ], matrix[ 2 ][ 0 ], matrix[ 2 ][ 1 ], matrix[ 2 ][ 2 ], matrix[ 2 ][ 3 ] );
		
//		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolatorFactory = new NearestNeighborInterpolatorFactory< UnsignedShortType >();
//		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolatorFactory = new NLinearInterpolatorFactory< UnsignedShortType >();
		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolatorFactory = new LanczosInterpolatorFactory< UnsignedShortType >();
		
		final RandomAccessible< UnsignedShortType > extendedImg = Views.extendValue( imgPlus, new UnsignedShortType() );
		final Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolant = new Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > >( extendedImg, interpolatorFactory );
		final AffineRandomAccessible< UnsignedShortType, AffineGet > mapping = new AffineRandomAccessible< UnsignedShortType, AffineGet >( interpolant, affine );
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )imgPlus.dimension( 0 ), ( int )imgPlus.dimension( 1 ) );
		final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( mapping, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
		
		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		
		final Timer timer = new Timer();
		
		projector.setPosition( imgPlus.dimension( 2 ) / 2, 2 );
		
		final AffineTransform3D forward = new AffineTransform3D();
		final AffineTransform3D rotation = new AffineTransform3D();
		final AffineTransform3D scale = new AffineTransform3D();
		scale.set(
				4, 0, 0, 0,
				0, 4, 0, 0,
				0, 0, 4, 0 );
		
		for ( int k = 0; k < 3; ++k )
		{	
			timer.start();
			for ( int i = 45; i < 48; ++i )
			{
				rotation.rotate( 1, Math.PI / 360 );
				forward.set(
						1.0, 0, 0, -imgPlus.dimension( 0 ) / 2.0,
						0, 1.0, 0, -imgPlus.dimension( 1 ) / 2.0,
						0, 0, 1.0, -imgPlus.dimension( 2 ) / 2.0 );
				forward.preConcatenate( scale );
				forward.preConcatenate( rotation );
				forward.set(
						forward.get( 0, 0 ), forward.get( 0, 1 ), forward.get( 0, 2 ), forward.get( 0, 3 ) + imgPlus.dimension( 0 ) / 2.0,
						forward.get( 1, 0 ), forward.get( 1, 1 ), forward.get( 1, 2 ), forward.get( 1, 3 ) + imgPlus.dimension( 1 ) / 2.0,
						forward.get( 2, 0 ), forward.get( 2, 1 ), forward.get( 2, 2 ), forward.get( 2, 3 ) + imgPlus.dimension( 2 ) / 2.0 );
				
				affine.set( forward.inverse() );
				
				projector.map();

				imp.setImage( screenImage.image() );
			}
			IJ.log( "loop " + ( k + 1 ) + ": " + timer.stop() );
		}
		
		final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
		imp.setProcessor( cpa );
		imp.updateAndDraw();
	}
}
