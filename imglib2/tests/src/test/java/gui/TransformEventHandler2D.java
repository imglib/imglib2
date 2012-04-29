package gui;

import ij.IJ;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import net.imglib2.Interval;
import net.imglib2.realtransform.AffineTransform2D;

public class TransformEventHandler2D implements KeyListener, MouseWheelListener, MouseListener, MouseMotionListener
{
	final static protected double step = Math.PI /180;
	protected double theta = 0.0;

	public double getTheta()
	{
		return theta;
	}

	public double getScale()
	{
		return scale;
	}

	protected double scale = 1.0;
	protected double oTheta = 0;

	public interface TransformListener
	{
		public void setTransform( AffineTransform2D transform );

		public void toggleInterpolation();

		public void quit();
	}

	Interval windowSize;

	final static protected String NL = System.getProperty( "line.separator" );

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
	 * Concatenate a list of {@link AffineTransform2D}
	 *
	 * @param list
	 * @param affine
	 */
	final protected static void reduceAffineTransformList( final Iterable< AffineTransform2D > list, final AffineTransform2D affine )
	{
		final AffineTransform2D a = new AffineTransform2D();
		for ( final AffineTransform2D t : list )
			a.preConcatenate( t );
		affine.set( a );
	}

	final protected ArrayList< AffineTransform2D > rotationList = new ArrayList< AffineTransform2D >();
	final protected AffineTransform2D affine = new AffineTransform2D();
	final protected AffineTransform2D centerShift = new AffineTransform2D();
	final protected AffineTransform2D centerUnShift = new AffineTransform2D();
	final protected AffineTransform2D rotation = new AffineTransform2D();
	final protected AffineTransform2D reducedRotation = new AffineTransform2D();

	final TransformListener listener;

	public TransformEventHandler2D( final Interval windowSize, final TransformListener listener )
	{
		this.windowSize = windowSize;
		this.listener = listener;

		centerShift.set(
				1, 0, -windowSize.dimension( 0 ) / 2.0,
				0, 1, -windowSize.dimension( 1 ) / 2.0 );
		centerUnShift.set(
				1, 0, windowSize.dimension( 0 ) / 2.0,
				0, 1, windowSize.dimension( 1 ) / 2.0 );

		rotationList.add( centerShift );
		rotationList.add( rotation );
		rotationList.add( centerUnShift );
	}

	final protected void update()
	{
		synchronized ( rotation )
		{
			reduceAffineTransformList( rotationList, reducedRotation );
			rotation.set(
					1, 0, 0,
					0, 1, 0 );
			affine.preConcatenate( reducedRotation );
			listener.setTransform( affine );
		}
	}

	protected void rotate( double dTheta )
	{
		while ( dTheta > Math.PI )
			dTheta -= Math.PI + Math.PI;
		while ( dTheta < -Math.PI )
			dTheta += Math.PI + Math.PI;

		theta += dTheta;

		while ( theta > Math.PI )
			theta -= Math.PI + Math.PI;
		while ( theta < -Math.PI )
			theta += Math.PI + Math.PI;

		synchronized ( rotation )
		{
			rotation.rotate( dTheta );
		}
	}

	protected void scale( final double dScale )
	{
		scale *= dScale;

		synchronized ( rotation )
		{
			rotation.scale( dScale );
		}
	}

	protected void translate( final double x, final double y )
	{
		synchronized ( rotation )
		{
			rotation.set(
				1, 0, x,
				0, 1, y );
		}
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
			// TODO
		}
		else if ( e.getKeyCode() == KeyEvent.VK_CONTROL )
		{
			// TODO
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
				listener.toggleInterpolation();
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
			}
			else if ( e.getKeyCode() == KeyEvent.VK_MINUS )
			{
				scale( 1.0 / ( 1.0 + 0.1 * v ) );
				update();
			}
		}
	}

	@Override
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		final float v = keyModfiedSpeed( e.getModifiersEx() );
		final int s = e.getWheelRotation();
		final double dScale = 1.0 + 0.1 * v;
		if ( s > 0 )
			scale( 1.0 / dScale );
		else
			scale( dScale );
		update();
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		final int modifiers = e.getModifiersEx();
		if ( ( modifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 )
		{
			dX = e.getX() - oX;
			dY = e.getY() - oY;
			oX += dX;
			oY += dY;
			translate( dX, dY );
		}
		else
		{
			dX = windowSize.dimension( 0 ) / 2 - e.getX();
			dY = windowSize.dimension( 1 ) / 2 - e.getY();
			final double a = Math.sqrt( dX * dX + dY * dY );
			if ( a == 0 )
				return;
			final double dTheta = Math.atan2( dY / a, dX / a );

			rotate( dTheta - oTheta );

			oTheta = dTheta;
		}
		update();
	}

	@Override
	public void mousePressed( final MouseEvent e )
	{
		oX = e.getX();
		oY = e.getY();

		dX = windowSize.dimension( 0 ) / 2 - oX;
		dY = windowSize.dimension( 1 ) / 2 - oY;
		final double a = Math.sqrt( dX * dX + dY * dY );
		if ( a == 0 )
			return;

		oTheta = Math.atan2( dY / a , dX / a );
	}

	@Override
	public void mouseMoved( final MouseEvent e )
	{
	}

	@Override
	public void mouseClicked( final MouseEvent e )
	{
	}

	@Override
	public void mouseReleased( final MouseEvent e )
	{
	}

	@Override
	public void mouseEntered( final MouseEvent e )
	{
	}

	@Override
	public void mouseExited( final MouseEvent e )
	{
	}

	@Override
	public void keyTyped( final KeyEvent e )
	{
	}

	@Override
	public void keyReleased( final KeyEvent e )
	{
	}
}
