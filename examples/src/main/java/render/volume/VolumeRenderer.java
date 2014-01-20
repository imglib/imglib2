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
/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package render.volume;

import ij.IJ;
import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.Cursor;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.ARGBDoubleARGBConverter;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgs;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.meta.ImgPlus;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.InvertibleRealTransformSequence;
import net.imglib2.realtransform.Perspective3D;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.realtransform.Scale3D;
import net.imglib2.realtransform.Translation3D;
import net.imglib2.type.numeric.ARGBDoubleType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.AbstractARGBDoubleType;
import net.imglib2.type.numeric.NativeARGBDoubleType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import net.imglib2.view.composite.CompositeIntervalView;
import net.imglib2.view.composite.RealComposite;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class VolumeRenderer
{
	final static double bg = 0;
	final static ARGBDoubleType bgARGB = new ARGBDoubleType( 1, 0, 0, 0 );
	
	final static int numFrames = 360;
	final static int stepSize = 1;
	
	static protected < T extends NumericType< ? > > void render(
			final RandomAccessible< T > volume,
			final RandomAccessibleInterval< T > canvas,
			final long minZ,
			final long maxZ,
			final RowAccumulator< T > accumulator )
	{
		final RandomAccess< T > pixel = canvas.randomAccess( canvas );
		final RandomAccess< T > poxel = volume.randomAccess();
		
		pixel.setPosition( canvas.min( 0 ), 0 );
		pixel.setPosition( canvas.min( 1 ), 0 );

		poxel.setPosition( pixel.getLongPosition( 0 ), 0 );
		poxel.setPosition( pixel.getLongPosition( 1 ), 1 );
		poxel.setPosition( maxZ, 2 );
		
		while ( pixel.getLongPosition( 1 ) <= canvas.max( 1 ) )
		{
			pixel.setPosition( canvas.min( 0 ), 0 );
			poxel.setPosition( pixel.getLongPosition( 0 ), 0 );
			while ( pixel.getLongPosition( 0 ) <= canvas.max( 0 ) )
			{
				poxel.setPosition( maxZ, 2 );
				accumulator.accumulateRow( pixel.get(), poxel, minZ, maxZ, stepSize, 2 );
				
				pixel.fwd( 0 );
				poxel.fwd( 0 );
			}
			
			pixel.fwd( 1 );
			poxel.fwd( 1 );
		}
	}
	
	static protected < T extends AbstractARGBDoubleType< T > > void renderARGBDouble(
			final RandomAccessible< T > volume,
			final RandomAccessibleInterval< ARGBType > canvas,
			final long minZ,
			final long maxZ,
			final RowAccumulator< T > accumulator )
	{
		final RandomAccess< ARGBType > pixel = canvas.randomAccess( canvas );
		final RandomAccess< T > poxel = volume.randomAccess();
		final T accumulate = volume.randomAccess().get().createVariable();
		
		pixel.setPosition( canvas.min( 0 ), 0 );
		pixel.setPosition( canvas.min( 1 ), 0 );

		poxel.setPosition( pixel.getLongPosition( 0 ), 0 );
		poxel.setPosition( pixel.getLongPosition( 1 ), 1 );
		poxel.setPosition( maxZ, 2 );
		
		while ( pixel.getLongPosition( 1 ) <= canvas.max( 1 ) )
		{
			pixel.setPosition( canvas.min( 0 ), 0 );
			poxel.setPosition( pixel.getLongPosition( 0 ), 0 );
			while ( pixel.getLongPosition( 0 ) <= canvas.max( 0 ) )
			{
				poxel.setPosition( maxZ, 2 );
				accumulate.set( bgARGB.getA(), bgARGB.getR(), bgARGB.getG(), bgARGB.getB() );
				accumulator.accumulateRow( accumulate, poxel, minZ, maxZ, stepSize, 2 );
				pixel.get().set( accumulate.toARGBInt() );
				
				pixel.fwd( 0 );
				poxel.fwd( 0 );
			}
			
			pixel.fwd( 1 );
			poxel.fwd( 1 );
		}
	}
	
	final static double accelerate( final double x )
	{
		return 0.5 - 0.5 * Math.cos( Math.PI * x );
	}
	
	public static void test1() throws ImgIOException
	{
		new ImageJ();
		final String filename = "./l1-cns.tif";
		//final String filename = "/home/saalfeld/tmp/valia/tassos/7.tif";
		
		long t;
		
		IJ.log( "Opening ZIP with ImgOpener ..." );
		t = System.currentTimeMillis();
		final ImgPlus< FloatType > img = new ImgOpener().openImg( filename + ".zip", new ArrayImgFactory< FloatType >(), new FloatType() );
		t = System.currentTimeMillis() - t;
		IJ.log( "  took " +  t + "ms" );
		
		ImageJFunctions.show( img );
		
		final ImagePlusImg< FloatType, ? > movie = ImagePlusImgs.floats( img.dimension( 0 ), img.dimension( 1 ), numFrames );
		ImageJFunctions.show( movie );
		
		final AffineTransform3D centerShift = new AffineTransform3D();
		centerShift.set(
				1, 0, 0, -img.dimension( 0 ) / 2.0 - img.min( 0 ),
				0, 1, 0, -img.dimension( 1 ) / 2.0 - img.min( 1 ),
				0, 0, 1, -img.dimension( 2 ) / 2.0 - img.min( 2 ) );
		
		final AffineTransform3D centerUnshiftXY = centerShift.inverse();
		centerUnshiftXY.set( 0, 2, 3 );
		
		final double f = img.dimension( 1 );
		
		final AffineTransform3D zShift = new AffineTransform3D();
		zShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, img.dimension( 2 ) / 2.0 + f );
		
		final AffineTransform3D rotation = new AffineTransform3D();
		final AffineTransform3D affine = new AffineTransform3D();
		
		final Perspective3D perspective = Perspective3D.getInstance();
		final Scale scale = new Scale( f, f, 1 );
		
		final InvertibleRealTransformSequence transformSequence = new InvertibleRealTransformSequence();
		
		/* rotation */
		transformSequence.add( affine );
		
		/* camera */
		transformSequence.add( perspective );
		transformSequence.add( scale );
		transformSequence.add( centerUnshiftXY );
		
		final ExtendedRandomAccessibleInterval< FloatType, ImgPlus< FloatType > > extendedImg = Views.extendValue( img, img.firstElement().createVariable() );
		final RealRandomAccessible< FloatType > interpolant = Views.interpolate( extendedImg, new NLinearInterpolatorFactory< FloatType >() );
//		final RealRandomAccessible< FloatType > interpolant = Views.interpolate( extendedImg, new NearestNeighborInterpolatorFactory< FloatType >() );
		final RandomAccessible< FloatType > rotated = RealViews.transform( interpolant, transformSequence );
		
		final AlphaIntensityLayers< FloatType > accumulator = new AlphaIntensityLayers< FloatType >( 1.0 / 4095.0, 0 );
//		final AlphaIntensityLayers< FloatType > accumulator = new AlphaIntensityLayers< FloatType >( 1.0 / 5000.0, -500 );
//		final AlphaIntensityLayers< FloatType > accumulator = new AlphaIntensityLayers< FloatType >( 1.0 / 0.2, -0.01 );
		
		for ( int i = 0; i < numFrames; ++i )
		{
			final double j = ( double )i / numFrames;
			//final double k = Math.max( 0, Math.min( 1, j * 1.5 - 0.25 ) );
			final double l = accelerate( j );
			
			
			affine.set(
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0 );
			
			rotation.set( affine );
			
			rotation.rotate( 0, -l * Math.PI * 2 * 2 );
			rotation.rotate( 1, j * Math.PI * 2 );
			
			affine.preConcatenate( centerShift );
			affine.preConcatenate( rotation );
			affine.preConcatenate( zShift );
		
			final FinalRealInterval bounds = affine.estimateBounds( img );
			final long minZ	= ( long )Math.floor( bounds.realMin( 2 ) );
			final long maxZ	= ( long )Math.ceil( bounds.realMax( 2 ) );
			
			System.out.println( "minZ = " + minZ + "; maxZ = " + maxZ );
			
			//final ArrayImg< FloatType, ? > canvas = ArrayImgs.floats( img.dimension( 0 ), img.dimension( 1 ) );
			final RandomAccessibleInterval< FloatType > canvas = Views.hyperSlice( movie, 2, i );
		
			render( rotated, canvas, minZ, maxZ, accumulator );
		}
	}
	
	public static void test2() throws ImgIOException
	{
		new ImageJ();
		
		final double s = 1.0 / 4095.0;
		
		//final String filename = "/home/saalfeld/examples/l1-cns-05-05-5-DPX-9.tif";
		final String filename = "/home/saalfeld/examples/l1-cns-05-05-5-DPX-9-10.tif";
		final ImgPlus< UnsignedShortType > xycz = new ImgOpener().openImg( filename, new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType() );
		final RandomAccessibleInterval< UnsignedShortType > xyzc = Views.permute( xycz, 2, 3 );
		final CompositeIntervalView< UnsignedShortType, RealComposite< UnsignedShortType > > img =
				Views.collapseReal( xyzc );
		
		/* composing converter */
		final RealCompositeARGBDoubleConverter< UnsignedShortType > composite2ARGBDouble =
				new RealCompositeARGBDoubleConverter< UnsignedShortType >( ( int )xyzc.dimension( 3 ) );
		
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 1, s, 0, 0 ), 0 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 0.35, s, s, s ), 1 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 0, s, s, s ), 2 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 1, 0, s, 0 ), 3 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 1, 0, 0, s ), 4 );
		
		final RandomAccessibleInterval< ARGBDoubleType > argbComposite = Converters.convert( img, composite2ARGBDouble, new ARGBDoubleType() );
		
		/* copy it as on-the-fly conversion isn't the quickest thing in the world */
		
		final RandomAccessibleInterval< ARGBType > argb = Converters.convert(
				argbComposite,
				new ARGBDoubleARGBConverter< ARGBDoubleType >(),
				new ARGBType() );
		ImageJFunctions.show( argb );
		
		
		final ImagePlusImg< ARGBType, ? > movie = ImagePlusImgs.argbs( xycz.dimension( 0 ), xycz.dimension( 1 ), numFrames );
		ImageJFunctions.show( movie );
				
		final AffineTransform3D centerShift = new AffineTransform3D();
		centerShift.set(
				1, 0, 0, -xyzc.dimension( 0 ) / 2.0 - xyzc.min( 0 ),
				0, 1, 0, -xyzc.dimension( 1 ) / 2.0 - xyzc.min( 1 ),
				0, 0, 1, -xyzc.dimension( 2 ) / 2.0 - xyzc.min( 2 ) );
		
		final AffineTransform3D centerUnshiftXY = centerShift.inverse();
		centerUnshiftXY.set( 0, 2, 3 );
		
		final double f = xyzc.dimension( 1 );
		
		final AffineTransform3D zShift = new AffineTransform3D();
		zShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, xyzc.dimension( 2 ) / 2.0 + f );
		
		final AffineTransform3D rotation = new AffineTransform3D();
		final AffineTransform3D affine = new AffineTransform3D();
		
		final Perspective3D perspective = Perspective3D.getInstance();
		final Scale scale = new Scale( f, f, 1 );
		
		final InvertibleRealTransformSequence transformSequence = new InvertibleRealTransformSequence();
		
		/* rotation */
		transformSequence.add( affine );
		
		/* camera */
		transformSequence.add( perspective );
		transformSequence.add( scale );
		transformSequence.add( centerUnshiftXY );
		
		final RealRandomAccessible< ARGBDoubleType > interpolant = Views.interpolate( argbComposite, new NLinearInterpolatorFactory< ARGBDoubleType >() );
