package net.imglib2.type.numeric;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

import org.junit.Test;

import net.imglib2.RealLocalizable;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Fraction;
import net.imglib2.view.Views;
import net.imglib2.view.composite.Composite;
import net.imglib2.view.composite.CompositeIntervalView;
import net.imglib2.view.composite.RealComposite;

public class DoubleVectorTypeTest
{

	public static final double EPS = 1e-9;

	@Test
	public void testDoubleVectorType()
	{
		final DoubleVectorType x = new DoubleVectorType( 3 );
		final DoubleVectorType y = new DoubleVectorType( 3 );
		final double[] arr = new double[] { 1, 2, 3 };
		final double[] pos = new double[] { 10, 11, 12 };
		final double[] result = new double[ 3 ];

		// test set and get
		x.set( arr );
		y.set( x );
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( arr[ i ], x.getDouble( i ), EPS );
			assertEquals( arr[ i ], y.getDouble( i ), EPS );
		}

		// test read
		x.read( result );
		assertArrayEquals( arr, result, EPS );

		// zero and one
		x.setZero();
		y.setOne();
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( 0, x.getDouble( i ), EPS );
			assertEquals( 1, y.getDouble( i ), EPS );
		}

		// localizable and positionable methods
		x.setPosition( pos );
		x.read( result );
		assertArrayEquals( pos, result, EPS );

		y.setPosition( x );
		y.read( result );
		assertArrayEquals( pos, result, EPS );
	}

	@Test
	public void testDoubleVectorTypeImage()
	{
		final int vecElements = 5;
		final long numSamples = 32;

		// make an image
		final DoubleArray dataAccess = new DoubleArray( DoubleStream.iterate( 0.0, x -> x + 1 ).limit( vecElements * numSamples ).toArray() );
		final ArrayImg< DoubleVectorType, DoubleArray > img = new ArrayImg<>( dataAccess, new long[] { numSamples }, new Fraction( vecElements, 1 ) );
		final DoubleVectorType t = new DoubleVectorType( img, vecElements );
		img.setLinkedType( t );

		double trueval = 0;
		final ArrayCursor< DoubleVectorType > c = img.cursor();
		while ( c.hasNext() )
		{
			final DoubleVectorType v = c.next();
			for ( int i = 0; i < vecElements; i++ )
				assertEquals( trueval++, v.getDouble( i ), EPS );
		}
	}

	private static final void benchmarkRealLocalizable1(final long[] size, final double[] test, final Iterable< ? extends RealLocalizable > img, final String comment) {

		final long time = System.nanoTime();

		int i = 0;
		for (final RealLocalizable t : img ) {

			for (int j = 0; j < size[0]; ++j) {
				assertEquals( test[i], t.getDoublePosition( j ), 0 );
				++i;
			}
		}

		System.out.println( comment + ( System.nanoTime() - time ) );
	}

	private static final void benchmarkRealLocalizable2(final long[] size, final double[] test, final Iterable< ? extends RealLocalizable > img, final String comment) {

		final long time = System.nanoTime();

		int i = 0;
		for (final RealLocalizable t : img ) {

			for (int j = 0; j < size[0]; ++j) {
				assertEquals( test[i], t.getDoublePosition( j ), 0 );
				++i;
			}
		}

		System.out.println( comment + ( System.nanoTime() - time ) );
	}

	private static final void benchmarkRealLocalizable3(final long[] size, final double[] test, final Iterable< ? extends RealLocalizable > img, final String comment) {

		final long time = System.nanoTime();

		int i = 0;
		for (final RealLocalizable t : img ) {

			for (int j = 0; j < size[0]; ++j) {
				assertEquals( test[i], t.getDoublePosition( j ), 0 );
				++i;
			}
		}

		System.out.println( comment + ( System.nanoTime() - time ) );
	}

	private static final void benchmarkComposite1(final long[] size, final double[] test, final Iterable< ? extends Composite< DoubleType > > img, final String comment) {

		final long time = System.nanoTime();

		int i = 0;
		for (final Composite< DoubleType > t : img ) {

			for (int j = 0; j < size[0]; ++j) {
				assertEquals( test[i], t.get( j ).get(), 0 );
				++i;
			}
		}

		System.out.println( comment + ( System.nanoTime() - time ) );
	}

	private static final void benchmarkComposite2(final long[] size, final double[] test, final Iterable< ? extends Composite< DoubleType > > img, final String comment) {

		final long time = System.nanoTime();

		int i = 0;
		for (final Composite< DoubleType > t : img ) {

			for (int j = 0; j < size[0]; ++j) {
				assertEquals( test[i], t.get( j ).get(), 0 );
				++i;
			}
		}

		System.out.println( comment + ( System.nanoTime() - time ) );
	}

	private static final void benchmarkComposite3(final long[] size, final double[] test, final Iterable< ? extends Composite< DoubleType > > img, final String comment) {

		final long time = System.nanoTime();

		int i = 0;
		for (final Composite< DoubleType > t : img ) {

			for (int j = 0; j < size[0]; ++j) {
				assertEquals( test[i], t.get( j ).get(), 0 );
				++i;
			}
		}

		System.out.println( comment + ( System.nanoTime() - time ) );
	}

	@Test
	public void benchmarkDoubleVectorType() {

		final long[] size = {3, 500, 200, 300};
		final Random rnd = new Random(0);
		final double[] test = new double[(int)(size[0] * size[1] * size[2] * size[3])];
		for (int i = 0; i < test.length; ++i) {

			test[ i ] = rnd.nextDouble();
		}

		final CompositeIntervalView< DoubleType, RealComposite< DoubleType > > img1 = Views.collapseReal(
				Views.moveAxis(
						ArrayImgs.doubles(test, size),
						0,
						3));
		final ArrayImg<DoubleVectorType, DoubleArray> img2 = new ArrayImg<>( new DoubleArray( test ), Arrays.copyOfRange( size, 1, size.length ), new Fraction(size[0], 1));
		img2.setLinkedType( new DoubleVectorType( img2, (int )size[0] ) );


		benchmarkRealLocalizable1( size, test, Views.iterable( img1 ), "RealComposite polymorphic RealLocalizable:       " );
		benchmarkRealLocalizable1( size, test, img2,                   "DoubleVector polymorphic RealLocalizable:        " );
		benchmarkRealLocalizable1( size, test, Views.iterable( img1 ), "RealComposite polymorphic RealLocalizable:       " );
		benchmarkRealLocalizable1( size, test, img2,                   "DoubleVector polymorphic RealLocalizable:        " );

		benchmarkRealLocalizable2( size, test, Views.iterable( img1 ), "RealComposite monomorphic RealLocalizable:       " );
		benchmarkRealLocalizable3( size, test, img2,                   "DoubleVector monomorphic RealLocalizable:        " );
		benchmarkRealLocalizable2( size, test, Views.iterable( img1 ), "RealComposite monomorphic RealLocalizable:       " );
		benchmarkRealLocalizable3( size, test, img2,                   "DoubleVector monomorphic RealLocalizable:        " );

		benchmarkComposite1( size, test, Views.iterable( img1 ),       "RealComposite polymorphic Composite<DoubleType>: " );
		benchmarkComposite1( size, test, img2,                         "DoubleVector polymorphic Composite<DoubleType>:  " );
		benchmarkComposite1( size, test, Views.iterable( img1 ),       "RealComposite polymorphic Composite<DoubleType>: " );
		benchmarkComposite1( size, test, img2,                         "DoubleVector polymorphic Composite<DoubleType>:  " );

		benchmarkComposite2( size, test, Views.iterable( img1 ),       "RealComposite monomorphic Composite<DoubleType>: " );
		benchmarkComposite3( size, test, img2,                         "DoubleVector monomorphic Composite<DoubleType>:  " );
		benchmarkComposite2( size, test, Views.iterable( img1 ),       "RealComposite monomorphic Composite<DoubleType>: " );
		benchmarkComposite3( size, test, img2,                         "DoubleVector monomorphic Composite<DoubleType>:  " );

	}

}

