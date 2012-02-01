package tests;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

public class Interactive3DRotationTest implements PlugIn, KeyListener, MouseWheelListener, MouseListener, MouseMotionListener
{
	final private class GUI
	{
		final private ImageWindow window;
		final private Canvas canvas;
		
		final private ImageJ ij;
		
		/* backup */
		private KeyListener[] windowKeyListeners;
		private KeyListener[] canvasKeyListeners;
		private KeyListener[] ijKeyListeners;
		
		private MouseListener[] canvasMouseListeners;
		private MouseMotionListener[] canvasMouseMotionListeners;
		
		private MouseWheelListener[] windowMouseWheelListeners;
		
		
		GUI( final ImagePlus imp )
		{
			window = imp.getWindow();
			canvas = imp.getCanvas();
			
			ij = IJ.getInstance();
		}
		
		/**
		 * Add new event handlers.
		 */
		final void takeOverGui()
		{
			canvas.addKeyListener( Interactive3DRotationTest.this );
			window.addKeyListener( Interactive3DRotationTest.this );
			
			canvas.addMouseMotionListener( Interactive3DRotationTest.this );
			
			canvas.addMouseListener( Interactive3DRotationTest.this );
			
			ij.addKeyListener( Interactive3DRotationTest.this );
			
			window.addMouseWheelListener( Interactive3DRotationTest.this );
		}
		
		/**
		 * Backup old event handlers for restore.
		 */
		final void backupGui()
		{
			canvasKeyListeners = canvas.getKeyListeners();
			windowKeyListeners = window.getKeyListeners();
			ijKeyListeners = IJ.getInstance().getKeyListeners();
			canvasMouseListeners = canvas.getMouseListeners();
			canvasMouseMotionListeners = canvas.getMouseMotionListeners();
			windowMouseWheelListeners = window.getMouseWheelListeners();
			clearGui();	
		}
		
		/**
		 * Restore the previously active Event handlers.
		 */
		final void restoreGui()
		{
			clearGui();
			for ( final KeyListener l : canvasKeyListeners )
				canvas.addKeyListener( l );
			for ( final KeyListener l : windowKeyListeners )
				window.addKeyListener( l );
			for ( final KeyListener l : ijKeyListeners )
				ij.addKeyListener( l );
			for ( final MouseListener l : canvasMouseListeners )
				canvas.addMouseListener( l );
			for ( final MouseMotionListener l : canvasMouseMotionListeners )
				canvas.addMouseMotionListener( l );
			for ( final MouseWheelListener l : windowMouseWheelListeners )
				window.addMouseWheelListener( l );
		}
		
		/**
		 * Remove both ours and the backed up event handlers.
		 */
		final void clearGui()
		{
			for ( final KeyListener l : canvasKeyListeners )
				canvas.removeKeyListener( l );
			for ( final KeyListener l : windowKeyListeners )
				window.removeKeyListener( l );
			for ( final KeyListener l : ijKeyListeners )
				ij.removeKeyListener( l );
			for ( final MouseListener l : canvasMouseListeners )
				canvas.removeMouseListener( l );
			for ( final MouseMotionListener l : canvasMouseMotionListeners )
				canvas.removeMouseMotionListener( l );
			for ( final MouseWheelListener l : windowMouseWheelListeners )
				window.removeMouseWheelListener( l );
			
			canvas.removeKeyListener( Interactive3DRotationTest.this );
			window.removeKeyListener( Interactive3DRotationTest.this );
			ij.removeKeyListener( Interactive3DRotationTest.this );
			canvas.removeMouseListener( Interactive3DRotationTest.this );
			canvas.removeMouseMotionListener( Interactive3DRotationTest.this );
			window.removeMouseWheelListener( Interactive3DRotationTest.this );
		}
	}
	
	public class MappingThread extends Thread
	{
		private boolean pleaseRepaint;
		
		public MappingThread()
		{
			this.setName( "MappingThread" );
		}
		
