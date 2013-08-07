package net.imglib2.ui;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * TODO
 *
 * @param <T>
 *            type of transformation.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface TransformEventHandler< T > extends MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	/**
	 * Get (a copy of) the current source-to-screen transform.
	 *
	 * @return current transform.
	 */
	public T getTransform();

	/**
	 * Set the current source-to-screen transform.
	 */
	public void setTransform( final T transform );

	/**
	 * This is called, when the screen size of the canvas (the component
	 * displaying the image and generating mouse events) changes. This can be
	 * used to determine screen coordinates to keep fixed while zooming or
	 * rotating with the keyboard, e.g., set these to
	 * <em>(width/2, height/2)</em>. It can also be used to update the current
	 * source-to-screen transform, e.g., to change the zoom along with the
	 * canvas size.
	 *
	 * @param width
	 *            the new canvas width.
	 * @param height
	 *            the new canvas height.
	 * @param updateTransform
	 *            whether the current source-to-screen transform should be
	 *            updated. This will be <code>false</code> for the initial
	 *            update a new {@link TransformEventHandler} and
	 *            <code>true</code> on subsequent calls. If <code>true</code>,
	 *            an update to its {@link TransformListener} should be
	 *            triggered.
	 */
	public void setCanvasSize( final int width, final int height, final boolean updateTransform );

	/**
	 * Get description of how mouse and keyboard actions map to transformations.
	 */
	public String getHelpString();
	// TODO: ???

}
