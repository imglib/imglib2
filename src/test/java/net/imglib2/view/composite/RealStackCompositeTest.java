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

	int N = 5;
	RealRandomAccessible<DoubleType>[] sources;

	@SuppressWarnings( "unchecked" )
	@Before
	public void before() throws Exception
	{
		sources = new RealRandomAccessible[ N ];
		for( int i = 0; i < N; i++ )
		{
			final int j = i;
			sources[i] = new FunctionRealRandomAccessible<>( 1,
					( x, v ) -> { v.set( j * x.getDoublePosition( 0 ) ); },
					DoubleType::new );
		}
	}

	@Test
	public final void test()
	{
		RealStackCompositeView<DoubleType> rimg = new RealStackCompositeView<>( sources );
		assertEquals( "ndims", 1, rimg.numDimensions() );

		RealRandomAccess<Composite<DoubleType>> access = rimg.realRandomAccess();
		for( int i = 0; i < N; i++ )
			assertEquals( 0, access.get().get( i ).get(), EPS );

		double p = 10;
		access.setPosition( p, 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * p, access.get().get( i ).get(), EPS );

		p++;
		access.fwd( 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * p, access.get().get( i ).get(), EPS );

		p -= 2;
		access.move( -2, 0 );
		for( int i = 0; i < N; i++ )
			assertEquals( i * p, access.get().get( i ).get(), EPS );

	}
}
