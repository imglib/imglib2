package net.imglib2.ui;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import net.imglib2.realtransform.AffineTransform3D;


public class TransformEventHandler3D implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	/**
	 * Current source to screen transform.
	 */
	final private AffineTransform3D affine = new AffineTransform3D();

	/**
	 * Whom to notify when the {@link #affine current transform} is changed.
	 */
	final private TransformListener3D listener;

	/**
	 * Copy of {@link #affine current transform} when mouse dragging started.
	 */
	final private AffineTransform3D affineDragStart = new AffineTransform3D();

	/**
	 * Coordinates where mouse dragging started.
	 */
	private double oX, oY;

	/**
	 * Current rotation axis for rotating with keyboard, indexed x->0, y->1, z->2.
	 */
	private int axis = 0;

	/**
	 * Screen coordinates to keep centered while zooming or rotating with the keyboard.
	 * For example set these to <em>(screen-width/2, screen-height/2)</em>
	 */
	private int centerX = 0, centerY = 0;

	public TransformEventHandler3D( final TransformListener3D listener )
	{
		this.listener = listener;
	}

	public TransformEventHandler3D( final AffineTransform3D t, final TransformListener3D listener )
	{
		this.listener = listener;
		affine.set( t );
	}

	/**
	 * Get (a copy of) the current source to screen transform.
	 *
	 * @return current transform.
	 */
	public AffineTransform3D getTransform()
	{
		synchronized ( affine )
		{
			return affine.copy();
		}
	}

	/**
	 * Set the current source to screen transform.
	 */
	public void setTransform( final AffineTransform3D transform )
	{
		synchronized ( affine )
		{
			affine.set( transform );
		}
	}

	/**
	 * Get description of how mouse and keyboard actions map to transformations.
	 */
	public String getHelpString()
	{
		return helpString;
	}

	/**
	 * Set screen coordinates to keep fixed while zooming or rotating with the keyboard.
	 * For example set these to <em>(screen-width/2, screen-height/2)</em>
	 */
	public void setWindowCenter( final int x, final int y )
	{
		centerX = x;
		centerY = y;
	}

	/**
	 * notifies {@link #listener} that the current transform changed.
	 */
	private void update()
	{
		listener.transformChanged( affine );
	}

	/**
	 * One step of rotation (radian).
	 */
	final private static double step = Math.PI / 180;

	final private static String NL = System.getProperty( "line.separator" );

	final private static String helpString =
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
			"CURSOR UP - Zoom in." + NL +
			"CURSOR DOWN - Zoom out." + NL +
			"./> - Forward alongside z-axis." + NL +
			",/< - Backward alongside z-axis." + NL +
			"SHIFT - Rotate and browse 10x faster." + NL +
			"CTRL - Rotate and browse 10x slower.";

	/**
	 * Return rotate/translate/scale speed resulting from modifier keys.
	 *
	 * Normal speed is 1. SHIFT is faster (10). CTRL is slower (0.1).
	 *
	 * @param modifiers
	 * @return speed resulting from modifier keys.
	 */
	private static double keyModfiedSpeed( final int modifiers )
	{
		if ( ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
			return 10;
		else if ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 )
			return 0.1;
		else
			return 1;
	}

	@Override
	public void mousePressed( final MouseEvent e )
	{
		synchronized ( affine )
		{
			final double f = getMouseScaleFactor();
			oX = e.getX() * f;
			oY = e.getY() * f;
			affineDragStart.set( affine );
		}
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		synchronized ( affine )
		{
			final int modifiers = e.getModifiersEx();
			final double f = getMouseScaleFactor();
			final double dX = oX - e.getX() * f;
			final double dY = oY - e.getY() * f;

			if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 ) // rotate
			{
				affine.set( affineDragStart );

				// center shift
				affine.set( affine.get( 0, 3 ) - oX, 0, 3 );
				affine.set( affine.get( 1, 3 ) - oY, 1, 3 );

				// rotate
				final double v = step * keyModfiedSpeed( modifiers );
				affine.rotate( 0, -dY * v );
				affine.rotate( 1, dX * v );

				// center un-shift
				affine.set( affine.get( 0, 3 ) + oX, 0, 3 );
				affine.set( affine.get( 1, 3 ) + oY, 1, 3 );
			}
			else if ( ( modifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 ) // translate
			{
				affine.set( affineDragStart );

				affine.set( affine.get( 0, 3 ) - dX, 0, 3 );
				affine.set( affine.get( 1, 3 ) - dY, 1, 3 );
			}
			update();
		}
	}

	private void scale( final double s, final double x, final double y )
	{
		// center shift
		affine.set( affine.get( 0, 3 ) - x, 0, 3 );
		affine.set( affine.get( 1, 3 ) - y, 1, 3 );

		// scale
		affine.scale( s );

		// center un-shift
		affine.set( affine.get( 0, 3 ) + x, 0, 3 );
		affine.set( affine.get( 1, 3 ) + y, 1, 3 );
	}

	/**
	 * Rotate by d radians around axis. Keep screen coordinates
	 * ({@link #centerX}, {@link #centerY}) fixed.
	 */
	private void rotate( final int axis, final double d )
	{
		// center shift
		affine.set( affine.get( 0, 3 ) - centerX, 0, 3 );
		affine.set( affine.get( 1, 3 ) - centerY, 1, 3 );

		// rotate
		affine.rotate( axis, d );

		// center un-shift
		affine.set( affine.get( 0, 3 ) + centerX, 0, 3 );
		affine.set( affine.get( 1, 3 ) + centerY, 1, 3 );

	}

	@Override
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		synchronized ( affine )
		{
			final int modifiers = e.getModifiersEx();
			final double v = keyModfiedSpeed( modifiers );
			final int s = e.getWheelRotation();
			if ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 &&
			     ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
			{
				final double f = getMouseScaleFactor();
				final double dScale = 1.0 + 0.05;
				if ( s > 0 )
					scale( 1.0 / dScale, e.getX() * f, e.getY() * f );
				else
					scale( dScale, e.getX() * f, e.getY() * f );
			}
			else
			// translate in Z
			{
				final double dZ = v * -s;
				affine.set( affine.get( 2, 3 ) - dZ, 2, 3 );
			}

			update();
		}
	}

	@Override
	public void keyPressed( final KeyEvent e )
	{
		synchronized ( affine )
		{
			if ( e.getKeyCode() == KeyEvent.VK_X )
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
				final double v = keyModfiedSpeed( e.getModifiersEx() );
				if ( e.getKeyCode() == KeyEvent.VK_LEFT )
				{
					rotate( axis, step * v );
					update();
				}
				else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
				{
					rotate( axis, step * -v );
					update();
				}
				if ( e.getKeyCode() == KeyEvent.VK_UP )
				{
					final double dScale = 1.0 + 0.1 * v;
					scale( dScale, centerX, centerY );
					update();
				}
				else if ( e.getKeyCode() == KeyEvent.VK_DOWN )
				{
					final double dScale = 1.0 + 0.1 * v;
					scale( 1.0 / dScale, centerX, centerY );
					update();
				}
				else if ( e.getKeyCode() == KeyEvent.VK_COMMA )
				{
					affine.set( affine.get( 2, 3 ) + v, 2, 3 );
					update();
				}
				else if ( e.getKeyCode() == KeyEvent.VK_PERIOD )
				{
					affine.set( affine.get( 2, 3 ) - v, 2, 3 );
					update();
				}
			}
		}
	}

	@Override
	public void mouseMoved( final MouseEvent e ) {}

	@Override
	public void mouseClicked( final MouseEvent e ) {}

	@Override
	public void mouseReleased( final MouseEvent e ) {}

	@Override
	public void mouseEntered( final MouseEvent e ) {}

	@Override
	public void mouseExited( final MouseEvent e ) {}

	@Override
	public void keyTyped( final KeyEvent e ) {}

	@Override
	public void keyReleased( final KeyEvent e ) {}

	protected double getMouseScaleFactor()
	{
		return 1.0;
	}
}