//		final RealRandomAccessible< ARGBDoubleType > interpolant = Views.interpolate( argbComposite, new NearestNeighborInterpolatorFactory< ARGBDoubleType >() );
		final RandomAccessible< ARGBDoubleType > rotated = RealViews.transform( interpolant, transformSequence );

//		final RealRandomAccessible< NativeARGBDoubleType > interpolant = Views.interpolate( Views.extendZero( argbCopy ), new NLinearInterpolatorFactory< NativeARGBDoubleType >() );
//		final RealRandomAccessible< NativeARGBDoubleType > interpolant = Views.interpolate( Views.extendZero( argbCopy ), new NearestNeighborInterpolatorFactory< NativeARGBDoubleType >() );
//		final RandomAccessible< NativeARGBDoubleType > rotated = RealViews.transform( interpolant, transformSequence );
		
//		final ARGBDoubleLayers< NativeARGBDoubleType > accumulator = new ARGBDoubleLayers();
		final ARGBDoubleLayers< ARGBDoubleType > accumulator = new ARGBDoubleLayers< ARGBDoubleType >();
		
		for ( int i = 44; i < numFrames; ++i )
		{
			final double j = ( double )i / numFrames;
			//final double k = Math.max( 0, Math.min( 1, j * 1.5 - 0.25 ) );
			final double l = accelerate( j );
			
			
			affine.set(
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0 );
			
			rotation.set( affine );
			
			rotation.rotate( 0, -l * Math.PI * 2 * 2 );
			rotation.rotate( 1, j * Math.PI * 2 );
			
			affine.preConcatenate( centerShift );
			affine.preConcatenate( rotation );
			affine.preConcatenate( zShift );
		
			final FinalRealInterval bounds = affine.estimateBounds( img );
			final long minZ	= ( long )Math.floor( bounds.realMin( 2 ) );
			final long maxZ	= ( long )Math.ceil( bounds.realMax( 2 ) );
			
			System.out.println( "minZ = " + minZ + "; maxZ = " + maxZ );
			
			final RandomAccessibleInterval< ARGBType > canvas = Views.hyperSlice( movie, 2, i );
		
			renderARGBDouble( rotated, canvas, minZ, maxZ, accumulator );
		}
	}
	
	public static void test3() throws ImgIOException
	{
		new ImageJ();
		
		final double s = 10.0 / 4095.0;
		final double a = 0.5;
		
		final String filename = "/home/saalfeld/examples/l1-cns-05-05-5-DPX-9.tif";
		//final String filename = "/home/saalfeld/examples/l1-cns-05-05-5-DPX-9-10.tif";
		final ImgPlus< UnsignedShortType > xycz = new ImgOpener().openImg( filename, new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType() );
		final RandomAccessibleInterval< UnsignedShortType > xyzc = Views.permute( xycz, 2, 3 );
		final CompositeIntervalView< UnsignedShortType, RealComposite< UnsignedShortType > > img =
				Views.collapseReal( xyzc );
		
		/* composing converter */
		final RealCompositeARGBDoubleConverter< UnsignedShortType > composite2ARGBDouble =
				new RealCompositeARGBDoubleConverter< UnsignedShortType >( ( int )xyzc.dimension( 3 ) );
		
		
		composite2ARGBDouble.setARGB( new ARGBDoubleType( a, s, 0, 0 ), 0 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 0.35 * a, s, s, s ), 1 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( 0, s, s, s ), 2 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( a, 0, s, 0 ), 3 );
		composite2ARGBDouble.setARGB( new ARGBDoubleType( a, 0, 0, s ), 4 );
		
		final RandomAccessibleInterval< ARGBDoubleType > argbComposite = Converters.convert( img, composite2ARGBDouble, new ARGBDoubleType() );
		
		/* copy it as on-the-fly conversion isn't the quickest thing in the world */
		final Img< NativeARGBDoubleType > argbCopy;
		if ( xyzc.dimension( 0 ) * xyzc.dimension( 1 ) * xyzc.dimension( 2 ) * 4 > Integer.MAX_VALUE )
			argbCopy = new CellImgFactory< NativeARGBDoubleType >( 256 ).create( img, new NativeARGBDoubleType() );
		else
			argbCopy = new ArrayImgFactory< NativeARGBDoubleType >().create( img, new NativeARGBDoubleType() );
		
		final IterableInterval< ARGBDoubleType > sourceIterable = Views.flatIterable( argbComposite );
		final IterableInterval< NativeARGBDoubleType > targetIterable = Views.flatIterable( argbCopy );
		final Cursor< ARGBDoubleType > sourceCursor = sourceIterable.cursor();
		final Cursor< NativeARGBDoubleType > targetCursor = targetIterable.cursor();
		while ( targetCursor.hasNext() )
			targetCursor.next().set( sourceCursor.next() );
		
		final RandomAccessibleInterval< ARGBType > argb = Converters.convert(
				( RandomAccessibleInterval< NativeARGBDoubleType > )argbCopy,
				new ARGBDoubleARGBConverter< NativeARGBDoubleType >(),
				new ARGBType() );
		ImageJFunctions.show( argb );
		
		
		
		
		final ImagePlusImg< ARGBType, ? > movie = ImagePlusImgs.argbs( xycz.dimension( 0 ), xycz.dimension( 1 ), numFrames );
		ImageJFunctions.show( movie );
				
		final Translation3D centerShift = new Translation3D(
				-xyzc.dimension( 0 ) / 2.0 - xyzc.min( 0 ),
				-xyzc.dimension( 1 ) / 2.0 - xyzc.min( 1 ),
				-xyzc.dimension( 2 ) / 2.0 - xyzc.min( 2 ) );
		
		final Translation3D centerUnshiftXY = new Translation3D(
				xyzc.dimension( 0 ) / 2.0 + xyzc.min( 0 ),
				xyzc.dimension( 1 ) / 2.0 + xyzc.min( 1 ),
				0 );
		
		final double f = xyzc.dimension( 1 );
		
		final Translation3D zShift = new Translation3D( 0, 0, xyzc.dimension( 2 ) / 2.0 + f );
		
		final AffineTransform3D rotation = new AffineTransform3D();
		final AffineTransform3D affine = new AffineTransform3D();
		
		final Perspective3D perspective = Perspective3D.getInstance();
		final Scale3D scale = new Scale3D( f, f, 1 );
		
		final InvertibleRealTransformSequence transformSequence = new InvertibleRealTransformSequence();
		
		/* rotation */
		transformSequence.add( affine );
		
		/* camera */
		transformSequence.add( perspective );
		transformSequence.add( scale );
		transformSequence.add( centerUnshiftXY );
		
