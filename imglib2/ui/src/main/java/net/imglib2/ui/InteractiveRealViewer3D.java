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

package net.imglib2.ui;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

/**
 * 
 * @param <T>
 *
 * @author TobiasPietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class InteractiveRealViewer3D< T extends NumericType< T > > extends AbstractInteractiveViewer3D< T >
{
	/**
	 * The {@link RandomAccessible} to display
	 */
	final protected RealRandomAccessible< T > source;

	/**
	 * Converts {@link #source} type T to ARGBType for display
	 */
	final protected Converter< ? super T, ARGBType > converter;

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final Converter< ? super T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final Converter< ? super T, ARGBType > converter, final DisplayTypes displayType )
	{
		this( width, height, source, sourceInterval, new AffineTransform3D(), converter, displayType );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		this( width, height, source, sourceInterval, sourceTransform, converter, DisplayTypes.DISPLAY_SWING );
	}

	public InteractiveRealViewer3D( final int width, final int height, final RealRandomAccessible< T > source, final Interval sourceInterval, final AffineTransform3D sourceTransform, final Converter< ? super T, ARGBType > converter, final DisplayTypes displayType )
	{
		super( width, height, sourceInterval, sourceTransform, displayType );
		this.source = source;
		this.converter = converter;
		projector = createProjector();
		display.startPainter();
	}

	@Override
	protected XYRandomAccessibleProjector< T, ARGBType > createProjector()
	{
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( source, sourceToScreen.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}
}
