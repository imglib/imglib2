package gui;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;

import java.awt.Canvas;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Register mouse and key listeners. Backup and restore old listeners.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class GUI< T extends KeyListener & MouseWheelListener & MouseListener & MouseMotionListener >
{
	final private ImageWindow window;

	final private Canvas canvas;

	final private ImageJ ij;

	private T handler;

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
		handler = null;
	}

	/**
	 * Add new event handlers.
	 */
	final void takeOverGui( final T handler )
	{
		this.handler = handler;
		backupGui();
		clearGui();

		canvas.addKeyListener( handler );
		window.addKeyListener( handler );

		if ( ij != null )
			ij.addKeyListener( handler );

		canvas.addMouseMotionListener( handler );
		canvas.addMouseListener( handler );

		window.addMouseWheelListener( handler );
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
		if ( ij != null )
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
	 * Backup active event handlers for restore.
	 */
	private final void backupGui()
	{
		canvasKeyListeners = canvas.getKeyListeners();
		windowKeyListeners = window.getKeyListeners();
		if ( ij != null )
			ijKeyListeners = ij.getKeyListeners();
		canvasMouseListeners = canvas.getMouseListeners();
		canvasMouseMotionListeners = canvas.getMouseMotionListeners();
		windowMouseWheelListeners = window.getMouseWheelListeners();
	}

	/**
	 * Remove both ours and the backed up event handlers.
	 */
	private final void clearGui()
	{
		for ( final KeyListener l : canvasKeyListeners )
			canvas.removeKeyListener( l );
		for ( final KeyListener l : windowKeyListeners )
			window.removeKeyListener( l );
		if ( ij != null )
			for ( final KeyListener l : ijKeyListeners )
				ij.removeKeyListener( l );
		for ( final MouseListener l : canvasMouseListeners )
			canvas.removeMouseListener( l );
		for ( final MouseMotionListener l : canvasMouseMotionListeners )
			canvas.removeMouseMotionListener( l );
		for ( final MouseWheelListener l : windowMouseWheelListeners )
			window.removeMouseWheelListener( l );

		if ( handler != null )
		{
			canvas.removeKeyListener( handler );
			window.removeKeyListener( handler );
			if ( ij != null )
				ij.removeKeyListener( handler );
			canvas.removeMouseListener( handler );
			canvas.removeMouseMotionListener( handler );
			window.removeMouseWheelListener( handler );
		}
	}
}
