package examples;
import fractals.MandelbrotRealRandomAccessible;
import gui.InteractiveReal2DViewer;

import java.util.ArrayList;

import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.io.ImgIOException;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.LongType;

public class Real2DViewerExample< T extends NumericType< T > & NativeType< T > >
{
	private final int width = 800;
	private final int height = 600;

	public Real2DViewerExample( final RealRandomAccessible< T > source, final Converter< T, ARGBType > converter )
	{
		/* center shift */
		final AffineTransform2D centerShift = new AffineTransform2D();
		centerShift.set(
				1, 0, -width / 2.0,
				0, 1, -height / 2.0 );

		/* center un-shift */
		final AffineTransform2D centerUnShift = new AffineTransform2D();
		centerUnShift.set(
				1, 0, width / 2.0,
				0, 1, height / 2.0 );

		/* initialize rotation */
		final AffineTransform2D rotation = new AffineTransform2D();
		rotation.scale( 200 );

		rotation.preConcatenate( centerUnShift );
		
		new InteractiveReal2DViewer< T >( width, height, source, converter, rotation, new ArrayList< Object >() );
	}

	final static public void main( final String[] args ) throws ImgIOException
	{
		final int maxIterations = 100;
		
		final Converter< LongType, ARGBType > lut = new Converter< LongType, ARGBType >()
		{
			
			final protected int[] rgb = new int[ maxIterations + 1 ];
			{
				for ( int i = 0; i <= maxIterations; ++i )
				{
					final double r = 1.0 - ( double )i / maxIterations;
					final double g = Math.sin( Math.PI * r );
					final double b = 0.5 - 0.5 * Math.cos( Math.PI * g );
					
					final int ri = ( int )Math.round( Math.max( 0, 255 * r ) );
					final int gi = ( int )Math.round( Math.max( 0, 255 * g ) );
					final int bi = ( int )Math.round( Math.max( 0, 255 * b ) );
					
					rgb[ i ] = ( ( ( ri << 8 ) | gi ) << 8 ) | bi | 0xff000000;
				}
			}
			
			@Override
			public void convert( final LongType input, final ARGBType output )
			{
				output.set( rgb[ input.getInteger() ] );
			}
		};
		
		final MandelbrotRealRandomAccessible mandelbrot = new MandelbrotRealRandomAccessible( maxIterations );

		new Real2DViewerExample< LongType >( mandelbrot, lut );
	}

}
