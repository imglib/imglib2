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

		// Convert RGB to normalized float values
		final float rf = r / 255f;
		final float gf = g / 255f;
		final float bf = b / 255f;

		final float max = Math.max( rf, Math.max( gf, bf ) );
		final float min = Math.min( rf, Math.min( gf, bf ) );
		final float delta = max - min;

		// Calculate HSB values
		float hue;
		float saturation;
		float brightness = max;

		// Saturation
		saturation = ( max == 0 ) ? 0 : delta / max;

		// Hue
		if ( delta == 0 )
		{
			hue = 0;
		}
		else if ( max == rf )
		{
			hue = ( ( gf - bf ) / delta ) / 6f;
			if ( hue < 0 )
				hue += 1f;
		}
		else if ( max == gf )
		{
			hue = ( 2f + ( bf - rf ) / delta ) / 6f;
		}
		else
		{
			hue = ( 4f + ( rf - gf ) / delta ) / 6f;
		}

		// Scale brightness
		brightness = ( float ) Math.min( 1f, brightness / scale );

		// Convert HSB back to RGB
		return hsbToRGB( hue, saturation, brightness );
	}

	private static int hsbToRGB( final float h, final float s, final float b )
	{
		int r = 0, g = 0, bl = 0;

		if ( s == 0 )
		{
			r = g = bl = ( int ) ( b * 255f + 0.5f );
		}
		else
		{
			final float h6 = ( h - ( float ) Math.floor( h ) ) * 6f;
			final float f = h6 - ( float ) Math.floor( h6 );
			final float p = b * ( 1f - s );
			final float q = b * ( 1f - s * f );
			final float t = b * ( 1f - s * ( 1f - f ) );

			switch ( ( int ) h6 )
			{
			case 0:
				r = ( int ) ( b * 255f + 0.5f );
				g = ( int ) ( t * 255f + 0.5f );
				bl = ( int ) ( p * 255f + 0.5f );
				break;
			case 1:
				r = ( int ) ( q * 255f + 0.5f );
				g = ( int ) ( b * 255f + 0.5f );
				bl = ( int ) ( p * 255f + 0.5f );
				break;
			case 2:
				r = ( int ) ( p * 255f + 0.5f );
				g = ( int ) ( b * 255f + 0.5f );
				bl = ( int ) ( t * 255f + 0.5f );
				break;
			case 3:
				r = ( int ) ( p * 255f + 0.5f );
				g = ( int ) ( q * 255f + 0.5f );
				bl = ( int ) ( b * 255f + 0.5f );
				break;
			case 4:
				r = ( int ) ( t * 255f + 0.5f );
				g = ( int ) ( p * 255f + 0.5f );
				bl = ( int ) ( b * 255f + 0.5f );
				break;
			case 5:
				r = ( int ) ( b * 255f + 0.5f );
				g = ( int ) ( p * 255f + 0.5f );
				bl = ( int ) ( q * 255f + 0.5f );
				break;
			}
		}

		return 0xff000000 | ( r << 16 ) | ( g << 8 ) | bl;
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
