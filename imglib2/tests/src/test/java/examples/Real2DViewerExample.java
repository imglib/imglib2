package examples;
import java.util.ArrayList;

import net.imglib2.converter.Converter;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.LongType;
import fractals.MandelbrotRealRandomAccessible;
import gui.InteractiveReal2DViewer;

public class Real2DViewerExample< T extends NumericType< T > & NativeType< T > >
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		final int width = 800;
		final int height = 600;

		final int maxIterations = 100;
		final MandelbrotRealRandomAccessible mandelbrot = new MandelbrotRealRandomAccessible( maxIterations );

		final AffineTransform2D transform = new AffineTransform2D();
		transform.scale( 200 );
		transform.translate( width / 2.0, height / 2.0 );

		final RealARGBConverter< LongType > converter = new RealARGBConverter< LongType >( 0, maxIterations );
		new InteractiveReal2DViewer< LongType >( width, height, mandelbrot, converter, transform, new ArrayList< Object >() );

//		final Converter< LongType, ARGBType > lut = new Converter< LongType, ARGBType >()
//		{
//			final protected int[] rgb = new int[ maxIterations + 1 ];
//			{
//				for ( int i = 0; i <= maxIterations; ++i )
//				{
//					final double r = 1.0 - ( double )i / maxIterations;
//					final double g = Math.sin( Math.PI * r );
//					final double b = 0.5 - 0.5 * Math.cos( Math.PI * g );
//
//					final int ri = ( int )Math.round( Math.max( 0, 255 * r ) );
//					final int gi = ( int )Math.round( Math.max( 0, 255 * g ) );
//					final int bi = ( int )Math.round( Math.max( 0, 255 * b ) );
//
//					rgb[ i ] = ( ( ( ri << 8 ) | gi ) << 8 ) | bi | 0xff000000;
//				}
//			}
//
//			@Override
//			public void convert( final LongType input, final ARGBType output )
//			{
//				output.set( rgb[ input.getInteger() ] );
//			}
//		};
//
//		new InteractiveReal2DViewer< LongType >( width, height, mandelbrot, lut, transform, new ArrayList< Object >() );
	}

}
