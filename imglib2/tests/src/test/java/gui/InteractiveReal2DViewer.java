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

package gui;

import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

public class InteractiveReal2DViewer< T extends NumericType< T > & NativeType< T > > extends AbstractInteractive2DViewer< T >
{
	/**
	 * the {@link RealRandomAccessible} to display
	 */
	final protected RealRandomAccessible< T > source;

	/**
	 *
	 * @param width
	 *            width of the display window
	 * @param height
	 *            height of the display window
	 * @param source
	 *            the {@link RandomAccessible} to display
	 * @param converter
	 *            converts {@link #source} type T to ARGBType for display
	 * @param initialTransform
	 *            initial transformation to apply to the {@link #source}
	 */
	public InteractiveReal2DViewer( final int width, final int height, final RealRandomAccessible< T > source, final Converter< T, ARGBType > converter, final AffineTransform2D initialTransform )
	{
		super( width, height, converter, initialTransform );
		this.source = source;

		projector = createProjector();

		requestRepaint();
		startPainter();
	}

	protected XYRandomAccessibleProjector< T, ARGBType > createProjector()
	{
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( source, reducedAffineCopy.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
}