//		final RealRandomAccessible< ARGBDoubleType > interpolant = Views.interpolate( argbComposite, new NLinearInterpolatorFactory< ARGBDoubleType >() );
//		final RealRandomAccessible< ARGBDoubleType > interpolant = Views.interpolate( argbComposite, new NearestNeighborInterpolatorFactory< ARGBDoubleType >() );
//		final RandomAccessible< ARGBDoubleType > rotated = RealViews.transform( interpolant, transformSequence );

		final RealRandomAccessible< NativeARGBDoubleType > interpolant = Views.interpolate( Views.extendZero( argbCopy ), new NLinearInterpolatorFactory< NativeARGBDoubleType >() );
//		final RealRandomAccessible< NativeARGBDoubleType > interpolant = Views.interpolate( Views.extendZero( argbCopy ), new NearestNeighborInterpolatorFactory< NativeARGBDoubleType >() );
		final RandomAccessible< NativeARGBDoubleType > rotated = RealViews.transform( interpolant, transformSequence );
		
		final ARGBDoubleLayers< NativeARGBDoubleType > accumulator = new ARGBDoubleLayers< NativeARGBDoubleType >();
		
		for ( int i = 0; i < numFrames; ++i )
		{
			final double j = ( double )i / numFrames;
			//final double k = Math.max( 0, Math.min( 1, j * 1.5 - 0.25 ) );
			final double l = accelerate( j );
			
			
			affine.set(
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0 );
			
			rotation.set( affine );
			
			rotation.rotate( 0, -l * Math.PI * 2 * 2 );
			rotation.rotate( 1, j * Math.PI * 2 );
			
			affine.preConcatenate( centerShift );
			affine.preConcatenate( rotation );
			affine.preConcatenate( zShift );
		
			final FinalRealInterval bounds = affine.estimateBounds( img );
			final long minZ	= ( long )Math.floor( bounds.realMin( 2 ) );
			final long maxZ	= ( long )Math.ceil( bounds.realMax( 2 ) );
			
			System.out.println( "minZ = " + minZ + "; maxZ = " + maxZ );
			
//			final ArrayImg< FloatType, ? > canvas = ArrayImgs.floats( img.dimension( 0 ), img.dimension( 1 ) );
			final RandomAccessibleInterval< ARGBType > canvas = Views.hyperSlice( movie, 2, i );
		
//			renderARGBDouble( rotated, canvas, minZ, maxZ, accumulator );
			renderARGBDouble( rotated, canvas, minZ, maxZ, accumulator );
		}
	}
	
	final static public void main( final String[] args ) throws ImgIOException
	{
		test3();
	}
}
