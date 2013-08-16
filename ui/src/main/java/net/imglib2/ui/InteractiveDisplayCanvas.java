package net.imglib2.ui;

import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public interface InteractiveDisplayCanvas< T > extends TransformListener< T >
{
	/**
	 * Add an {@link OverlayRenderer} that draws on top of the current {@link #bufferedImage}.
	 *
	 * @param renderer overlay renderer to add.
	 */
	public void addOverlayRenderer( final OverlayRenderer renderer );

	/**
	 * Remove an {@link OverlayRenderer}.
	 *
	 * @param renderer overlay renderer to remove.
	 */
	public void removeOverlayRenderer( final OverlayRenderer renderer );

	/**
	 * Add a {@link TransformListener} to notify about view transformation changes.
	 *
	 * @param listener the transform listener to add.
	 */
	public void addTransformListener( final TransformListener< T > listener );

	/**
	 * Remove a {@link TransformListener}.
	 *
	 * @param listener the transform listener to remove.
	 */
	public void removeTransformListener( final TransformListener< T > listener );

	/**
	 * Add new event handler. Depending on the interfaces implemented by
	 * <code>handler</code> calls {@link Component#addKeyListener(KeyListener)},
	 * {@link Component#addMouseListener(MouseListener)},
	 * {@link Component#addMouseMotionListener(MouseMotionListener)},
	 * {@link Component#addMouseWheelListener(MouseWheelListener)}.
	 */
	public void addHandler( final Object handler );

	/**
	 * Remove an event handler.
	 * Add new event handler. Depending on the interfaces implemented by
	 * <code>handler</code> calls {@link Component#removeKeyListener(KeyListener)},
	 * {@link Component#removeMouseListener(MouseListener)},
	 * {@link Component#removeMouseMotionListener(MouseMotionListener)},
	 * {@link Component#removeMouseWheelListener(MouseWheelListener)}.
	 */
	public void removeHandler( final Object handler );

	/**
	 * Get the {@link TransformEventHandler} that handles mouse and key events
	 * to update our view transform.
	 *
	 * @return handles mouse and key events to update the view transform.
	 */
	public TransformEventHandler< T > getTransformEventHandler();

	/**
	 * Set the {@link TransformEventHandler} that handles mouse and key events
	 * to update our view transform.
	 *
	 * @param handles mouse and key events to update the view transform
	 */
	public void setTransformEventHandler( final TransformEventHandler< T > transformEventHandler );
}