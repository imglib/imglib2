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

package net.imglib2.ui.ij;

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
	/**
	 * Backup and clear current key and mouse listeners.
	 *
	 * @param imp
	 *            the ImagePlus in which to install the GUI.
	 */
	public GUI( final ImagePlus imp )
	{
		window = imp.getWindow();
		canvas = imp.getCanvas();
		ij = IJ.getInstance();
		backup = backupGui();
		clearGui();
		handlers = new ArrayList< Object >();
	}

	/**
	 * Add new event handler.
	 */
	public void addHandler( final Object handler )
	{
		handlers.add( handler );

		if ( KeyListener.class.isInstance( handler ) )
		{
			canvas.addKeyListener( ( KeyListener ) handler );
			window.addKeyListener( ( KeyListener ) handler );
			if ( ij != null )
				ij.addKeyListener( ( KeyListener ) handler );
		}

		if ( MouseMotionListener.class.isInstance( handler ) )
			canvas.addMouseMotionListener( ( MouseMotionListener ) handler );

		if ( MouseListener.class.isInstance( handler ) )
			canvas.addMouseListener( ( MouseListener ) handler );

		if ( MouseWheelListener.class.isInstance( handler ) )
			canvas.addMouseWheelListener( ( MouseWheelListener ) handler );
	}

	/**
	 * Add new event handlers.
	 */
	public void addHandlers( final Collection< Object > handlers )
	{
		for ( final Object h : handlers )
			addHandler( h );
	}

	/**
	 * Restore the previously active Event handlers.
	 */
	public void restoreGui()
	{
		restoreGui( backup );
	}

	/**
	 * Stores current mouse and keyboard listeners.
	 */
	protected class State
	{
		final KeyListener[] windowKeyListeners;

		final KeyListener[] canvasKeyListeners;

		final KeyListener[] ijKeyListeners;

		final MouseListener[] canvasMouseListeners;

		final MouseMotionListener[] canvasMouseMotionListeners;

		final MouseWheelListener[] canvasMouseWheelListeners;

		public State()
		{
			canvasKeyListeners = canvas.getKeyListeners();
			windowKeyListeners = window.getKeyListeners();
			ijKeyListeners = ( ij == null ) ? null : ij.getKeyListeners();
			canvasMouseListeners = canvas.getMouseListeners();
			canvasMouseMotionListeners = canvas.getMouseMotionListeners();
			canvasMouseWheelListeners = canvas.getMouseWheelListeners();
		}
	}

	final protected ImageWindow window;

	final protected Canvas canvas;

	final protected ImageJ ij;

	final protected State backup;

	final protected ArrayList< Object > handlers;

	/**
	 * Restore the event handlers from a {@link State}.
	 *
	 * @param state
	 *            the state to restore.
	 */
	protected void restoreGui( final State state )
	{
		clearGui();
		for ( final KeyListener l : state.canvasKeyListeners )
			canvas.addKeyListener( l );
		for ( final KeyListener l : state.windowKeyListeners )
			window.addKeyListener( l );
		if ( ij != null )
			for ( final KeyListener l : state.ijKeyListeners )
				ij.addKeyListener( l );
		for ( final MouseListener l : state.canvasMouseListeners )
			canvas.addMouseListener( l );
		for ( final MouseMotionListener l : state.canvasMouseMotionListeners )
			canvas.addMouseMotionListener( l );
		for ( final MouseWheelListener l : state.canvasMouseWheelListeners )
			canvas.addMouseWheelListener( l );
	}

	/**
	 * Backup active event handlers for restore.
	 */
	protected State backupGui()
	{
		return new State();
	}

	/**
	 * Remove all event handlers.
	 */
	protected void clearGui()
	{
		KeyListener[] keyListeners = canvas.getKeyListeners();
		for ( final KeyListener l : keyListeners )
			canvas.removeKeyListener( l );

		keyListeners = window.getKeyListeners();
		for ( final KeyListener l : keyListeners )
			window.removeKeyListener( l );

		if ( ij != null )
		{
			keyListeners = ij.getKeyListeners();
			for ( final KeyListener l : keyListeners )
				ij.removeKeyListener( l );
		}

		final MouseListener[] mouseListeners = canvas.getMouseListeners();
		for ( final MouseListener l : mouseListeners )
			canvas.removeMouseListener( l );

		final MouseMotionListener[] mouseMotionListeners = canvas.getMouseMotionListeners();
		for ( final MouseMotionListener l : mouseMotionListeners )
			canvas.removeMouseMotionListener( l );

		final MouseWheelListener[] mouseWheelListeners = window.getMouseWheelListeners();
		for ( final MouseWheelListener l : mouseWheelListeners )
			canvas.removeMouseWheelListener( l );
	}
}
