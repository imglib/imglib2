package tobias;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class Img2DViewerExample< T extends RealType< T > & NativeType< T > > extends AbstractInteractive2DViewer< T >
{
	final private ImgPlus< T > imgPlus;
	final private Img< T > img;
	final protected ColorProcessor cp;

	private double yScale;

	public Img2DViewerExample( final ImgPlus< T > imgPlus, final RealARGBConverter< T > converter )
	{
		super( converter );
		this.imgPlus = imgPlus;
		img = imgPlus.getImg();
		cp = new ColorProcessor( 800, 600 );
		run();
	}

	@Override
	protected XYRandomAccessibleProjector< T, ARGBType > createProjector(
			final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final T template = img.randomAccess().get().copy();
		final RandomAccessible< T > extendedImg = Views.extendValue( img, template );
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( extendedImg, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, reducedAffineCopy.inverse() );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] )cp.getPixels() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}

	public void run()
    {
		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.updateAndDraw();

		list.clear();
		rotationList.clear();

		gui = new GUI( imp );

		if ( Double.isNaN( imgPlus.calibration( 0 ) ) || Double.isNaN( imgPlus.calibration( 1 ) ) )
			yScale = 1;
		else
			yScale = imgPlus.calibration( 1 ) / imgPlus.calibration( 0 );

		final int w = cp.getWidth();
		final int h = cp.getHeight();

		/* un-scale */
		final AffineTransform2D unScale = new AffineTransform2D();
		unScale.set(
			1.0, 0.0, ( cp.getWidth() - img.dimension( 0 ) ) / 2.0,
			0.0, yScale, ( cp.getHeight() - img.dimension( 1 ) * yScale ) / 2.0 );

		/* center shift */
		centerShift.set(
				1, 0, -w / 2.0,
				0, 1, -h / 2.0 );

		/* center un-shift */
		centerUnShift.set(
				1, 0, w / 2.0,
				0, 1, h / 2.0 );

		/* initialize rotation */
		rotation.set(
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0 );

		list.add( unScale );
		list.add( affine );

		rotationList.add( centerShift );
		rotationList.add( rotation );
		rotationList.add( centerUnShift );

		gui.takeOverGui();

		projector = createProjector( nnFactory );

		painter = new MappingThread();

		painter.start();

		update();
    }

	final static public void main( final String[] args ) throws ImgIOException
	{
		//new ImageJ();
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
