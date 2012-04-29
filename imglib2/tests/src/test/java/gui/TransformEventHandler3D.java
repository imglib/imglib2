package gui;

import ij.IJ;
import ij.ImagePlus;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.realtransform.AffineTransform3D;

public class TransformEventHandler3D implements KeyListener, MouseWheelListener, MouseListener, MouseMotionListener
{
	public interface TransformListener
	{
		public void setTransform( AffineTransform3D transform );

		public void toggleInterpolation();

		public void quit();
	}

	final Interval windowSize;

	final ImagePlus imp;

	final TransformListener listener;

	final static protected String NL = System.getProperty( "line.separator" );

	final static private float step = ( float )Math.PI / 180;

	private final double yScale, zScale;

	/* the current rotation axis, indexed x->0, y->1, z->2 */
	private int axis = 0;

	/* the current slice index (rotated z) in isotropic x,y,z space */
	private double currentSlice;

	/**
	 * Return rotate/translate/scale speed resulting from modifier keys.
	 *
	 * Normal speed is 1. SHIFT is faster (10). CTRL is slower (0.1).
	 *
	 * @param modifiers
	 * @return speed resulting from modifier keys.
	 */
	public static float keyModfiedSpeed( final int modifiers )
	{
		if ( ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
			return 10;
		else if ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 )
			return 0.1f;
		else
			return 1;
	}

	/**
	 * Concatenate a list of {@link AffineTransform3D}
	 *
	 * @param list
	 * @param affine
	 */
	final protected static void reduceAffineTransformList( final Iterable< AffineTransform3D > list, final AffineTransform3D affine )
	{
		final AffineTransform3D a = new AffineTransform3D();
		for ( final AffineTransform3D t : list )
			a.preConcatenate( t );
		affine.set( a );
	}

	final private ArrayList< AffineTransform3D > list = new ArrayList< AffineTransform3D >();
	final private AffineTransform3D affine = new AffineTransform3D();
	final private AffineTransform3D mouseRotation = new AffineTransform3D();
	final private AffineTransform3D sliceShift = new AffineTransform3D();
	final private AffineTransform3D reducedAffine = new AffineTransform3D();