		@Override
		public void run()
		{
			while ( !isInterrupted() )
			{
				final boolean b;
				synchronized ( this )
				{
					b = pleaseRepaint;
					pleaseRepaint = false;
				}
				if ( b )
				{
					synchronized ( reducedAffine )
					{
						reducedAffineCopy.set( reducedAffine );
					}
					projector.map();
					imp.setImage( screenImage.image() );
					visualizeOrientation();
					imp.updateAndDraw();
				}
				synchronized ( this )
				{
					try
					{
						if ( !pleaseRepaint ) wait();
					}
					catch ( final InterruptedException e ){}
				}
			}
		}
		
		public void repaint()
		{
			synchronized ( this )
			{
				pleaseRepaint = true;
				notify();
			}
		}
		
		public void toggleInterpolation() {
			++interpolation;
			interpolation %= 2;
			switch ( interpolation )
			{
			case 0:
				projector = createProjector( img, reducedAffineCopy.inverse(), nnFactory, converter );
				break;
			case 1:
				projector = createProjector( img, reducedAffineCopy.inverse(), nlFactory, converter );
				break;
			}
			
		}
		
		public void useLanczosInterpolation() {
			projector = createProjector( img, reducedAffineCopy.inverse(), laFactory, converter );			
		}
		
		private double perspectiveX( final double[] p, final double d, final double w2 )
		{
			return ( p[ 0 ] - w2 ) / 10 / ( p[ 2 ] / 10 + d ) * d + w2 / 5;
		}
		
		private double perspectiveY( final double[] p, final double d, final double h2 )
		{
			return ( p[ 1 ] - h2 ) / 10 / ( p[ 2 ] / 10 + d ) * d + h2 / 5;
		}
		
