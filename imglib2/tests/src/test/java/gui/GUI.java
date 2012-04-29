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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Register mouse and key listeners. Backup and restore old listeners.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class GUI
{
	final protected ImageWindow window;

	final protected Canvas canvas;

	final protected ImageJ ij;

	protected ArrayList< Object > handlers = new ArrayList< Object >();

	/* backup */
	protected KeyListener[] windowKeyListeners;

	protected KeyListener[] canvasKeyListeners;

	protected KeyListener[] ijKeyListeners;

	protected MouseListener[] canvasMouseListeners;

	private MouseMotionListener[] canvasMouseMotionListeners;

	private MouseWheelListener[] windowMouseWheelListeners;

	GUI( final ImagePlus imp )
	{
		window = imp.getWindow();
		canvas = imp.getCanvas();

		ij = IJ.getInstance();
		handlers.clear();
	}

	/**
	 * Add new event handlers.
	 */
	final void takeOverGui( final Collection< Object > handlers )
	{
		this.handlers.addAll( handlers );
		backupGui();
		clearGui();
		
		for ( final Object h : handlers )
		{
			if ( KeyListener.class.isInstance( h ) )
			{
				canvas.addKeyListener( ( KeyListener )h );
				window.addKeyListener( ( KeyListener )h );
				if ( ij != null )
					ij.addKeyListener( ( KeyListener )h );
			}
			if ( MouseMotionListener.class.isInstance( h ) )
				canvas.addMouseMotionListener( ( MouseMotionListener )h );
			
			if ( MouseListener.class.isInstance( h ) )
				canvas.addMouseListener( ( MouseListener )h );
			
			if ( MouseWheelListener.class.isInstance( h ) )
				window.addMouseWheelListener( ( MouseWheelListener )h );
		}
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

		for ( final Object h : handlers )
		{
			if ( KeyListener.class.isInstance( h ) )
			{
				canvas.removeKeyListener( ( KeyListener )h );
				window.removeKeyListener( ( KeyListener )h );
				if ( ij != null )
					ij.removeKeyListener( ( KeyListener )h );
			}
			if ( MouseMotionListener.class.isInstance( h ) )
				canvas.removeMouseMotionListener( ( MouseMotionListener )h );
			
			if ( MouseListener.class.isInstance( h ) )
				canvas.removeMouseListener( ( MouseListener )h );
			
			if ( MouseWheelListener.class.isInstance( h ) )
				window.removeMouseWheelListener( ( MouseWheelListener )h );
		}
	}
}
