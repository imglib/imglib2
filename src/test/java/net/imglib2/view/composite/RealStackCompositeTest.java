package net.imglib2.view.composite;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.position.FunctionRealRandomAccessible;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.ConstantUtils;

public class RealStackCompositeTest
{
	private static final double EPS = 1e-9;

	private int N = 5;
	private RealRandomAccessible<DoubleType>[] sources;
	private RealRandomAccessible<DoubleType>[] sources2;

	@SuppressWarnings( "unchecked" )
	@Before
	public void before() throws Exception
	{
		// 1d
		sources = new RealRandomAccessible[ N ];
		for( int i = 0; i < N; i++ )
		{
			final int j = i;
			sources[i] = new FunctionRealRandomAccessible<>( 1,
					( x, v ) -> { v.set( j * x.getDoublePosition( 0 ) ); },
					DoubleType::new );
		}

		// 2d
		sources2 = new RealRandomAccessible[ N ];
		for( int i = 0; i < N; i++ )
		{
			final int j = i;
			sources2[i] = new FunctionRealRandomAccessible<>( 2,
					( x, v ) -> { v.set( j * x.getDoublePosition( 0 ) + x.getDoublePosition( 1 )); },
					DoubleType::new );
		}
	}

	@Test
	public final void test1d()
	{
		RealStackCompositeView<DoubleType> rimg = new RealStackCompositeView<>( sources );
		assertEquals( "ndims", 1, rimg.numDimensions() );

		RealRandomAccess<Composite<DoubleType>> access = rimg.realRandomAccess();
		for( int i = 0; i < N; i++ )
			assertEquals( 0, access.get().get( i ).get(), EPS );

		// test setPosition
		double p = 10;
		access.setPosition( p, 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * p, access.get().get( i ).get(), EPS );

		// test fwd
		p++;
		access.fwd( 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * p, access.get().get( i ).get(), EPS );

		// test move
		p -= 2;
		access.move( -2, 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * p, access.get().get( i ).get(), EPS );

	}

	@Test
	public final void test2d()
	{
		RealStackCompositeView<DoubleType> rimg = new RealStackCompositeView<>( sources2 );
		assertEquals( "ndims", 2, rimg.numDimensions() );

		RealRandomAccess<Composite<DoubleType>> access = rimg.realRandomAccess();
		for( int i = 0; i < N; i++ )
			assertEquals( 0, access.get().get( i ).get(), EPS );

		double x = 10;
		double y = 2;
		double[] pa = new double[]{x,y};

		// test setPosition
		access.setPosition( x, 0 );
		access.setPosition( y, 1 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * x + y, access.get().get( i ).get(), EPS );

		// test setPosition
		x = 11; pa[0] = x;
		y =  3; pa[1] = y;
		access.setPosition( pa );
		for( int i = 0; i < N; i++ )
			assertEquals( i * x + y, access.get().get( i ).get(), EPS );

		// test fwd
		x++;
		access.fwd( 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * x + y, access.get().get( i ).get(), EPS );

		y++;
		access.fwd( 1 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * x + y, access.get().get( i ).get(), EPS );

		// test move
		x -= 2.5;
		access.move( -2.5, 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * x + y, access.get().get( i ).get(), EPS );

		// test move
		y -= 0.7;
		access.move( -0.7, 1 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * x + y, access.get().get( i ).get(), EPS );

	}
}
