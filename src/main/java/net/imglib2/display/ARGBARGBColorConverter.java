/*-
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

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;
import net.imglib2.util.Util;

public abstract class ARGBARGBColorConverter<R> implements ColorConverter, Converter< R, ARGBType >
{
	protected double min = 0;

	protected double max = 1;

	protected final ARGBType color = new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) );

	protected int A;

	protected double scaleR;

	protected double scaleG;

	protected double scaleB;
	
	protected int black = 0;
	
	public ARGBARGBColorConverter( final double min, final double max )
	{
		this.min = min;
		this.max = max;
		update();
	}

	@Override
	public ARGBType getColor()
	{
		return color.copy();
	}

	@Override
	public void setColor( final ARGBType c )
	{
		color.set( c );
		update();
	}

	@Override
	public boolean supportsColor()
	{
		return true;
	}

	@Override
	public double getMin()
	{
		return min;
	}

	@Override
	public double getMax()
	{
		return max;
	}

	@Override
	public void setMax( final double max )
	{
		this.max = max;
		update();
	}

	@Override
	public void setMin( final double min )
	{
		this.min = min;
		update();
	}

	private void update()
	{
		final double scale = 1.0 / ( max - min );
		final int value = color.get();
		A = ARGBType.alpha( value );
		scaleR = ARGBType.red( value ) * scale;
		scaleG = ARGBType.green( value ) * scale;
		scaleB = ARGBType.blue( value ) * scale;
		black = 0;
	}
	
	int convertColor( final int color )
	{
		final int a = ARGBType.alpha( color );
		int r = ARGBType.red( color );
		int g = ARGBType.green( color );
		int b = ARGBType.blue( color );
		
		final int v = Math.min( 255, Math.max( 0, ( r + g + b ) / 3 ) );
		
		final int newR = (int)Math.min( 255, Util.round( scaleR * v ));
		final int newG = (int)Math.min( 255, Util.round( scaleG * v ));
		final int newB = (int)Math.min( 255, Util.round( scaleB * v ));
		
		return ARGBType.rgba( newR, newG, newB, a );
	}

	/**
	 * A converter from a ARGB to ARGB that initially converts the input color to grayscale, 
	 * then scales the resulting grayscale value with the set color. 
	 * <p>
	 * This can be useful if a grayscale image is imported as ARGB and one wants to change
	 * the hue for visualization / overlay.
	 * 
	 * @author John Bogovic
	 *
	 */
	public static class ToGray extends ARGBARGBColorConverter<ARGBType>
	{
		public ToGray( final double min, final double max )
		{
			super( min, max );
		}

		@Override
		public void convert( final ARGBType input, final ARGBType output )
		{
			output.set( convertColor( input.get() ));
		}
	}
	
	public static class VolatileToGray extends ARGBARGBColorConverter<VolatileARGBType>
	{
		public VolatileToGray( final double min, final double max )
		{
			super( min, max );
		}

		@Override
		public void convert( final VolatileARGBType input, final ARGBType output )
		{
			output.set( convertColor( input.get().get() ));
		}
	}
}
