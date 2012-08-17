package net.imglib2.algorithm.region.localneighborhood;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhood;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhood2;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhoodCursor;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhoodCursor2;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class LocalMaximaBenchmark
{
	public static < T extends Type< T > & Comparable< T > > int findLocalMaximaNeighborhood( final RandomAccessibleInterval< T > img )
	{
		// final ArrayList< Point > maxima = new ArrayList< Point >();
		int nMaxima = 0;

		final Cursor< T > center = Views.iterable( Views.interval( img, Intervals.expand( img, -1 ) ) ).localizingCursor();
		final LocalNeighborhood< T > neighborhood = new LocalNeighborhood< T >( img, center );
		final LocalNeighborhoodCursor< T > nc = neighborhood.cursor();
		A: while ( center.hasNext() )
		{
			final T t = center.next();
			nc.updateCenter( center );
			while ( nc.hasNext() )
			{
				final T n = nc.next();
				if ( n.compareTo( t ) > 0 )
					continue A;
			}
			// maxima.add( new Point( center ) );
			++nMaxima;
		}

		return nMaxima;
	}

	public static < T extends Type< T > & Comparable< T > > int findLocalMaximaNeighborhood2( final RandomAccessibleInterval< T > img )
	{
		// final ArrayList< Point > maxima = new ArrayList< Point >();
		int nMaxima = 0;

		final Cursor< T > center = Views.iterable( Views.interval( img, Intervals.expand( img, -1 ) ) ).localizingCursor();
		final LocalNeighborhood2< T > neighborhood = new LocalNeighborhood2< T >( img, center );
		final LocalNeighborhoodCursor2< T > nc = neighborhood.cursor();
		A: while ( center.hasNext() )
		{
			final T t = center.next();
			neighborhood.updateCenter( center );
			nc.reset();
			while ( nc.hasNext() )
			{
				final T n = nc.next();
				if ( n.compareTo( t ) > 0 )
					continue A;
			}
			// maxima.add( new Point( center ) );
			++nMaxima;
		}
		return nMaxima;
	}

	public static < T extends Type< T > & Comparable< T > > int countLocalMaxima( final RandomAccessibleInterval< T > img, final Shape shape )
	{
		int nMaxima = 0;
		final RandomAccessibleInterval< T > source = Views.interval( img, Intervals.expand( img, -1 ) );
		final Cursor< T > center = Views.iterable( source ).cursor();
A:		for ( final Neighborhood< T > neighborhood : shape.neighborhoods( source ) )
		{
			final T c = center.next();
			for ( final T t : neighborhood )
				if ( t.compareTo( c ) > 0 )
					continue A;
			++nMaxima;
		}
		return nMaxima;
	}

	public static < T extends Type< T > & Comparable< T > > int findLocalMaximaNeighborhood6( final RandomAccessibleInterval< T > img )
	{
		final RectangleShape neighborhoods = new RectangleShape( 1, true );
		return countLocalMaxima( img, neighborhoods );
	}

	public static void main( final String[] args )
	{
		final int numRuns = 20;
		final boolean printIndividualTimes = false;
		final long[] dimensions = new long[] { 200, 200, 200 };
		final Img< FloatType > img = ArrayImgs.floats( dimensions );
		final Random random = new Random( 123914924 );
		for ( final FloatType t : img )
			t.set( random.nextFloat() );

		System.out.println( "findLocalMaximaNeighborhood" );
		System.out.println( "(using old LocalNeighborhoodCursor)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood2" );
		System.out.println( "(using LocalNeighborhoodCursor2 by Bene and Tobias)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood2( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood6" );
		System.out.println( "(using RectangleShape)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood6( img );
			}
		} );

		final int n = findLocalMaximaNeighborhood( img );
		System.out.println( n );
		final int n2 = findLocalMaximaNeighborhood2( img );
		System.out.println( n2 );
		final int n6 = findLocalMaximaNeighborhood6( img );
		System.out.println( n6 );
	}
}
