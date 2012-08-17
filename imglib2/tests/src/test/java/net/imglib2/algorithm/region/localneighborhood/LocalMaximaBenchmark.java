package net.imglib2.algorithm.region.localneighborhood;

import java.util.Random;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodCursor;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodFactory;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodRandomAccess;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodSkipCenter;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodSkipCenterUnsafe;
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
import net.imglib2.view.RandomAccessibleIntervalCursor;
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
		final RectangleNeighborhoodCursor< T > neighborhoods = new RectangleNeighborhoodCursor< T >( Views.interval( img, Intervals.expand( img, -1 ) ), span, RectangleNeighborhoodSkipCenter.< T >factory() );
//		final RandomAccess< T > center = img.randomAccess();
		final Cursor< T > center = Views.iterable( Views.interval( img, Intervals.expand( img, -1 ) ) ).cursor();
		final Cursor< T > nc = neighborhoods.get().cursor();
A:		while ( neighborhoods.hasNext() )
		{
			neighborhoods.fwd();
			nc.reset();
			center.fwd();
//			center.setPosition( neighborhoods );
			final T c = center.get();
			while ( nc.hasNext() )
				if ( nc.next().compareTo( c ) > 0 )
					continue A;
			// maxima.add( new Point( center ) );
			++nMaxima;
		}

		return nMaxima;
	}

	public static final class TheAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;
		final Interval span;
		final RectangleNeighborhoodFactory< T > factory;

		public TheAccessible( final RandomAccessibleInterval< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new RectangleNeighborhoodRandomAccess< T >( source, span, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}
	}

	public static < T extends Type< T > & Comparable< T > > int findLocalMaximaNeighborhood4( final RandomAccessibleInterval< T > img )
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

		final RandomAccessibleInterval< Neighborhood< T > > theAccessible = new TheAccessible< T >( Views.interval( img, Intervals.expand( img, -1 ) ), span, RectangleNeighborhoodSkipCenter.< T >factory() );
		final Cursor< Neighborhood< T > > neighborhoods = new RandomAccessibleIntervalCursor< Neighborhood<T> >( theAccessible );
//		final Cursor< RectangleSkipCenterNeighborhood< T > > neighborhoods = Views.iterable( theAccessible ).cursor();
//		final RandomAccess< T > center = img.randomAccess();
		final Cursor< T > center = Views.iterable( Views.interval( img, Intervals.expand( img, -1 ) ) ).cursor();
		final Cursor< T > nc = neighborhoods.get().cursor();
A:		while ( neighborhoods.hasNext() )
		{
			neighborhoods.fwd();
			nc.reset();
			center.fwd();
//			center.setPosition( neighborhoods );
			final T c = center.get();
			while ( nc.hasNext() )
				if ( nc.next().compareTo( c ) > 0 )
					continue A;
			// maxima.add( new Point( center ) );
			++nMaxima;
		}

		return nMaxima;
	}

	public static < T extends Type< T > & Comparable< T > > int findLocalMaximaNeighborhood5( final RandomAccessibleInterval< T > img )
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

		final RandomAccessibleInterval< Neighborhood< T > > theAccessible = new TheAccessible< T >( Views.interval( img, Intervals.expand( img, -1 ) ), span, RectangleNeighborhoodSkipCenterUnsafe.< T >factory() );
		final Cursor< T > center = Views.iterable( Views.interval( img, Intervals.expand( img, -1 ) ) ).cursor();
A:		for ( final Neighborhood< T > neighborhood : Views.iterable( theAccessible ) )
		{
			center.fwd();
			final T c = center.get();
			for ( final T t : neighborhood )
				if ( t.compareTo( c ) > 0 )
					continue A;
			// maxima.add( new Point( center ) );
			++nMaxima;
		}

		return nMaxima;
	}

	public static < T extends Type< T > & Comparable< T > > int countLocalMaxima( final RandomAccessibleInterval< T > img, final Shape neighborhoods )
	{
		int nMaxima = 0;
		final RandomAccessibleInterval< T > source = Views.interval( img, Intervals.expand( img, -1 ) );
		final Cursor< T > center = Views.iterable( source ).cursor();
		final IterableInterval< Neighborhood< T > > allneighborhoods = Views.iterable( neighborhoods.neighborhoodsRandomAccessible( source ) );
A:		for ( final Neighborhood< T > neighborhood : allneighborhoods )
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
		final long[] dimensions = new long[] { 500, 500, 10 };
		final Img< FloatType > img = ArrayImgs.floats( dimensions );
		final Random random = new Random( 123914924 );
		for ( final FloatType t : img )
			t.set( random.nextFloat() );

		System.out.println( "findLocalMaximaNeighborhood6" );
		System.out.println( "(using RectangleNeighborhoods)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood6( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood5" );
		System.out.println( "(using RectangleNeighborhoodRandomAccess with RectangleSkipCenterNeighborhoodSingle)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood5( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood3" );
		System.out.println( "(using RectangleNeighborhoodCursor with RectangleSkipCenterNeighborhood)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood3( img );
			}
		} );

		System.out.println( "findLocalMaximaNeighborhood4" );
		System.out.println( "(using RectangleNeighborhoodRandomAccess with RectangleSkipCenterNeighborhood)" );
		BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				findLocalMaximaNeighborhood4( img );
			}
		} );

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

		final int n = findLocalMaximaNeighborhood( img );
		System.out.println( n );
		final int n2 = findLocalMaximaNeighborhood2( img );
		System.out.println( n2 );
		final int n3 = findLocalMaximaNeighborhood3( img );
		System.out.println( n3 );
		final int n4 = findLocalMaximaNeighborhood4( img );
		System.out.println( n4 );
		final int n5 = findLocalMaximaNeighborhood5( img );
		System.out.println( n5 );
		final int n6 = findLocalMaximaNeighborhood6( img );
		System.out.println( n6 );
	}
}
