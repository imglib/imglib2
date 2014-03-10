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

import net.imglib2.realtransform.AffineTransform3D;

/**
 * A {@link TransformEventHandler} that changes an {@link AffineTransform3D} in
 * response to mouse and keyboard events.
 * 
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class TransformEventHandler3D extends MouseAdapter implements KeyListener, TransformEventHandler< AffineTransform3D >
{
	final static private TransformEventHandlerFactory< AffineTransform3D > factory = new TransformEventHandlerFactory< AffineTransform3D >()
	{
		@Override
		public TransformEventHandler< AffineTransform3D > create( final TransformListener< AffineTransform3D > transformListener )
		{
			return new TransformEventHandler3D( transformListener );
		}
	};

	public static TransformEventHandlerFactory< AffineTransform3D > factory()
	{
		return factory;
	}

	/**
	 * Current source to screen transform.
	 */
	final protected AffineTransform3D affine = new AffineTransform3D();

	/**
	 * Whom to notify when the {@link #affine current transform} is changed.
	 */
	protected TransformListener< AffineTransform3D > listener;

	/**
	 * Copy of {@link #affine current transform} when mouse dragging started.
	 */
	final protected AffineTransform3D affineDragStart = new AffineTransform3D();

	/**
	 * Coordinates where mouse dragging started.
	 */
	protected double oX, oY;

	/**
	 * Current rotation axis for rotating with keyboard, indexed x->0, y->1,
	 * z->2.
	 */
	protected int axis = 0;

	/**
	 * The screen size of the canvas (the component displaying the image and
	 * generating mouse events).
	 */
	protected int canvasW = 1, canvasH = 1;

	/**
	 * Screen coordinates to keep centered while zooming or rotating with the
	 * keyboard. These are set to <em>(canvasW/2, canvasH/2)</em>
	 */
	protected int centerX = 0, centerY = 0;

	public TransformEventHandler3D( final TransformListener< AffineTransform3D > listener )
	{
		this.listener = listener;
	}

	@Override
	public AffineTransform3D getTransform()
	{
		synchronized ( affine )
		{
			return affine.copy();
		}
	}

	@Override
	public void setTransform( final AffineTransform3D transform )
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
				affine.set( affine.get( 0, 3 ) - canvasW / 2, 0, 3 );
				affine.set( affine.get( 1, 3 ) - canvasH / 2, 1, 3 );
				affine.scale( ( double ) width / canvasW );
				affine.set( affine.get( 0, 3 ) + width / 2, 0, 3 );
				affine.set( affine.get( 1, 3 ) + height / 2, 1, 3 );
				update();
			}
		}
		canvasW = width;
		canvasH = height;
		centerX = width / 2;
		centerY = height / 2;
	}

	@Override
	public void setTransformListener( final TransformListener< AffineTransform3D > transformListener )
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
	private void update()
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
					"Pan and tilt the volume by left-click and dragging the image in the canvas, " + NL +
					"move the volume by middle-or-right-click and dragging the image in the canvas, " + NL +
					"browse alongside the z-axis using the mouse-wheel, and" + NL +
					"zoom in and out using the mouse-wheel holding CTRL+SHIFT or META." + NL + " " + NL +
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
			final double dX = oX - e.getX();
			final double dY = oY - e.getY();

			if ( ( modifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 ) // rotate
			{
				affine.set( affineDragStart );

				// center shift
				affine.set( affine.get( 0, 3 ) - oX, 0, 3 );
				affine.set( affine.get( 1, 3 ) - oY, 1, 3 );

				final double v = step * keyModfiedSpeed( modifiers );
				affine.rotate( 0, -dY * v );
				affine.rotate( 1, dX * v );

				// center un-shift
				affine.set( affine.get( 0, 3 ) + oX, 0, 3 );
				affine.set( affine.get( 1, 3 ) + oY, 1, 3 );
				update();
			}
			else if ( ( modifiers & ( MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK ) ) != 0 ) // translate
			{
				affine.set( affineDragStart );

				affine.set( affine.get( 0, 3 ) - dX, 0, 3 );
				affine.set( affine.get( 1, 3 ) - dY, 1, 3 );
				update();
			}
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
	 * Rotate by d radians around axis. Keep screen coordinates (
	 * {@link #centerX}, {@link #centerY}) fixed.
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
			if ( ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 && ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 ) || ( modifiers & KeyEvent.META_DOWN_MASK ) != 0 )
			{
				final double dScale = 1.0 + 0.05;
				if ( s > 0 )
					scale( 1.0 / dScale, e.getX(), e.getY() );
				else
					scale( dScale, e.getX(), e.getY() );
			}
			else
			// translate in Z
			{
				final double dZ = v * -s;
				// TODO (optionally) correct for zoom
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
			final int keyCode = e.getKeyCode();
			final int keyModifiers = e.getModifiersEx() & ( KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK | KeyEvent.ALT_GRAPH_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK | KeyEvent.META_DOWN_MASK );
			final double v = keyModfiedSpeed( e.getModifiersEx() );
			if ( keyCode == KeyEvent.VK_X && keyModifiers == 0 )
			{
				axis = 0;
			}
			else if ( keyCode == KeyEvent.VK_Y && keyModifiers == 0 )
			{
				axis = 1;
			}
			else if ( keyCode == KeyEvent.VK_Z && keyModifiers == 0 )
			{
				axis = 2;
			}
			else if ( keyCode == KeyEvent.VK_LEFT )
			{
				rotate( axis, step * v );
				update();
			}
			else if ( keyCode == KeyEvent.VK_RIGHT )
			{
				rotate( axis, step * -v );
				update();
			}
			if ( keyCode == KeyEvent.VK_UP )
			{
				final double dScale = 1.0 + 0.1 * v;
				scale( dScale, centerX, centerY );
				update();
			}
			else if ( keyCode == KeyEvent.VK_DOWN )
			{
				final double dScale = 1.0 + 0.1 * v;
				scale( 1.0 / dScale, centerX, centerY );
				update();
			}
			else if ( keyCode == KeyEvent.VK_COMMA )
			{
				affine.set( affine.get( 2, 3 ) + v, 2, 3 );
				update();
			}
			else if ( keyCode == KeyEvent.VK_PERIOD )
			{
				affine.set( affine.get( 2, 3 ) - v, 2, 3 );
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
