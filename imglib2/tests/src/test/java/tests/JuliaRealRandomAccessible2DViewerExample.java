package tests;
import fractals.JuliaRealRandomAccess;
import ij.ImageJ;
import ij.ImagePlus;

import java.awt.event.MouseEvent;

import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class JuliaRealRandomAccessible2DViewerExample< T extends RealType< T > & NativeType< T > > extends AbstractInteractive2DViewer< T >
{
	final private RealRandomAccessible< T > source;
	
	final protected ComplexDoubleType c;
	
	public JuliaRealRandomAccessible2DViewerExample( final RealRandomAccessible< T > source, final Converter< T, ARGBType > converter, final ComplexDoubleType c )
	{
		super( converter );
		this.c = c;
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
	public void mouseDragged( final MouseEvent e )
	{
		final int modifiers = e.getModifiersEx();
		if ( ( modifiers & MouseEvent.BUTTON3_DOWN_MASK ) != 0 )
		{
			dX = screenImage.dimension( 0 ) / 2 - e.getX();
			dY = screenImage.dimension( 1 ) / 2 - e.getY();
			final double a = Math.sqrt( dX * dX + dY * dY );
			if ( a == 0 )
				return;
			final double dTheta = Math.atan2( dY / a, dX / a );
			
			rotate( dTheta - oTheta );
			
			oTheta = dTheta;
		}
		else
		{
			dX = e.getX() - oX;
			dY = e.getY() - oY;
			oX += dX;
			oY += dY;
			if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 )
				c.set( c.getRealDouble() + dX / 10 / scale, c.getImaginaryDouble() + dY / 10 / scale );
			else
				translate( dX, dY );
		}
		update();
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
		
		//translate( -w, -h );
		update();
		
    }
	
	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		
		final int maxIterations = 100;
		
		final ComplexDoubleType c = new ComplexDoubleType( -0.4, 0.6 );
		
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
				return new JuliaRealRandomAccess( c, maxIterations, 4096 );
			}

			@Override
			public RealRandomAccess< UnsignedByteType > realRandomAccess( final RealInterval interval )
			{
				return realRandomAccess();
			}
		};
		
		final Converter< UnsignedByteType, ARGBType > lut = new Converter< UnsignedByteType, ARGBType >()
		{
			final protected int[] rgb = new int[ 256 ];
			{
				for ( int i = 0; i < 256; ++i )
				{
					final double r = 1.0 - ( double )i / maxIterations;
					final double g = Math.sin( Math.PI * r );
					final double b = 0.5 - 0.5 * Math.cos( Math.PI * g );
					
					final int ri = ( int )Math.round( Math.max( 0, 255 * r ) );
					final int gi = ( int )Math.round( Math.max( 0, 255 * g ) );
					final int bi = ( int )Math.round( Math.max( 0, 255 * b ) );
					
					rgb[ i ] = ( ( ( ri << 8 ) | gi ) << 8 ) | bi | 0xff000000;
				}
			}
			@Override
			public void convert( final UnsignedByteType input, final ARGBType output )
			{
				output.set( rgb[ input.get() ] );
			}
		};
		
		new JuliaRealRandomAccessible2DViewerExample< UnsignedByteType >( juliaset, lut, c ).run( "" );
	}
}
