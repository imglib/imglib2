package examples;

import gui.Interactive3DViewer;
import ij.IJ;
import ij.ImageJ;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

public class Img3DViewerExample< T extends RealType< T > & NativeType< T > >
{
	private final int width = 400;
	private final int height = 300;

	public Img3DViewerExample( final ImgPlus< T > imgPlus, final RealARGBConverter< T > converter )
	{
		final Img< T > img = imgPlus.getImg();
		final T template = img.randomAccess().get().copy();
		final ExtendedRandomAccessibleInterval< T, Img< T > > source = Views.extendValue( img, template );

		double yScale;
		double zScale;
		if ( Double.isNaN( imgPlus.calibration( 0 ) ) )
			yScale = zScale = 1.0;
		else
		{
			if ( Double.isNaN( imgPlus.calibration( 1 ) ) )
				yScale = 1.0;
			else
				yScale = imgPlus.calibration( 1 ) / imgPlus.calibration( 0 );
			if ( Double.isNaN( imgPlus.calibration( 2 ) ) )
				zScale = 1.0;
			else
				zScale = imgPlus.calibration( 2 ) / imgPlus.calibration( 0 );
		}

		final int d = ( int )img.dimension( 2 );

		final double currentSlice = ( d / 2.0 - 0.5 ) * zScale;

		/* un-scale */
		final AffineTransform3D unScale = new AffineTransform3D();
		unScale.set(
			1.0, 0.0, 0.0, ( width - img.dimension( 0 ) ) / 2.0,
			0.0, yScale, 0.0, ( height - img.dimension( 1 ) * yScale ) / 2.0,
			0.0, 0.0, zScale, 0.0 );

		new Interactive3DViewer< T >( width, height, source, img, converter, unScale, yScale, zScale, currentSlice );
	}

	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		final ImgOpener io = new ImgOpener();
		final ImgPlus< UnsignedShortType > imgPlus;
		try
		{
			imgPlus = io.openImg( "/home/saalfeld/Desktop/l1-cns.tif", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType() );
		}
		catch ( final ImgIOException e )
		{
			IJ.log( "Problems opening the image, check the error msg." );
			e.printStackTrace();
			return;
		}
		new Img3DViewerExample< UnsignedShortType >( imgPlus, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
	}

}
