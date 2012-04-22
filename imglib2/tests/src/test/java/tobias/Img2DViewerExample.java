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
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class Img2DViewerExample< T extends RealType< T > & NativeType< T > > extends AbstractInteractive2DViewer< T >
{
	final private ImgPlus< T > imgPlus;

	private static < T extends Type< T > > ExtendedRandomAccessibleInterval< T, Img< T > > extend( final ImgPlus< T > imgPlus )
	{
		final Img< T > img = imgPlus.getImg();
		final T template = img.randomAccess().get().copy();
		return Views.extendValue( img, template );
	}

	public Img2DViewerExample( final ImgPlus< T > imgPlus, final RealARGBConverter< T > converter )
	{
		super( 800, 600, extend( imgPlus ), converter );
		this.imgPlus = imgPlus;
		run();
	}

	@Override
	public void run()
	{

		double yScale;
		if ( Double.isNaN( imgPlus.calibration( 0 ) ) || Double.isNaN( imgPlus.calibration( 1 ) ) )
			yScale = 1;
		else
			yScale = imgPlus.calibration( 1 ) / imgPlus.calibration( 0 );

		final int w = cp.getWidth();
		final int h = cp.getHeight();

		/* un-scale */
		final AffineTransform2D unScale = new AffineTransform2D();
		unScale.set(
			1.0, 0.0, ( cp.getWidth() - imgPlus.dimension( 0 ) ) / 2.0,
			0.0, yScale, ( cp.getHeight() - imgPlus.dimension( 1 ) * yScale ) / 2.0 );

		/* center shift */
		final AffineTransform2D centerShift = new AffineTransform2D();
		centerShift.set(
				1, 0, -w / 2.0,
				0, 1, -h / 2.0 );

		/* center un-shift */
		final AffineTransform2D centerUnShift = new AffineTransform2D();
		centerUnShift.set(
				1, 0, w / 2.0,
				0, 1, h / 2.0 );

		/* initialize rotation */
		final AffineTransform2D rotation = new AffineTransform2D();
		rotation.rotate( 0.05 );

		unScale.preConcatenate( centerShift );
		unScale.preConcatenate( rotation );
		unScale.preConcatenate( centerUnShift );

		list.clear();
		list.add( unScale );
		list.add( affine );

		projector = createProjector( nnFactory );

		super.run();
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
