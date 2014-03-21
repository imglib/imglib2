package net.imglib2.display;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.RealFloatConverter;
import net.imglib2.display.projector.IterableIntervalProjector2D;
import net.imglib2.display.projector.RandomAccessibleProjector2D;
import net.imglib2.display.projector.XYRandomAccessibleProjector;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.view.Views;

public class ProjectorBenchmark
{
	public static < A, B > void benchmarkIterableIntervalProjector2D(
			final RandomAccessibleInterval< A > source,
			final IterableInterval< B > target,
			final Converter< A, B > converter )
	{
		final IterableIntervalProjector2D< A, B > projector =
				new IterableIntervalProjector2D< A, B >( 0, 1, source, target, converter );

		BenchmarkHelper.benchmarkAndPrint( 100, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int z = 0; z < source.dimension( 2 ); ++z )
				{
					projector.setPosition( z, 2 );
					projector.map();
				}
			}
		} );
	}

	public static < A, B > void benchmarkRandomAccessibleProjector2D(
			final RandomAccessibleInterval< A > source,
			final RandomAccessibleInterval< B > target,
			final Converter< A, B > converter )
	{
		final RandomAccessibleProjector2D< A, B > projector =
				new RandomAccessibleProjector2D< A, B >( 0, 1, source, target, converter );

		BenchmarkHelper.benchmarkAndPrint( 100, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int z = 0; z < source.dimension( 2 ); ++z )
				{
					projector.setPosition( z, 2 );
					projector.map();
				}
			}
		} );
	}

	public static < A, B > void benchmarkXYRandomAccessibleProjector(
			final RandomAccessibleInterval< A > source,
			final RandomAccessibleInterval< B > target,
			final Converter< A, B > converter )
	{
		final XYRandomAccessibleProjector< A, B > projector =
				new XYRandomAccessibleProjector< A, B >( source, target, converter );

		BenchmarkHelper.benchmarkAndPrint( 100, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int z = 0; z < source.dimension( 2 ); ++z )
				{
					projector.setPosition( z, 2 );
					projector.map();
				}
			}
		} );
	}

	public static void main( final String[] args )
	{
		final Img< FloatType > source = ArrayImgs.floats( 1000, 1000, 10 );
		final Img< FloatType > target = ArrayImgs.floats( 1000, 1000 );
		final Converter< FloatType, FloatType > converter = new RealFloatConverter< FloatType >();

		final AffineTransform3D t = new AffineTransform3D();
		final RandomAccessibleInterval< FloatType > view = Views.interval(
				RealViews.constantAffine(
						Views.interpolate( source, new NearestNeighborInterpolatorFactory< FloatType >() ),
						t ),
				source );

//		System.out.println( "IterableIntervalProjector2D Img" );
//		benchmarkIterableIntervalProjector2D( source, target, converter );

//		System.out.println( "IterableIntervalProjector2D Affine" );
//		benchmarkIterableIntervalProjector2D( view, target, converter );

//		System.out.println( "RandomAccessibleProjector2D Img" );
//		benchmarkRandomAccessibleProjector2D( source, target, converter );

//		System.out.println( "RandomAccessibleProjector2D Affine" );
//		benchmarkRandomAccessibleProjector2D( view, target, converter );

//		System.out.println( "XYRandomAccessibleProjector Img" );
//		benchmarkXYRandomAccessibleProjector( source, target, converter );

//		System.out.println( "XYRandomAccessibleProjector Affine" );
//		benchmarkXYRandomAccessibleProjector( view, target, converter );

		for ( int i = 0; i < 10; ++i )
		{
			System.out.println( "IterableIntervalProjector2D Img" );
			benchmarkIterableIntervalProjector2D( source, target, converter );
			System.out.println( "IterableIntervalProjector2D Affine" );
			benchmarkIterableIntervalProjector2D( view, target, converter );

			System.out.println( "RandomAccessibleProjector2D Img" );
			benchmarkRandomAccessibleProjector2D( source, target, converter );
			System.out.println( "RandomAccessibleProjector2D Affine" );
			benchmarkRandomAccessibleProjector2D( view, target, converter );

			System.out.println( "XYRandomAccessibleProjector Img" );
			benchmarkXYRandomAccessibleProjector( source, target, converter );
			System.out.println( "XYRandomAccessibleProjector Affine" );
			benchmarkXYRandomAccessibleProjector( view, target, converter );

			System.out.println( "--------------------------------" );
		}
	}
}
