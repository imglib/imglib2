package net.imglib2.type.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.stream.DoubleStream;

import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.DoubleVectorType;
import net.imglib2.util.Fraction;

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
		ArrayCursor< DoubleVectorType > c = img.cursor();
		while ( c.hasNext() )
		{
			DoubleVectorType v = c.next();
			for ( int i = 0; i < vecElements; i++ )
				assertEquals( trueval++, v.getDouble( i ), EPS );
		}
	}

}
