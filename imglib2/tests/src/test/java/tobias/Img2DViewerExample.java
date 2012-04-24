package tobias;
import ij.IJ;
import ij.ImageJ;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class Img2DViewerExample< T extends RealType< T > & NativeType< T > >
{
	private final int width = 800;
	private final int height = 600;

	public Img2DViewerExample( final ImgPlus< T > imgPlus, final RealARGBConverter< T > converter )
	{
		final Img< T > img = imgPlus.getImg();
		final T template = img.randomAccess().get().copy();
		final ExtendedRandomAccessibleInterval< T, Img< T > > source = Views.extendValue( img, template );

		double yScale;
		if ( Double.isNaN( imgPlus.calibration( 0 ) ) || Double.isNaN( imgPlus.calibration( 1 ) ) )
			yScale = 1;
		else
			yScale = imgPlus.calibration( 1 ) / imgPlus.calibration( 0 );

		/* un-scale */
		final AffineTransform2D unScale = new AffineTransform2D();
		unScale.set(
			1.0, 0.0, ( width - imgPlus.dimension( 0 ) ) / 2.0,
			0.0, yScale, ( height - imgPlus.dimension( 1 ) * yScale ) / 2.0 );

		/* center shift */
		final AffineTransform2D centerShift = new AffineTransform2D();
		centerShift.set(
				1, 0, -width / 2.0,
				0, 1, -height / 2.0 );

		/* center un-shift */
		final AffineTransform2D centerUnShift = new AffineTransform2D();
		centerUnShift.set(
				1, 0, width / 2.0,
				0, 1, height / 2.0 );

		/* initialize rotation */
		final AffineTransform2D rotation = new AffineTransform2D();
		rotation.rotate( 0.05 );

		unScale.preConcatenate( centerShift );
		unScale.preConcatenate( rotation );
		unScale.preConcatenate( centerUnShift );

		new AbstractInteractive2DViewer< T >( width, height, source, converter, unScale );
	}

	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		final ImgOpener io = new ImgOpener();
		final ImgPlus< UnsignedByteType > imgPlus;
		try
		{
			imgPlus = io.openImg( "src/test/java/resources/preikestolen.tif", new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		}
		catch ( final ImgIOException e )
		{
			IJ.log( "Problems opening the image, check the error msg." );
			e.printStackTrace();
			return;
		}
		new Img2DViewerExample< UnsignedByteType >( imgPlus, new RealARGBConverter< UnsignedByteType >( 0, 255 ) );
	}

}
