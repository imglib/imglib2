package tests;
import fractals.JuliaRealRandomAccess;
import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.tutorial.t02.MandelbrotRealRandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class RealRandomAccessible2DViewerExample< T extends RealType< T > & NativeType< T > > extends AbstractInteractive2DViewer< T >
{
	final private RealRandomAccessible< T > source;
	
	public RealRandomAccessible2DViewerExample( final RealRandomAccessible< T > source, final RealARGBConverter< T > converter )
	{
		super( converter );
		this.source = source;
	}
	
	@Override
	protected XYRandomAccessibleProjector< T, ARGBType > createProjector(
			final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( source, reducedAffineCopy.inverse() );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] )cp.getPixels() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
	
	@Override
	public void run( final String arg )
    {	
		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.updateAndDraw();
		
		list.clear();
		rotationList.clear();
		
		gui = new GUI( imp );
		
		final int w = cp.getWidth();
		final int h = cp.getHeight();
		
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

		list.add( affine );
		
		rotationList.add( centerShift );
		rotationList.add( rotation );
		rotationList.add( centerUnShift );
		
		gui.backupGui();
		gui.takeOverGui();
		
		projector = createProjector( null );
		
		painter = new MappingThread();
		
		painter.start();
		
		translate( w / 2.0, h / 2.0 );
		update();
		
		scale( 200 );
		update();
		
		translate( 100, 0 );
		update();
		
    }
	
	final static public void main2( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		
		final RealRandomAccessible< UnsignedByteType > mandelbrot = new RealRandomAccessible< UnsignedByteType >()
		{
			@Override
			public int numDimensions()
			{
				return 2;
			}

			@Override
			public RealRandomAccess< UnsignedByteType > realRandomAccess()
			{
				return new MandelbrotRealRandomAccess();
			}

			@Override
			public RealRandomAccess< UnsignedByteType > realRandomAccess( final RealInterval interval )
			{
				return realRandomAccess();
			}
			
		};
		
		new RealRandomAccessible2DViewerExample< UnsignedByteType >( mandelbrot, new RealARGBConverter< UnsignedByteType >( 0, 255 ) ).run( "" );
	}
	
	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		
		final RealRandomAccessible< UnsignedByteType > juliaset = new RealRandomAccessible< UnsignedByteType >()
		{
			@Override
			public int numDimensions()
			{
				return 2;
			}

			@Override
			public RealRandomAccess< UnsignedByteType > realRandomAccess()
			{
				return new JuliaRealRandomAccess();
			}

			@Override
			public RealRandomAccess< UnsignedByteType > realRandomAccess( final RealInterval interval )
			{
				return realRandomAccess();
			}
			
		};
		
		new RealRandomAccessible2DViewerExample< UnsignedByteType >( juliaset, new RealARGBConverter< UnsignedByteType >( 0, 255 ) ).run( "" );
	}
}
