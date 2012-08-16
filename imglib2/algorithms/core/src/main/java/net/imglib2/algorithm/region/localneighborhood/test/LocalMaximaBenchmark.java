package net.imglib2.algorithm.region.localneighborhood.test;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhood;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhood2;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhoodCursor;
import net.imglib2.algorithm.region.localneighborhood.LocalNeighborhoodCursor2;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodCursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class LocalMaximaBenchmark
{
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

	public static < T extends Type< T > & Comparable< T > > int findLocalMaximaNeighborhood3( final RandomAccessibleInterval< T > img )
	{
		// final ArrayList< Point > maxima = new ArrayList< Point >();
		int nMaxima = 0;

		final int n = img.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = -1;
			max[ d ] = 1;
		}
		final FinalInterval span = new FinalInterval( min, max );
		final RectangleNeighborhoodCursor< T > neighborhoods = new RectangleNeighborhoodCursor< T >( Views.interval( img, Intervals.expand( img, -1 ) ), span );
		final RandomAccess< T > center = img.randomAccess();
//		final Cursor< T > center = Views.iterable( Views.interval( img, Intervals.expand( img, -1 ) ) ).localizingCursor();
		final Cursor< T > nc = neighborhoods.get().cursor();
A:		while ( neighborhoods.hasNext() )
		{
			neighborhoods.fwd();
			nc.reset();
//			center.fwd();
			center.setPosition( neighborhoods );
			final T c = center.get();
			while ( nc.hasNext() )
				if ( nc.next().compareTo( c ) > 0 )
					continue A;
			// maxima.add( new Point( center ) );
			++nMaxima;
		}

		return nMaxima;
	}

	public static void main( final String[] args )
	{
		final int numRuns = 10;
		final boolean printIndividualTimes = false;
		final long[] dimensions = new long[] { 2000, 2000 };
		final Img< FloatType > img = ArrayImgs.floats( dimensions );
		final Random random = new Random( 1239149214 );
		for ( final FloatType t : img )
			t.set( random.nextFloat() );

		System.out.println( "findLocalMaximaNeighborhood3" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood3( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood2" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood2( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood( img );
			}
		} );

		final int n = findLocalMaximaNeighborhood( img );
		System.out.println( n );
		final int n2 = findLocalMaximaNeighborhood2( img );
		System.out.println( n2 );
		final int n3 = findLocalMaximaNeighborhood3( img );
		System.out.println( n3 );
	}
}
