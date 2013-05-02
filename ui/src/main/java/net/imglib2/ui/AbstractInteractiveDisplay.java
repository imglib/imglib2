/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.ui;


/**
 * Base class for interactive ImgLib2 examples.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractInteractiveDisplay
{
	/**
	 * Thread to repaint display.
	 */
	final public class MappingThread extends Thread
	{
		private boolean pleaseRepaint = true;

		public MappingThread()
		{
			this.setName( "MappingThread" );
		}

		@Override
		public void run()
		{
			while ( !isInterrupted() )
			{
				final boolean b;
				synchronized ( this )
				{
					b = pleaseRepaint;
					pleaseRepaint = false;
				}
				if ( b )
					paint();
				synchronized ( this )
				{
					try
					{
						if ( !pleaseRepaint )
							wait();
					}
					catch ( final InterruptedException e )
					{}
				}
			}
		}

		/**
		 * request repaint.
		 */
		public void repaint()
		{
			synchronized ( this )
			{
				pleaseRepaint = true;
				notify();
			}
		}
	}

	private final MappingThread painter;

	/**
	 * Set up a thread to trigger painting.
	 * The painter thread is not started yet.
	 */
	public AbstractInteractiveDisplay()
	{
		painter = new MappingThread();
	}

	/**
	 * Start the painter thread.
	 */
	public void startPainter()
	{
		painter.start();
	}

	/**
	 * Stop the painter thread.
	 */
	public void stopPainter()
	{
		painter.interrupt();
	}

	/**
	 * Request a repaint of the display from the painter thread. The painter
	 * thread will trigger a {@link #paint()} as soon as possible (that is,
	 * immediately or after the currently running {@link #paint()} has
	 * completed).
	 */
	public void requestRepaint()
	{
		painter.repaint();
	}

	/**
	 * This is called by the painter thread to repaint the display.
	 */
	public abstract void paint();

	/**
	 * Add new event handler.
	 */
	public abstract void addHandler( final Object handler );
}
