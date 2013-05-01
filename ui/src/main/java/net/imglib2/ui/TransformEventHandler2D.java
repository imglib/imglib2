/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import net.imglib2.realtransform.AffineTransform2D;

public class TransformEventHandler2D implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	/**
	 * Current source to screen transform.
	 */
	final private AffineTransform2D affine = new AffineTransform2D();

	/**
	 * Whom to notify when the {@link #affine current transform} is changed.
	 */
	final private TransformListener2D listener;

	/**
	 * Copy of {@link #affine current transform} when mouse dragging started.
	 */
	final private AffineTransform2D affineDragStart = new AffineTransform2D();

	/**
	 * Coordinates where mouse dragging started.
	 */
	private double oX, oY;

	/**
	 * Screen coordinates to keep centered while zooming or rotating with the keyboard.
	 * For example set these to <em>(screen-width/2, screen-height/2)</em>
	 */
	private int centerX = 0, centerY = 0;

	public TransformEventHandler2D( final TransformListener2D listener )
	{
		this.listener = listener;
	}

	public TransformEventHandler2D( final AffineTransform2D t, final TransformListener2D listener )
	{
		this.listener = listener;
		affine.set( t );
	}

	/**
	 * Get (a copy of) the current source to screen transform.
	 *
	 * @return current transform.
	 */
	public AffineTransform2D getTransform()
	{
		synchronized ( affine )
		{
			return affine.copy();
		}
	}

	/**
	 * Set the current source to screen transform.
	 */
	public void setTransform( final AffineTransform2D transform )
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
			"rotate the image by left-click and dragging the image in the canvas, " + NL +
			"move the image by middle-click and dragging the image in the canvas, " + NL +
			"zoom in and out using the mouse-wheel." + NL + " " + NL +
			"Key control:" + NL + " " + NL +
			"CURSOR LEFT - Rotate clockwise." + NL +
			"CURSOR RIGHT - Rotate counter-clockwise." + NL +
			"CURSOR UP - Zoom in." + NL +
			"CURSOR DOWN - Zoom out." + NL +
			"SHIFT - Rotate and zoom 10x faster." + NL +
			"CTRL - Rotate and zoom 10x slower.";

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

			if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 ) // rotate
			{
				affine.set( affineDragStart );

				final double dX = e.getX() * f - centerX;
				final double dY = e.getY() * f - centerY;
				final double odX = oX - centerX;
				final double odY = oY - centerY;
				final double theta = Math.atan2( dY, dX ) - Math.atan2( odY, odX );

				rotate( theta );
			}
			else if ( ( modifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 ) // translate
			{
				affine.set( affineDragStart );

				final double dX = oX - e.getX() * f;
				final double dY = oY - e.getY() * f;

				affine.set( affine.get( 0, 2 ) - dX, 0, 2 );
				affine.set( affine.get( 1, 2 ) - dY, 1, 2 );
			}

			update();
		}
	}

	/**
	 * Scale by factor s. Keep screen coordinates (x, y) fixed.
	 */
	private void scale( final double s, final double x, final double y )
	{
		// center shift
		affine.set( affine.get( 0, 2 ) - x, 0, 2 );
		affine.set( affine.get( 1, 2 ) - y, 1, 2 );

		// scale
		affine.scale( s );

		// center un-shift
		affine.set( affine.get( 0, 2 ) + x, 0, 2 );
		affine.set( affine.get( 1, 2 ) + y, 1, 2 );
	}

	/**
	 * Rotate by d radians. Keep screen coordinates ({@link #centerX},
	 * {@link #centerY}) fixed.
	 */
	private void rotate( final double d )
	{
		// center shift
		affine.set( affine.get( 0, 2 ) - centerX, 0, 2 );
		affine.set( affine.get( 1, 2 ) - centerY, 1, 2 );

		// rotate
		affine.rotate( d );

		// center un-shift
		affine.set( affine.get( 0, 2 ) + centerX, 0, 2 );
		affine.set( affine.get( 1, 2 ) + centerY, 1, 2 );
	}

	@Override
	public void mouseWheelMoved( final MouseWheelEvent e )
	{
		synchronized ( affine )
		{
			final int modifiers = e.getModifiersEx();
			final double v = keyModfiedSpeed( modifiers );
			final int s = e.getWheelRotation();

			final double f = getMouseScaleFactor();
			final double dScale = 1.0 + 0.05 * v;
			if ( s > 0 )
				scale( 1.0 / dScale, e.getX() * f, e.getY() * f );
			else
				scale( dScale, e.getX() * f, e.getY() * f );

			update();
		}
	}

	@Override
	public void keyPressed( final KeyEvent e )
	{
		synchronized ( affine )
		{
			final double v = keyModfiedSpeed( e.getModifiersEx() );
			if ( e.getKeyCode() == KeyEvent.VK_LEFT )
			{
				rotate( step * v );
				update();
			}
			else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
			{
				rotate( step * -v );
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
