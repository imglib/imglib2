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

import net.imglib2.loops.ClassCopyProvider;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

class RealARGBColorConverterFactory
{
	@SuppressWarnings( "rawtypes" )
	private static ClassCopyProvider< RealARGBColorConverter > provider;

	@SuppressWarnings( "unchecked" )
	public static < R extends RealType< ? > > RealARGBColorConverter< R > create( final R type, final double min, final double max )
	{
		if ( provider == null )
		{
			synchronized ( RealARGBColorConverterFactory.class )
			{
				if ( provider == null )
					provider = new ClassCopyProvider<>( Imp.class, RealARGBColorConverter.class, double.class, double.class );
			}
		}
		return provider.newInstanceForKey( type.getClass(), min, max );
	}

	public static class Imp< R extends RealType< ? > > implements RealARGBColorConverter< R >
	{
		private double min = 0;

		private double max = 1;

		private final ARGBType color = new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) );

		private int A;

		private double scaleR;

		private double scaleG;

		private double scaleB;

		private int black;

		public Imp( final double min, final double max )
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
			black = ARGBType.rgba( 0, 0, 0, A );
		}

		@Override
		public void convert( final R input, final ARGBType output )
		{
			final double v = input.getRealDouble() - min;
			if ( v < 0 )
			{
				output.set( black );
			}
			else
			{
				final int r0 = ( int ) ( scaleR * v + 0.5 );
				final int g0 = ( int ) ( scaleG * v + 0.5 );
				final int b0 = ( int ) ( scaleB * v + 0.5 );
				final int r = Math.min( 255, r0 );
				final int g = Math.min( 255, g0 );
				final int b = Math.min( 255, b0 );
				output.set( ARGBType.rgba( r, g, b, A ) );
			}
		}
	}
}
