package tests;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class Interactive2DRotationTest< T extends RealType< T > & NativeType< T > > extends AbstractInteractiveTest< T >
{
	final static private double step = Math.PI /180;
	private double theta = 0.0;
	private double scale = 1.0;
	private double oTheta = 0;
	
	@Override
	final protected synchronized void copyState()
	{
		reducedAffineCopy.set( reducedAffine );
	}
		
	@Override
	final protected void visualize()
	{
		final Image image = imp.getImage();
		final Graphics2D graphics = ( Graphics2D )image.getGraphics();
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString("theta = " + String.format( "%.3f", ( theta / Math.PI * 180 ) ), 10, 10 );
		graphics.drawString("scale = " + String.format( "%.3f", ( scale ) ), 10, 20 );
	}
	
	final private ImgPlus< T > imgPlus;
	final private ColorProcessor cp;
	
	final private ArrayList< AffineTransform2D > list = new ArrayList< AffineTransform2D >();
	final private AffineTransform2D rotation = new AffineTransform2D();
	final private AffineTransform2D reducedAffine = new AffineTransform2D();
	final private AffineTransform2D reducedAffineCopy = new AffineTransform2D();
	
	final private RealARGBConverter< T > converter;
	
	private double yScale;
	
	public Interactive2DRotationTest( final ImgPlus< T > imgPlus, final RealARGBConverter< T > converter )
	{
		this.imgPlus = imgPlus;
		this.converter = converter;
		img = imgPlus.getImg();
		cp = new ColorProcessor( 800, 600 );
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
	
	@Override
	public void run( final String arg )
    {	
		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.updateAndDraw();
		
		list.clear();
		
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
		final AffineTransform2D centerShift = new AffineTransform2D();
		centerShift.set(
				1, 0, -w / 2.0,
				0, 1, -h / 2.0 * yScale );

		/* center un-shift */
		final AffineTransform2D centerUnShift = new AffineTransform2D();
		centerUnShift.set(
				1, 0, w / 2.0,
				0, 1, h / 2.0 * yScale );

		/* initialize rotation */
		rotation.set(
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0 );

		list.add( unScale );
		list.add( centerShift );
		list.add( rotation );
		list.add( centerUnShift );
		
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}

		gui.backupGui();
		gui.takeOverGui();
		
		projector = createProjector( nnFactory );
		
		painter = new MappingThread();
		
		painter.start();
		
		update();
    }
	
	@Override
	final protected void update()
	{
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}
		painter.repaint();
	}
	
	
	private void rotate( final double d )
	{
		theta += d;
		while ( theta > Math.PI )
			theta -= Math.PI + Math.PI;
		while ( theta < -Math.PI )
			theta += Math.PI + Math.PI;
		rotation.rotate( d );
	}
	
	private void scale( final double d )
	{
		scale *= d;
		rotation.scale( d );
	}
	
	/**
	 * Concatenate a list of {@link AffineTransform3D}
	 * 
	 * @param list
	 * @param affine
	 */
	final private static void reduceAffineTransformList( final Iterable< AffineTransform2D > list, final AffineTransform2D affine )
	{
		final AffineTransform2D a = new AffineTransform2D();
		for ( final AffineTransform2D t : list )
			a.preConcatenate( t );
		affine.set( a );
	}
	
	@Override
	public void keyPressed( final KeyEvent e )
	{
		if ( e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER )
		{
			painter.interrupt();
			
			if ( imp != null )
			{
				if ( e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					gui.restoreGui();
				}
			}
		}
		else if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
		{
			oX -= 9 * dX / 10;
			oY -= 9 * dY / 10;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			oX += 9 * dX;
			oY += 9 * dY;
		}
		else
		{
			final float v = keyModfiedSpeed( e.getModifiersEx() );
			if ( e.getKeyCode() == KeyEvent.VK_LEFT )
			{
				rotate( -step * v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
			{
				rotate( step * v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_I )
			{
				painter.toggleInterpolation();
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_E )
			{
				IJ.log( rotation.toString() );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_F1 )
			{
				IJ.showMessage(
						"Interactive Rotation",
						"Mouse control:" + NL + " " + NL +
						"Rotate the image by dragging the image in the canvas." + NL + " " + NL +
						"Key control:" + NL + " " + NL +
						"CURSOR LEFT - Rotate clockwise around the choosen rotation axis." + NL +
						"CURSOR RIGHT - Rotate counter-clockwise around the choosen rotation axis." + NL +
						"SHIFT - Rotate 10x faster." + NL +
						"CTRL - Rotate 10x slower." + NL +
						"ENTER/ESC - Return." + NL +
						"I - Toggle interpolation." + NL +
						"E - Export the current rotation to the log window." );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS )
			{
				scale( 1.0 + 0.1 * v );
				update();
//				IJ.run("In [+]");
			}
			else if ( e.getKeyCode() == KeyEvent.VK_MINUS )
			{
				scale( 1.0 / ( 1.0 + 0.1 * v ) );
				update();
//				IJ.run("Out [-]");
			}
		}
	}

	@Override
	public void mouseWheelMoved( final MouseWheelEvent e ) {}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		dX = img.dimension( 0 ) / 2 - e.getX();
		dY = img.dimension( 1 ) / 2 - e.getY();
		final double a = Math.sqrt( dX * dX + dY * dY );
		if ( a == 0 )
			return;
		final double dTheta = Math.atan2( dY / a, dX / a );
		
		rotation.rotate( dTheta - oTheta );
		
		oTheta += dTheta;
		
		update();
	}

	@Override
	public void mouseMoved( final MouseEvent e ){}
	@Override
	public void mouseClicked( final MouseEvent e ){}
	@Override
	public void mouseEntered( final MouseEvent e ){}
	@Override
	public void mouseExited( final MouseEvent e ){}
	@Override
	public void mouseReleased( final MouseEvent e ){}
	@Override
	public void mousePressed( final MouseEvent e )
	{
		dX = img.dimension( 0 ) / 2 - e.getX();
		dY = img.dimension( 1 ) / 2 - e.getY();
		final double a = Math.sqrt( dX * dX + dY * dY );
		if ( a == 0 )
			return;
		
		oTheta = Math.atan2( dY / a , dX / a );
	}
	
	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		final ImgOpener io = new ImgOpener();
		final ImgPlus< UnsignedByteType > imgPlus;
		try
		{
			imgPlus = io.openImg( "/home/saalfeld/Desktop/screenshot.tif", new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		}
		catch ( final ImgIOException e )
		{
			IJ.log( "Problems opening the image, check the error msg." );
			e.printStackTrace();
			return;
		}
		new Interactive2DRotationTest< UnsignedByteType >( imgPlus, new RealARGBConverter< UnsignedByteType >( 0, 255 ) ).run( "" );
	}
}
