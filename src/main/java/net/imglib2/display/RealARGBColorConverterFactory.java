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
