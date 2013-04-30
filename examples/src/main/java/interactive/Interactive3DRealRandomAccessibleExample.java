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

package interactive;

import interactive.fractals.MandelbulbRealRandomAccess;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.ui.InteractiveRealViewer3D;
import net.imglib2.util.Intervals;

public class Interactive3DRealRandomAccessibleExample
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final long maxIterations = 20;

		final RealRandomAccessible< LongType > mandelbulb = new RealRandomAccessible< LongType >()
		{
			@Override
			public int numDimensions()
			{
				return 3;
			}

			@Override
			public RealRandomAccess< LongType > realRandomAccess()
			{
				return new MandelbulbRealRandomAccess( maxIterations );
			}

			@Override
			public RealRandomAccess< LongType > realRandomAccess( final RealInterval interval )
			{
				return realRandomAccess();
			}
		};

		final int w = 400, h = 300;

		final double s = w / 4;
		final AffineTransform3D initial = new AffineTransform3D();
		initial.set(
			s, 0.0, 0.0, w / 2,
			0.0, s, 0.0, h / 2,
			0.0, 0.0, s, 0 );

		final LogoPainter logo = new LogoPainter();
		final Interval sourceInterval = Intervals.createMinMax( -2, -2, -2, 2, 2, 2 );
		final RealARGBConverter< LongType > converter = new RealARGBConverter< LongType >( 0, maxIterations );
		new InteractiveRealViewer3D< LongType >( w, h, mandelbulb, sourceInterval, initial, converter )
		{
			@Override
			public void drawScreenImage()
			{
				super.drawScreenImage();
				logo.paint( screenImage );
			}
		};
	}
}
