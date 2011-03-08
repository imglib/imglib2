package tests;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.display.ARGBScreenImage;
import mpicbg.imglib.display.RealARGBConverter;
import mpicbg.imglib.display.XYProjector;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.view.ExtendableRandomAccessibleIntervalImp;
import mpicbg.imglib.view.View;
import mpicbg.imglib.view.Views;


public class OpenAndDisplaySliceView
{
	public static < T extends Type< T > > void copy (View< T > src, Img< T > dst)
	{
		final Cursor< T > srcCursor = src.localizingCursor();
		final RandomAccess< T > dstCursor = dst.randomAccess();
		
		int[] position = new int[ src.numDimensions() ];
		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			srcCursor.localize( position );
			dstCursor.setPosition( position );
			dstCursor.get().set( srcCursor.get() );
		}
	}

	final static public void main( final String[] args )
	{
		new ImageJ();
		
		ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
		
		Img< FloatType > img = LOCI.openLOCIFloatType( "/home/tobias/Desktop/73.tif",  new ArrayImgFactory<FloatType>() );

		View< FloatType > view = Views.hyperSlice( Views.view( img ), 0, 50 );

		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )view.dimension( 0 ), ( int )view.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( view, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
		projector.map();

		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();	
	}
}
