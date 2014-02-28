/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import net.imglib2.realtransform.AffineTransform2D;

/**
 * A {@link TransformEventHandler} that changes an {@link AffineTransform2D} in
 * response to mouse and keyboard events.
 * 
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class TransformEventHandler2D extends MouseAdapter implements KeyListener, TransformEventHandler< AffineTransform2D >
{
	final static private TransformEventHandlerFactory< AffineTransform2D > factory = new TransformEventHandlerFactory< AffineTransform2D >()
	{
		@Override
		public TransformEventHandler< AffineTransform2D > create( final TransformListener< AffineTransform2D > transformListener )
		{
			return new TransformEventHandler2D( transformListener );
		}
	};

	public static TransformEventHandlerFactory< AffineTransform2D > factory()
	{
		return factory;
	}

	/**
	 * Current source to screen transform.
	 */
	final protected AffineTransform2D affine = new AffineTransform2D();

	/**
	 * Whom to notify when the {@link #affine current transform} is changed.
	 */
	protected TransformListener< AffineTransform2D > listener;

	/**
	 * Copy of {@link #affine current transform} when mouse dragging started.
	 */
	final protected AffineTransform2D affineDragStart = new AffineTransform2D();

	/**
	 * Coordinates where mouse dragging started.
	 */
	protected double oX, oY;

	/**
	 * The screen size of the canvas (the component displaying the image and
	 * generating mouse events).
	 */
	protected int canvasW = 1, canvasH = 1;

	/**
	 * Screen coordinates to keep centered while zooming or rotating with the
	 * keyboard. For example set these to
	 * <em>(screen-width/2, screen-height/2)</em>
	 */
	protected int centerX = 0, centerY = 0;

	public TransformEventHandler2D( final TransformListener< AffineTransform2D > listener )
	{
		this.listener = listener;
	}

	@Override
	public AffineTransform2D getTransform()
	{
		synchronized ( affine )
		{
			return affine.copy();
		}
	}

	@Override
	public void setTransform( final AffineTransform2D transform )
	{
		synchronized ( affine )
		{
			affine.set( transform );
		}
	}

	@Override
	public void setCanvasSize( final int width, final int height, final boolean updateTransform )
	{
		if ( updateTransform )
		{
			synchronized ( affine )
			{
				affine.set( affine.get( 0, 2 ) - canvasW / 2, 0, 2 );
				affine.set( affine.get( 1, 2 ) - canvasH / 2, 1, 2 );
				affine.scale( ( double ) width / canvasW );
				affine.set( affine.get( 0, 2 ) + width / 2, 0, 2 );
				affine.set( affine.get( 1, 2 ) + height / 2, 1, 2 );
				update();
			}
		}
		canvasW = width;
		canvasH = height;
		centerX = width / 2;
		centerY = height / 2;
	}

	@Override
	public void setTransformListener( final TransformListener< AffineTransform2D > transformListener )
	{
		listener = transformListener;
	}

	@Override
	public String getHelpString()
	{
		return helpString;
	}

	/**
	 * notifies {@link #listener} that the current transform changed.
	 */
	protected void update()
	{
		if ( listener != null )
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
					"move the image by middle-or-right-click and dragging the image in the canvas, " + NL +
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
			oX = e.getX();
			oY = e.getY();
			affineDragStart.set( affine );
		}
	}

	@Override
	public void mouseDragged( final MouseEvent e )
	{
		synchronized ( affine )
		{
			final int modifiers = e.getModifiersEx();

			if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 ) // rotate
			{
				affine.set( affineDragStart );

				final double dX = e.getX() - centerX;
				final double dY = e.getY() - centerY;
				final double odX = oX - centerX;
				final double odY = oY - centerY;
				final double theta = Math.atan2( dY, dX ) - Math.atan2( odY, odX );

				rotate( theta );
			}
			else if ( ( modifiers & ( MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK ) ) != 0 ) // translate
			{
				affine.set( affineDragStart );

				final double dX = oX - e.getX();
				final double dY = oY - e.getY();

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

			final double dScale = 1.0 + 0.05 * v;
			if ( s > 0 )
				scale( 1.0 / dScale, e.getX(), e.getY() );
			else
				scale( dScale, e.getX(), e.getY() );

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
	public void keyTyped( final KeyEvent e )
	{}

	@Override
	public void keyReleased( final KeyEvent e )
	{}
}