		private void splitEdge(
				final double[] a,
				final double[] b,
				final GeneralPath before,
				final GeneralPath behind,
				final double d2,
				final double w2,
				final double h2 )
		{
			final double[] t = new double[ 3 ];
			if ( a[ 2 ] <= 0 )
			{
				before.moveTo( perspectiveX( a, d2, w2 ), perspectiveY( a, d2, h2 ) );
				if ( b[ 2 ] <= 0 )
					before.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );					
				else
				{
					final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
					t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
					t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
					before.lineTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
					behind.moveTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
					behind.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
				}
			}
			else
			{
				behind.moveTo( perspectiveX( a, d2, w2 ), perspectiveY( a, d2, h2 ) );
				if ( b[ 2 ] > 0 )
					behind.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );					
				else
				{
					final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
					t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
					t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
					behind.lineTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
					before.moveTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
					before.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
				}
			}
			
		}
		
		private void visualizeOrientation()
		{
			final double w = img.dimension( 0 ) - 1;
			final double h = img.dimension( 1 ) - 1;
			final double d = img.dimension( 2 ) - 1;
			final double w2 = ( w + 1 ) / 2.0;
			final double h2 = ( h + 1 ) / 2.0 * yScale;
			final double d2 = d ;
			
			final double[] p000 = new double[]{ 0, 0, 0 };
			final double[] p100 = new double[]{ w, 0, 0 };
			final double[] p010 = new double[]{ 0, h, 0 };
			final double[] p110 = new double[]{ w, h, 0 };
			final double[] p001 = new double[]{ 0, 0, d };
			final double[] p101 = new double[]{ w, 0, d };
			final double[] p011 = new double[]{ 0, h, d };
			final double[] p111 = new double[]{ w, h, d };
			
			final double[] q000 = new double[ 3 ];
			final double[] q100 = new double[ 3 ];
			final double[] q010 = new double[ 3 ];
			final double[] q110 = new double[ 3 ];
			final double[] q001 = new double[ 3 ];
			final double[] q101 = new double[ 3 ];
			final double[] q011 = new double[ 3 ];
			final double[] q111 = new double[ 3 ];
			
			final double[] px = new double[]{ w / 2, 0, 0 };
			final double[] py = new double[]{ 0, h / 2, 0 };
			final double[] pz = new double[]{ 0, 0, d / 2 };
			
			final double[] qx = new double[ 3 ];
			final double[] qy = new double[ 3 ];
			final double[] qz = new double[ 3 ];
			
			
			reducedAffineCopy.apply( p000, q000 );
			reducedAffineCopy.apply( p100, q100 );
			reducedAffineCopy.apply( p010, q010 );
			reducedAffineCopy.apply( p110, q110 );
			reducedAffineCopy.apply( p001, q001 );
			reducedAffineCopy.apply( p101, q101 );
			reducedAffineCopy.apply( p011, q011 );
			reducedAffineCopy.apply( p111, q111 );
			
			reducedAffineCopy.apply( px, qx );
			reducedAffineCopy.apply( py, qy );
			reducedAffineCopy.apply( pz, qz );
			
			final GeneralPath box = new GeneralPath();
			final GeneralPath boxBehind = new GeneralPath();
			
			splitEdge( q000, q100, box, boxBehind, d2, w2, h2 );
			splitEdge( q100, q110, box, boxBehind, d2, w2, h2 );
			splitEdge( q110, q010, box, boxBehind, d2, w2, h2 );
			splitEdge( q010, q000, box, boxBehind, d2, w2, h2 );
			
			splitEdge( q001, q101, box, boxBehind, d2, w2, h2 );
			splitEdge( q101, q111, box, boxBehind, d2, w2, h2 );
			splitEdge( q111, q011, box, boxBehind, d2, w2, h2 );
			splitEdge( q011, q001, box, boxBehind, d2, w2, h2 );
			
			splitEdge( q000, q001, box, boxBehind, d2, w2, h2 );
			splitEdge( q100, q101, box, boxBehind, d2, w2, h2 );
			splitEdge( q110, q111, box, boxBehind, d2, w2, h2 );
			splitEdge( q010, q011, box, boxBehind, d2, w2, h2 );
			
			/* virtual slice canvas */
			final GeneralPath canvas = new GeneralPath();
			canvas.moveTo( perspectiveX( p000, d2, w2 ), perspectiveY( p000, d2, h2 ) );
			canvas.lineTo( perspectiveX( p100, d2, w2 ), perspectiveY( p100, d2, h2 ) );
			canvas.lineTo( perspectiveX( p110, d2, w2 ), perspectiveY( p110, d2, h2 ) );
			canvas.lineTo( perspectiveX( p010, d2, w2 ), perspectiveY( p010, d2, h2 ) );
			canvas.closePath();
			
			final Image image = imp.getImage();
			final Graphics2D graphics = ( Graphics2D )image.getGraphics();
			graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			graphics.setPaint( Color.MAGENTA );
			graphics.draw( boxBehind );
			graphics.setPaint( new Color( 0x80ffffff, true ) );
			graphics.fill( canvas );
			graphics.setPaint( Color.GREEN );
			graphics.draw( box );
			graphics.setPaint( Color.WHITE );
			graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
			graphics.drawString("x", ( float )perspectiveX( qx, d2, w2 ), ( float )perspectiveY( qx, d2, h2 ) - 2 );
			graphics.drawString("y", ( float )perspectiveX( qy, d2, w2 ), ( float )perspectiveY( qy, d2, h2 ) - 2 );
			graphics.drawString("z", ( float )perspectiveX( qz, d2, w2 ), ( float )perspectiveY( qz, d2, h2 ) - 2 );
			
		}
	}
	
	final static private String NL = System.getProperty( "line.separator" );
	
	private Img< UnsignedShortType > img;
	private ImagePlus imp;
	private GUI gui;
	
	final private ArrayList< AffineTransform3D > list = new ArrayList< AffineTransform3D >();
	final private AffineTransform3D rotation = new AffineTransform3D();
	final private AffineTransform3D mouseRotation = new AffineTransform3D();
	final private AffineTransform3D sliceShift = new AffineTransform3D();
	final private AffineTransform3D reducedAffine = new AffineTransform3D();
	final private AffineTransform3D reducedAffineCopy = new AffineTransform3D();
	
	final private NearestNeighborInterpolatorFactory< UnsignedShortType > nnFactory = new NearestNeighborInterpolatorFactory< UnsignedShortType >();
	final private NLinearInterpolatorFactory< UnsignedShortType > nlFactory = new NLinearInterpolatorFactory< UnsignedShortType >();
	final private LanczosInterpolatorFactory< UnsignedShortType > laFactory = new LanczosInterpolatorFactory< UnsignedShortType >();
	final private RealARGBConverter< UnsignedShortType > converter = new RealARGBConverter< UnsignedShortType >( 0, 4095 );
	
	final static private float step = ( float )Math.PI / 180;
	private ARGBScreenImage screenImage;
	private XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector;

	private double yScale, zScale;
	
	/* the current rotation axis, indexed x->0, y->1, z->2 */
	private int axis = 0;
	
	/* the current slice index (rotated z) in isotropic x,y,z space */
	private double currentSlice = 0;
	
	/* coordinates where mouse dragging started and the drag distance */
	private int oX, oY, dX, dY;
	
	private int interpolation = 0;
	
	private MappingThread painter;
	

	private < T extends Type< T > > XYRandomAccessibleProjector< T, ARGBType > createProjector(
			final RandomAccessibleInterval< T > source,
			final AffineGet affine,
			final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory,
			final Converter< T, ARGBType > conv )
	{
		final T template = source.randomAccess().get().copy();
		final RandomAccessible< T > extendedImg = Views.extendValue( source, template );
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( extendedImg, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, affine );
		screenImage = new ARGBScreenImage( ( int )source.dimension( 0 ), ( int )source.dimension( 1 ) );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, conv );
	}
	
	
	@Override
	public void run( final String arg )
    {
		final ImgOpener io = new ImgOpener();
		final ImgPlus< UnsignedShortType > imgPlus;
		try
		{
			imgPlus = io.openImg( "/home/saalfeld/Desktop/l1-cns.tif", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
			img = imgPlus.getImg();
		}
		catch ( final ImgIOException e )
		{
			IJ.log( "Problems opening the image, check the error msg." );
			e.printStackTrace();
			return;
		}
		
		imp = new ImagePlus( "argbScreenProjection", new ColorProcessor( ( int )img.dimension( 0 ), ( int )img.dimension( 1 ) ) );
		imp.show();
		
		imp.getWindow().setLocation( 0, 0 );
		IJ.run("In [+]");
		IJ.run("In [+]");

		list.clear();
		
		gui = new GUI( imp );
		
		yScale = imgPlus.calibration( 1 ) / imgPlus.calibration( 0 );
		zScale = imgPlus.calibration( 2 ) / imgPlus.calibration( 0 );
		
		final int w = ( int )img.dimension( 0 );
		final int h = ( int )img.dimension( 1 );
		final int d = ( int )img.dimension( 2 );
		
		currentSlice = ( d / 2.0 - 0.5 ) * zScale;
		
		/* un-scale */
		final AffineTransform3D unScale = new AffineTransform3D();
		unScale.set(
			1.0, 0.0, 0.0, 0.0,
			0.0, yScale, 0.0, 0.0,
			0.0, 0.0, zScale, 0.0 );

		/* slice shift */
		sliceShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, -currentSlice );

		/* center shift */
		final AffineTransform3D centerShift = new AffineTransform3D();
		centerShift.set(
				1, 0, 0, -w / 2.0,
				0, 1, 0, -h / 2.0 * yScale,
				0, 0, 1, -d / 2.0 * zScale );

		/* center un-shift */
		final AffineTransform3D centerUnShift = new AffineTransform3D();
		centerUnShift.set(
				1, 0, 0, w / 2.0,
				0, 1, 0, h / 2.0 * yScale,
				0, 0, 1, d / 2.0 * zScale );

		/* initialize rotation */
		rotation.set(
			1.0, 0.0, 0.0, 0.0,
			0.0, 1.0, 0.0, 0.0,
			0.0, 0.0, 1.0, 0.0 );

		list.add( unScale );
		list.add( centerShift );
		list.add( rotation );
		list.add( centerUnShift );
		list.add( sliceShift );
		
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}

		gui.backupGui();
		gui.takeOverGui();
		
		projector = createProjector( img, reducedAffineCopy.inverse(), nnFactory, converter );
		
		painter = new MappingThread();
		
		painter.start();
		
		update();
    }
	
	final private void update()
	{
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}
		painter.repaint();
	}
	
	
	private void rotate( final int a, final double d )
	{
		rotation.rotate( a, d * step );
	}
	
	final private void shift( final double d )
	{
		currentSlice += d;
		sliceShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, -currentSlice );
	}
	
	/**
	 * Concatenate a list of {@link AffineTransform3D}
	 * 
	 * @param list
	 * @param affine
	 */
	final private static void reduceAffineTransformList( final Iterable< AffineTransform3D > list, final AffineTransform3D affine )
	{
		final AffineTransform3D a = new AffineTransform3D();
		for ( final AffineTransform3D t : list )
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
		else if ( e.getKeyCode() == KeyEvent.VK_X )
		{
			axis = 0;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_Y )
		{
			axis = 1;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_Z )
		{
			axis = 2;
		}
		else
		{
			final float v = keyModfiedSpeed( e.getModifiersEx() );
			if ( e.getKeyCode() == KeyEvent.VK_LEFT )
			{
				rotate( axis, v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
			{
				rotate( axis, -v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_COMMA )
			{
				shift( v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_PERIOD )
			{
				shift( -v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_I )
			{
				painter.toggleInterpolation();
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_L )
			{
				painter.useLanczosInterpolation();
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_E )
			{
				IJ.log( rotation.toString() );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_F1 )
			{
				IJ.showMessage(
						"Interactive Stack Rotation",
						"Mouse control:" + NL + " " + NL +
						"Pan and tilt the volume by dragging the image in the canvas and" + NL +
						"browse alongside the z-axis using the mouse-wheel." + NL + " " + NL +
						"Key control:" + NL + " " + NL +
						"X - Select x-axis as rotation axis." + NL +
						"Y - Select y-axis as rotation axis." + NL +
						"Z - Select z-axis as rotation axis." + NL +
						"CURSOR LEFT - Rotate clockwise around the choosen rotation axis." + NL +
						"CURSOR RIGHT - Rotate counter-clockwise around the choosen rotation axis." + NL +
						"./> - Forward alongside z-axis." + NL +
						",/< - Backward alongside z-axis." + NL +
						"SHIFT - Rotate and browse 10x faster." + NL +
						"CTRL - Rotate and browse 10x slower." + NL +
						"ENTER/ESC - Return." + NL +
						"I - Toggle interpolation." + NL +
						"L - Use Lanczos interpolation." + NL +
						"E - Export the current rotation to the log window." );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS )
			{
				IJ.run("In [+]");
			}
			else if ( e.getKeyCode() == KeyEvent.VK_MINUS )
			{
				IJ.run("Out [-]");
			}
		}
	}

	final private float keyModfiedSpeed( final int modifiers )
	{
		if ( ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
			return 10;
		else if ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 )
			return 0.1f;
		else
			return 1;
	}

	@Override
	public void keyReleased( final KeyEvent e )
	{
		if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
		{
			oX += 9 * dX;
			oY += 9 * dY;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			oX -= 9 * dX / 10;
			oY -= 9 * dY / 10;
		}
	}
	
	@Override
	public void keyTyped( final KeyEvent e ){}
	
	@Override
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		final float v = keyModfiedSpeed( e.getModifiersEx() );
		final int s = -e.getWheelRotation();
		shift( v * s );
		update();		
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		final float v = 10 * step * keyModfiedSpeed( e.getModifiersEx() );
		dX = oX - e.getX();
		dY = oY - e.getY();
		rotation.set( mouseRotation );
		rotate( 0, -dY * v );
		rotate( 1, dX * v );
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
		oX = e.getX();
		oY = e.getY();
		mouseRotation.set( rotation );
	}
	
	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();
		new Interactive3DRotationTest().run( "" );
	}
}
