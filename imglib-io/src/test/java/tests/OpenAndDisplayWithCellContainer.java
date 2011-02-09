package tests;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.display.ARGBScreenImage;
import mpicbg.imglib.display.RealARGBConverter;
import mpicbg.imglib.display.XYProjector;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class OpenAndDisplayWithCellContainer
{
	public static < T extends Type< T > > void copyLocalizing (Img< T > src, Img< T > dst)
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
	
	public static < T extends Type< T > > void copyIterating (Img< T > src, Img< T > dst)
	{
		if ( ! src.equalIterationOrder( dst ) ) {
			System.err.println( "src and dst do not have compatible iteration order" );
			return;
		}

		final Cursor< T > srcCursor = src.cursor();
		final Cursor< T > dstCursor = dst.cursor();
		
		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			dstCursor.fwd();
			dstCursor.get().set( srcCursor.get() );
		}
		
	}
	
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		Img< FloatType > img = LOCI.openLOCIFloatType( "/home/tobias/workspace/imglibworkshop/DrosophilaWing.tif",  new CellContainerFactory<FloatType>( new int[] {64, 64} ) );

		final Img< FloatType > copy = img.factory().create( img, new FloatType() );
		copyLocalizing (img, copy);

		final Img< FloatType > copy2 = img.factory().create( img, new FloatType() );
		copyIterating( img, copy2 );

		Img< FloatType > finalImg = copy2;

		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )finalImg.dimension( 0 ), ( int )finalImg.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( finalImg, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
		
		projector.map();

		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.updateAndDraw();
	}
}
