package tests;

import ij.ImageJ;
import ij.ImagePlus;

import java.awt.Polygon;
import java.awt.Rectangle;

import net.imglib2.algorithm.transformation.ImageTransform;
import net.imglib2.img.ImgCursor;
import net.imglib2.img.ImgRandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.shapelist.ShapeList;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.real.FloatType;
import mpicbg.models.AffineModel3D;
import mpicbg.models.TranslationModel3D;
import mpicbg.util.Timer;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * @version 0.1a
 */
public class TestShapeList
{
	/**
	 * @param args
	 */
	public static < T extends RealType< T > > void main( String[] args )
	{
		
		Timer timer = new Timer();
		
		new ImageJ();
		
		final int depth = 50;
		

		/* Create ShapeList */
		//final ShapeList< FloatType > shapeList = new ShapeListCached<FloatType>( new int[]{ 200, 200, depth },  new FloatType( ) );
		final ShapeList< FloatType > shapeList = new ShapeList<FloatType>( new long[]{ 200, 200, depth },  new FloatType( ( byte )91 ) );
		
		/* add some shapes */
		for ( int i = 0; i < depth; ++i )
		{
			shapeList.addShape( new Rectangle( 10 + i, 20, 40, 70 + 2 * i ), new FloatType( ( byte )64 ), new long[]{ i } );
			shapeList.addShape( new Polygon( new int[]{ 90 + i, 180 - 2 * i, 190 - 4 * i, 120 - 2 * i }, new int[]{ 90, 80 + i, 140 - 3 * i, 130 - 2 * i }, 4 ), new FloatType( ( byte )127 ), new long[]{ i } );
		}
		
		final ImagePlus shapeListImp = ImageJFunctions.displayAsVirtualStack( shapeListImage );
		shapeListImp.show();
		//shapeListImp.getProcessor().setMinAndMax( 0, 255 );
		//shapeListImp.updateAndDraw();
		/* ----------------------------------------------------------------- */


		
		
		/* Copy content into another container */
		
		timer.start();
		final ArrayImgFactory< FloatType > arrayFactory = new ArrayImgFactory< FloatType >();
		final ArrayImg< FloatType, ? > arrayImage = arrayFactory.create( new long[]{ 200, 200, depth }, new FloatType() );
		final ImgCursor< FloatType > cArray = arrayImage.localizingCursor();
		final ImgRandomAccess< FloatType > cShapeList = shapeList.randomAccess();
		while ( cArray.hasNext() )
		{
			cArray.fwd();
			cShapeList.setPosition( cArray );
			cArray.get().set( cShapeList.get() );
		}

		arrayImage.getDisplay().setMinMax();
		final ImagePlus arrayImp = ImageJFunctions.displayAsVirtualStack( arrayImage );
		arrayImp.show();
		//arrayImp.getProcessor().setMinAndMax( 0, 255 );
		//arrayImp.updateAndDraw();
		System.out.println( "Copying into an ArrayContainer took " + timer.stop() + " ms." );
		/* ----------------------------------------------------------------- */
		
		
		
		/* Copy content into another container */
		
		timer.start();
		final CellImgFactory cellFactory = new CellImgFactory();
		final Image< FloatType > cellImage = new ImageFactory< FloatType >( new FloatType(), cellFactory ).createImage( new int[]{ 200, 200, depth }, "CellContainer" );
		final ImgCursor< FloatType > cCell = cellImage.createLocalizingRasterIterator();
		while ( cCell.hasNext() )
		{
			cCell.fwd();
			cShapeList.moveTo( cCell );
			cCell.get().set( cShapeList.get() );
			cCell.get().mul( cCell.getArrayIndex() / 1000.0f );
		}

//		cellImage.getDisplay().setMinMax();
//		final ImagePlus cellImp = ImageJFunctions.displayAsVirtualStack( cellImage );
//		cellImp.show();
//		//arrayImp.getProcessor().setMinAndMax( 0, 255 );
//		//arrayImp.updateAndDraw();
//		System.out.println( "Copying into a CellContainer took " + timer.stop() + " ms." );
//		/* ----------------------------------------------------------------- */
		

		/* Copy content rotated into another container */
		timer.start();
		final AffineModel3D affine = new AffineModel3D();
		affine.set(
				0.7660444f, -0.6427875f, 0.0f, 0.0f,
				0.6330221f, 0.75440645f, -0.17364818f, 0.0f,
				0.111618884f, 0.1330222f, 0.9848077f, 0.0f );
		final TranslationModel3D translation = new TranslationModel3D();
		translation.set( 100, 0, -15 );
		
		affine.preConcatenate( translation );
		
		//final ImageTransform<FloatType> transform = new ImageTransform<FloatType>( shapeListImage, affine, new LinearInterpolatorFactory<FloatType>( new OutOfBoundsStrategyValueFactory<FloatType>() ) );
		//final ImageTransform<FloatType> transform = new ImageTransform<FloatType>( arrayImage, affine, new LinearInterpolatorFactory<FloatType>( new OutOfBoundsStrategyValueFactory< FloatType >( new FloatType( ( byte )255 ) ) ) );
		//final ImageTransform<FloatType> transform = new ImageTransform<FloatType>( arrayImage, affine, new LinearInterpolatorFactory<FloatType>( new OutOfBoundsMirrorSingleBoundaryFactory<FloatType>() ) );
		final ImageTransform< FloatType > transform =
			new ImageTransform< FloatType >(
					arrayImage,
					affine,
//					new LinearInterpolatorFactory< FloatType >(
					new NearestNeighborInterpolatorFactory< FloatType >(
//							new OutOfBoundsMirrorFactory< FloatType >( true ) ) );
							new OutOfBoundsConstantValueFactory< FloatType >( new FloatType( ( byte )112 ) ) ) );
//							new OutOfBoundsStrategyPeriodicFactory< FloatType >() ) );
		transform.setOutputImageFactory(
				new ImageFactory< FloatType >( new FloatType(), cellFactory ) );
		
		if ( !transform.checkInput() || !transform.process() )
		{
			System.out.println( transform.getErrorMessage() );
			return;
		}
		
		final Image<FloatType> rotatedCellImage = transform.getResult();
		
		/*
		final Image< FloatType > cellImage = new ImageFactory< FloatType >( new FloatType(), cellFactory ).createImage( new int[]{ 200, 200, depth }, "Rotated CellContainer" );
		final LocalizableCursor< FloatType > cCell = cellImage.createLocalizableCursor();
		
		final int[] iLocation = new int[ cellImage.getNumDimensions() ];
		final float[] fLocation = new float[ cellImage.getNumDimensions() ];
		
		while ( cCell.hasNext() )
		{
			cCell.fwd();
			cCell.getPosition( iLocation );
			
			for ( int d = 0; d < iLocation.length; ++d )
				fLocation[ d ] = iLocation[ d ];
			
			try { affine.applyInverseInPlace( fLocation ); }
			catch ( final NoninvertibleModelException e ){}
			
			for ( int d = 0; d < iLocation.length; ++d )
				iLocation[ d ] = Math.round( fLocation[ d ] );
			
			cShapeList.setPosition( iLocation );
			//cShapeList.moveTo( iLocation );
			
			try { cCell.getType().set( cShapeList.getType() ); }
			catch ( final IndexOutOfBoundsException e ){}
		}
		*/
		
		rotatedCellImage.getDisplay().setMinMax();
		final ImagePlus rotatedCellImp = ImageJFunctions.displayAsVirtualStack( rotatedCellImage );
		rotatedCellImp.show();
		//cellImp.getProcessor().setMinAndMax( 0, 255 );
		//cellImp.updateAndDraw();
		System.out.println( "Transforming with an AffineModel3D took " + timer.stop() + " ms." );
		/* ----------------------------------------------------------------- */

		
		shapeListImp.updateAndDraw();
		arrayImp.updateAndDraw();
		//cellImp.updateAndDraw();
		
		
		try
		{
			Thread.sleep( 1000 );
		}
		catch ( final InterruptedException e ){}
	}
}
