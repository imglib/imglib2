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

package tobias;

import ij.ImagePlus;
import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

/**
 * Base class for interactive ImgLib2 examples.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractInteractiveExample< T extends NumericType< T > >
{

	/**
	 * Display.
	 */
	protected ImagePlus imp;

	/**
	 * Wrapped by {@link #imp}.
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * Currently active projector, used by {@link MappingThread} to re-paint the
	 * display. It maps the source data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * ImgLib2 logo overlay painter.
	 */
	final protected LogoPainter logo;

	/**
	 * Repaint display.
	 *
	 * @see AbstractInteractiveExample#projector
	 * @see AbstractInteractiveExample#screenImage
	 * @see AbstractInteractiveExample#logo
	 * @see AbstractInteractiveExample#imp
	 */
	final public class MappingThread extends Thread
	{
		private boolean pleaseRepaint;

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
				{
					copyState();
					projector.map();
					logo.paint( screenImage );
					// imp.setImage( screenImage.image() );
					visualize();
					imp.updateAndDraw();
				}
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

	public AbstractInteractiveExample()
	{
		logo = new LogoPainter();
	}

	abstract protected void copyState();

	abstract protected void visualize();

	final static protected String NL = System.getProperty( "line.separator" );

	final protected NearestNeighborInterpolatorFactory< T > nnFactory = new NearestNeighborInterpolatorFactory< T >();

	final protected NLinearInterpolatorFactory< T > nlFactory = new NLinearInterpolatorFactory< T >();

	protected int interpolation = 0;

	protected MappingThread painter;

	abstract protected XYRandomAccessibleProjector< T, ARGBType > createProjector( final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory );

	protected void toggleInterpolation()
	{
		++interpolation;
		interpolation %= 2;
		switch ( interpolation )
		{
		case 0:
			projector = createProjector( nnFactory );
			break;
		case 1:
			projector = createProjector( nlFactory );
			break;
		}
	}
}
