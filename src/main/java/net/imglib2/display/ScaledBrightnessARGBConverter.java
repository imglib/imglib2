/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */
package net.imglib2.display;

import net.imglib2.type.numeric.ARGBType;

/**
 * A {@link ScaledARGBConverter} that scales the brightness of the colors based
 * on the scale value computed from the min and max. The min and max are e.g.
 * set in the BDV side panel, so this gives a way to control the visibility of
 * ARGB images, such as segmentation label images.
 */
public class ScaledBrightnessARGBConverter
{

	private static final int getScaledColor( final int color, final double scale )
	{
		final int r = ARGBType.red( color );
		final int g = ARGBType.green( color );
		final int b = ARGBType.blue( color );

		final int nr = ( int ) Math.min( 255, r / scale );
		final int ng = ( int ) Math.min( 255, g / scale );
		final int nb = ( int ) Math.min( 255, b / scale );

		return 0xff000000 | ( nr << 16 ) | ( ng << 8 ) | nb;
	}

	public static class ARGB extends ScaledARGBConverter.ARGB
	{
		public ARGB( final double min, final double max )
		{
			super( min, max );
		}

		@Override
		int getScaledColor( final int color )
		{
			return ScaledBrightnessARGBConverter.getScaledColor( color, scale );
		}
	}

	public static class VolatileARGB extends ScaledARGBConverter.VolatileARGB
	{
		public VolatileARGB( final double min, final double max )
		{
			super( min, max );
		}

		@Override
		int getScaledColor( final int color )
		{
			return ScaledBrightnessARGBConverter.getScaledColor( color, scale );
		}
	}
}
