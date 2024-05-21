/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

public abstract class ScaledARGBConverter< T > implements ColorConverter, Converter< T, ARGBType >
{
	protected double min = 0;

	protected double max = 1;

	protected double scale;

	private ScaledARGBConverter( final double min, final double max )
	{
		this.min = min;
		this.max = max;
		update();
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

	@Override
	public ARGBType getColor()
	{
		return new ARGBType();
	}

	@Override
	public void setColor( final ARGBType c )
	{}

	@Override
	public boolean supportsColor()
	{
		return false;
	}

	private void update()
	{
		scale = 255.0 / ( max - min );
	}

	int getScaledColor( final int color )
	{
		final int a = ARGBType.alpha( color );
		final int r = Math.min( 255, ( int ) ( scale * Math.max( 0, ARGBType.red( color ) - min ) + 0.5 ) );
		final int g = Math.min( 255, ( int ) ( scale * Math.max( 0, ARGBType.green( color ) - min ) + 0.5 ) );
		final int b = Math.min( 255, ( int ) ( scale * Math.max( 0, ARGBType.blue( color ) - min ) + 0.5 ) );
		return ARGBType.rgba( r, g, b, a );
	}

	public static class ARGB extends ScaledARGBConverter< ARGBType >
	{
		public ARGB( final double min, final double max )
		{
			super( min, max );
		}

		@Override
		public void convert( final ARGBType input, final ARGBType output )
		{
			output.set( getScaledColor( input.get() ) );
		}
	}

	public static class VolatileARGB extends ScaledARGBConverter< VolatileARGBType >
	{
		public VolatileARGB( final double min, final double max )
		{
			super( min, max );
		}

		@Override
		public void convert( final VolatileARGBType input, final ARGBType output )
		{
			output.set( getScaledColor( input.get().get() ) );
		}
	}
}
