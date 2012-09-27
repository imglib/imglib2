package interactive;

import ij.IJ;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.ui.DisplayTypes;
import net.imglib2.ui.InteractiveViewer2D;
import net.imglib2.ui.InteractiveViewer3D;
import net.imglib2.view.Views;

public class InteractiveImageViewerExample
{
	private static < T extends Type< T > & Comparable< T > > void getMinMax( final IterableInterval< T > source, final T minValue, final T maxValue )
	{
		for ( final T t : source )
			if ( minValue.compareTo( t ) > 0 )
				minValue.set( t );
			else if ( maxValue.compareTo( t ) < 0 )
				maxValue.set( t );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval )
	{
		final T min = Views.iterable( interval ).firstElement().copy();
		final T max = min.copy();
		getMinMax( Views.iterable( interval ), min, max );
		show( interval, min, max );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval, final long width, final long height )
	{
		final T min = Views.iterable( interval ).firstElement().copy();
		final T max = min.copy();
		getMinMax( Views.iterable( interval ), min, max );
		show( interval, min, max, width, height );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval, final T min, final T max )
	{
		show( interval, min, max, interval.dimension( 0 ), interval.dimension( 1 ) );
	}

	public static < T extends RealType< T > & NativeType< T > > void show( final RandomAccessibleInterval< T > interval, final T min, final T max, final long width, final long height )
	{
		final RandomAccessible< T > source = Views.extendValue( interval, min );
		final RealARGBConverter< T > converter = new RealARGBConverter< T >( min.getRealDouble(), max.getRealDouble() );
		final LogoPainter logo = new LogoPainter();
		final int n = interval.numDimensions();
		if ( n == 2 )
		{
			double yScale = 1;
			if ( interval instanceof CalibratedSpace )
			{
				final CalibratedSpace cs = ( CalibratedSpace ) interval;
				yScale = cs.calibration( 1 ) / cs.calibration( 0 );
				if ( Double.isNaN( yScale ) || Double.isInfinite( yScale ) )
					yScale = 1;
			}

			final AffineTransform2D initial = new AffineTransform2D();
			initial.set(
				1, 0, ( width - interval.dimension( 0 ) ) / 2.0 - interval.min( 0 ),
				0, yScale, ( height - interval.dimension( 1 ) * yScale ) / 2.0 - interval.min( 1 ) * yScale );

//			new InteractiveViewer2D< T >( ( int ) width, ( int ) height, source, initial, converter, DisplayTypes.DISPLAY_IMAGEPLUS )
			new InteractiveViewer2D< T >( ( int ) width, ( int ) height, source, initial, converter, DisplayTypes.DISPLAY_SWING )
			{
				@Override
				public void drawScreenImage()
				{
					super.drawScreenImage();
					logo.paint( screenImage );
				}
			};
		}
		else if ( n == 3 )
		{
			double yScale = 1;
			double zScale = 1;
			if ( interval instanceof CalibratedSpace )
			{
				final CalibratedSpace cs = ( CalibratedSpace ) interval;
				yScale = cs.calibration( 1 ) / cs.calibration( 0 );
				zScale = cs.calibration( 2 ) / cs.calibration( 0 );
				if ( Double.isNaN( yScale ) || Double.isInfinite( yScale ) )
					yScale = 1;
				if ( Double.isNaN( zScale ) || Double.isInfinite( zScale ) )
					zScale = 1;
			}

			final AffineTransform3D initial = new AffineTransform3D();
			initial.set(
				1,      0,      0, ( width - interval.dimension( 0 ) ) / 2.0 - interval.min( 0 ),
				0, yScale,      0, ( height - interval.dimension( 1 ) * yScale ) / 2.0 - interval.min( 1 ) * yScale,
				0,      0, zScale, -interval.dimension( 2 ) * zScale / 2.0 );

//			final InteractiveViewer3D< T > viewer = new InteractiveViewer3D< T >( ( int ) width, ( int ) height, source, interval, initial, converter, DisplayTypes.DISPLAY_IMAGEPLUS )
			final InteractiveViewer3D< T > viewer = new InteractiveViewer3D< T >( ( int ) width, ( int ) height, source, interval, initial, converter, DisplayTypes.DISPLAY_SWING )
			{
				@Override
				public void drawScreenImage()
				{
					super.drawScreenImage();
					logo.paint( screenImage );
				}
			};
		}
		else
		{
			IJ.error( "Interactive viewer only supports 2D and 3D images" );
		}
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final String filename = "DrosophilaWing.tif";
//		final String filename = "/home/tobias/workspace/data/DM_MV_110629_TL0_Ch0_Angle0_fragment.tif";
		final ImgPlus< FloatType > img = new ImgOpener().openImg( filename, new ArrayImgFactory< FloatType >(), new FloatType() );
//		show( Views.interval( img, FinalInterval.createMinSize( 200, 10, 200, 200 ) ) );
		show ( img );
	}
}
