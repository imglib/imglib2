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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;

/**
 * A {@link JComponent} that is a {@link InteractiveDisplayCanvas}.
 * 
 * @param <A>
 *            transform type
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class InteractiveDisplayCanvasComponent< A > extends JComponent implements InteractiveDisplayCanvas< A >
{
	private static final long serialVersionUID = -5546719724928785878L;

	/**
	 * Mouse/Keyboard handler that manipulates the view transformation.
	 */
	protected TransformEventHandler< A > handler;

	/**
	 * Listeners that we have to notify about view transformation changes.
	 */
	final protected CopyOnWriteArrayList< TransformListener< A > > transformListeners;

	/**
	 * The {@link OverlayRenderer} that draws on top of the current
	 * {@link #bufferedImage}.
	 */
	final protected CopyOnWriteArrayList< OverlayRenderer > overlayRenderers;

	/**
	 * Create a new {@link InteractiveDisplayCanvas} with initially no
	 * {@link OverlayRenderer OverlayRenderers} and no {@link TransformListener
	 * TransformListeners}. A {@link TransformEventHandler} is instantiated
	 * using the given factory, and registered for mouse and key events if it
	 * implements the appropriate interfaces ({@link MouseListener} etc.)
	 * 
	 * @param width
	 *            preferred component width.
	 * @param height
	 *            preferred component height.
	 * @param transformEventHandlerFactory
	 *            factory to create a {@link TransformEventHandler} appropriate
	 *            for our transform type A.
	 */
	public InteractiveDisplayCanvasComponent( final int width, final int height, final TransformEventHandlerFactory< A > transformEventHandlerFactory )
	{
		super();
		setPreferredSize( new Dimension( width, height ) );
		setFocusable( true );

		this.overlayRenderers = new CopyOnWriteArrayList< OverlayRenderer >();
		this.transformListeners = new CopyOnWriteArrayList< TransformListener< A > >();

		addComponentListener( new ComponentAdapter()
		{
			@Override
			public void componentResized( final ComponentEvent e )
			{
				final int w = getWidth();
				final int h = getHeight();
				if ( handler != null )
					handler.setCanvasSize( w, h, true );
				for ( final OverlayRenderer or : overlayRenderers )
					or.setCanvasSize( w, h );
				// enableEvents( AWTEvent.MOUSE_MOTION_EVENT_MASK );
			}
		} );

		addMouseListener( new MouseAdapter()
		{
			@Override
			public void mousePressed( final MouseEvent e )
			{
				requestFocusInWindow();
			}
		} );

		handler = transformEventHandlerFactory.create( this );
		handler.setCanvasSize( width, height, false );
		addHandler( handler );
	}

	/**
	 * Add an {@link OverlayRenderer} that draws on top of the current
	 * {@link #bufferedImage}.
	 * 
	 * @param renderer
	 *            overlay renderer to add.
	 */
	@Override
	public void addOverlayRenderer( final OverlayRenderer renderer )
	{
		overlayRenderers.add( renderer );
		renderer.setCanvasSize( getWidth(), getHeight() );
	}

	/**
	 * Remove an {@link OverlayRenderer}.
	 * 
	 * @param renderer
	 *            overlay renderer to remove.
	 */
	@Override
	public void removeOverlayRenderer( final OverlayRenderer renderer )
	{
		overlayRenderers.remove( renderer );
	}

	/**
	 * Add a {@link TransformListener} to notify about view transformation
	 * changes.
	 * 
	 * @param listener
	 *            the transform listener to add.
	 */
	@Override
	public void addTransformListener( final TransformListener< A > listener )
	{
		transformListeners.add( listener );
	}

	/**
	 * Remove a {@link TransformListener}.
	 * 
	 * @param listener
	 *            the transform listener to remove.
	 */
	@Override
	public void removeTransformListener( final TransformListener< A > listener )
	{
		transformListeners.remove( listener );
	}

	/**
	 * Add new event handler. Depending on the interfaces implemented by
	 * <code>handler</code> calls {@link Component#addKeyListener(KeyListener)},
	 * {@link Component#addMouseListener(MouseListener)},
	 * {@link Component#addMouseMotionListener(MouseMotionListener)},
	 * {@link Component#addMouseWheelListener(MouseWheelListener)}.
	 */
	@Override
	public void addHandler( final Object handler )
	{
		if ( KeyListener.class.isInstance( handler ) )
			addKeyListener( ( KeyListener ) handler );

		if ( MouseMotionListener.class.isInstance( handler ) )
			addMouseMotionListener( ( MouseMotionListener ) handler );

		if ( MouseListener.class.isInstance( handler ) )
			addMouseListener( ( MouseListener ) handler );

		if ( MouseWheelListener.class.isInstance( handler ) )
			addMouseWheelListener( ( MouseWheelListener ) handler );
	}

	/**
	 * Remove an event handler. Add new event handler. Depending on the
	 * interfaces implemented by <code>handler</code> calls
	 * {@link Component#removeKeyListener(KeyListener)},
	 * {@link Component#removeMouseListener(MouseListener)},
	 * {@link Component#removeMouseMotionListener(MouseMotionListener)},
	 * {@link Component#removeMouseWheelListener(MouseWheelListener)}.
	 */
	@Override
	public void removeHandler( final Object handler )
	{
		if ( KeyListener.class.isInstance( handler ) )
			removeKeyListener( ( KeyListener ) handler );

		if ( MouseMotionListener.class.isInstance( handler ) )
			removeMouseMotionListener( ( MouseMotionListener ) handler );

		if ( MouseListener.class.isInstance( handler ) )
			removeMouseListener( ( MouseListener ) handler );

		if ( MouseWheelListener.class.isInstance( handler ) )
			removeMouseWheelListener( ( MouseWheelListener ) handler );
	}

	/**
	 * Get the {@link TransformEventHandler} that handles mouse and key events
	 * to update our view transform.
	 * 
	 * @return handles mouse and key events to update the view transform.
	 */
	@Override
	public TransformEventHandler< A > getTransformEventHandler()
	{
		return handler;
	}

	/**
	 * Set the {@link TransformEventHandler} that handles mouse and key events
	 * to update our view transform.
	 * 
	 * @param handles
	 *            mouse and key events to update the view transform
	 */
	@Override
	public synchronized void setTransformEventHandler( final TransformEventHandler< A > transformEventHandler )
	{
		removeHandler( handler );
		handler = transformEventHandler;
		handler.setCanvasSize( getWidth(), getHeight(), false );
		addHandler( handler );
	}

	@Override
	public void paintComponent( final Graphics g )
	{
		for ( final OverlayRenderer or : overlayRenderers )
			or.drawOverlays( g );
	}

	/**
	 * This is called by our {@link #getTransformEventHandler() transform event
	 * handler} when the transform is changed. In turn, we notify all our
	 * {@link TransformListener TransformListeners} that the view transform has
	 * changed.
	 */
	@Override
	public void transformChanged( final A transform )
	{
		for ( final TransformListener< A > l : transformListeners )
			l.transformChanged( transform );
	}
}