	public TransformEventHandler3D( final ImagePlus imp, final TransformListener listener, final double yScale, final double zScale, final double currentSlice )
	{
		this.yScale = yScale;
		this.zScale = zScale;
		this.currentSlice = currentSlice;

		this.windowSize = new FinalInterval( new long[] { imp.getWidth(), imp.getHeight() } );
		this.imp = imp;
		this.listener = listener;
		affine.set(
				1.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0 );

		list.add( affine );
		list.add( sliceShift );

		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
		}
	}

	final protected void update()
	{
		synchronized ( reducedAffine )
		{
			reduceAffineTransformList( list, reducedAffine );
			listener.setTransform( reducedAffine );
		}
	}

	private void rotate( final int a, final double d )
	{
		final double width = windowSize.dimension( 0 );
		final double height = windowSize.dimension( 1 );

		final AffineTransform3D t = new AffineTransform3D();

		/* center shift */
		t.set(
				1, 0, 0, -width / 2.0,
				0, 1, 0, -height / 2.0 * yScale,
				0, 0, 1, -currentSlice );

		affine.preConcatenate( t );
		affine.rotate( a, d * step );

		/* center un-shift */
		t.set(
				1, 0, 0, width / 2.0,
				0, 1, 0, height / 2.0 * yScale,
				0, 0, 1, currentSlice );

		affine.preConcatenate( t );
	}

	private void scale( final double dScale )
	{
		final double width = windowSize.dimension( 0 );
		final double height = windowSize.dimension( 1 );

		final AffineTransform3D t = new AffineTransform3D();

		/* center shift */
		t.set(
				1, 0, 0, -width / 2.0,
				0, 1, 0, -height / 2.0 * yScale,
				0, 0, 1, -currentSlice );

		affine.preConcatenate( t );
		affine.scale( dScale );

		/* center un-shift */
		t.set(
				1, 0, 0, width / 2.0,
				0, 1, 0, height / 2.0 * yScale,
				0, 0, 1, currentSlice );

		affine.preConcatenate( t );
	}

	private void translate( final double dX, final double dY )
	{
		final AffineTransform3D t = new AffineTransform3D();

		t.set(
				1, 0, 0, dX,
				0, 1, 0, dY,
				0, 0, 1, 0 );

		affine.preConcatenate( t );
	}

	final private void shift( final double d )
	{
		currentSlice += d;
		sliceShift.set(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, -currentSlice );
	}

	/* coordinates where mouse dragging started and the drag distance */
	protected double oX, oY, dX, dY;

	@Override
	public void keyPressed( final KeyEvent e )
	{
		if ( e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER )
		{
			listener.quit();
		}
		else if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
		{
			// TODO ???
			oX -= 9 * dX / 10;
			oY -= 9 * dY / 10;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			// TODO ???
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
				listener.toggleInterpolation();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_E )
			{
				IJ.log( affine.toString() );
			}
			else if ( e.getKeyCode() == KeyEvent.VK_F1 )
			{
				IJ.showMessage(
						"Interactive Stack Rotation",
						"Mouse control:" + NL + " " + NL +
						"Pan and tilt the volume by left-click and dragging the image in the canvas, " + NL +
						"move the volume by middle-click and dragging the image in the canvas, " + NL +
						"browse alongside the z-axis using the mouse-wheel, and" + NL +
						"zoom in and out using the mouse-wheel holding CTRL+SHIFT." + NL + " " + NL +
						"Key control:" + NL + " " + NL +
						"X - Select x-axis as rotation axis." + NL +
						"Y - Select y-axis as rotation axis." + NL +
						"Z - Select z-axis as rotation axis." + NL +
						"CURSOR LEFT - Rotate clockwise around the choosen rotation axis." + NL +
						"CURSOR RIGHT - Rotate counter-clockwise around the choosen rotation axis." + NL +
						"./> - Forward alongside z-axis." + NL +
						",/< - Backwardi alongside z-axis." + NL +
						"SHIFT - Rotate and browse 10x faster." + NL +
						"CTRL - Rotate and browse 10x slower." + NL +
						"ENTER/ESC - Return." + NL +
						"I - Toggle interpolation." + NL +
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

	@Override
	public void keyReleased( final KeyEvent e )
	{
		if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
		{
			// TODO ???
			oX += 9 * dX;
			oY += 9 * dY;
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			// TODO ???
			oX -= 9 * dX / 10;
			oY -= 9 * dY / 10;
		}
	}

	@Override
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		final int modifiers = e.getModifiersEx();
		final float v = keyModfiedSpeed( modifiers );
		final int s = e.getWheelRotation();
		if (
				( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 &&
				( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
		{
			final double dScale = 1.0 + 0.1;
			if ( s > 0 )
				scale( 1.0 / dScale );
			else
				scale( dScale );
		}
		else
			shift( v * -s );

		update();
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		final int modifiers = e.getModifiersEx();
		if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 )
		{
			final float v = step * keyModfiedSpeed( e.getModifiersEx() );
			dX = oX - e.getX() / imp.getCanvas().getMagnification();
			dY = oY - e.getY() / imp.getCanvas().getMagnification();

			affine.set( mouseRotation );

			final AffineTransform3D t = new AffineTransform3D();

			/* center shift */
			t.set(
					1, 0, 0, -oX,
					0, 1, 0, -oY,
					0, 0, 1, -currentSlice );

			affine.preConcatenate( t );
			affine.rotate( 0, -dY * v );
			affine.rotate( 1, dX * v );

			/* center un-shift */
			t.set(
					1, 0, 0, oX,
					0, 1, 0, oY,
					0, 0, 1, currentSlice );

			affine.preConcatenate( t );
		}
		else if ( ( modifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 )
		{
			dX = e.getX() / imp.getCanvas().getMagnification() - oX;
			dY = e.getY() / imp.getCanvas().getMagnification() - oY;
			oX += dX;
			oY += dY;
			translate( dX, dY );
		}

		update();
	}

	@Override
	public void mousePressed( final MouseEvent e )
	{
		oX = e.getX() / imp.getCanvas().getMagnification();
		oY = e.getY() / imp.getCanvas().getMagnification();
		mouseRotation.set( affine );
	}

	@Override
	public void mouseMoved( final MouseEvent e )
	{}

	@Override
	public void mouseClicked( final MouseEvent e )
	{}

	@Override
	public void mouseEntered( final MouseEvent e )
	{}

	@Override
	public void mouseExited( final MouseEvent e )
	{}

	@Override
	public void keyTyped( final KeyEvent e )
	{}

	@Override
	public void mouseReleased( final MouseEvent e )
	{}
}
