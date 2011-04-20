package net.imglib2.view;

import static org.junit.Assert.assertArrayEquals;


import net.imglib2.transform.integer.SequentializeTransform;

import org.junit.Test;

public class SequentializeTransformTest
{
	@Test
	public void test2Dto1D()
	{
		long[] dim = new long[] {10, 20};
		SequentializeTransform t = new SequentializeTransform( dim, 1 );
		
		long[] source = new long[ 2 ];
		long[] target = new long[ 1 ];
		long[] expectedTarget = new long[ 1 ];

		source[ 0 ] = 0;
		source[ 1 ] = 0;
		expectedTarget[ 0 ] = 0;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );

		source[ 0 ] = 5;
		source[ 1 ] = 0;
		expectedTarget[ 0 ] = 5;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );
	
		source[ 0 ] = 5;
		source[ 1 ] = 1;
		expectedTarget[ 0 ] = 15;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );
	}

	@Test
	public void test3Dto1D()
	{
		long[] dim = new long[] {10, 20, 30};
		SequentializeTransform t = new SequentializeTransform( dim, 1 );
		
		int[] source = new int[ 3 ];
		int[] target = new int[ 1 ];
		int[] expectedTarget = new int[ 1 ];

		source[ 0 ] = 0;
		source[ 1 ] = 0;
		source[ 2 ] = 0;
		expectedTarget[ 0 ] = 0;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );

		source[ 0 ] = 5;
		source[ 1 ] = 0;
		source[ 2 ] = 0;
		expectedTarget[ 0 ] = 5;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );
	
		source[ 0 ] = 5;
		source[ 1 ] = 1;
		source[ 2 ] = 0;
		expectedTarget[ 0 ] = 15;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );

		source[ 0 ] = 5;
		source[ 1 ] = 4;
		source[ 2 ] = 3;
		expectedTarget[ 0 ] = 5 + 4*10 + 3*20*10;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );
	}

	@Test
	public void test4Dto3D()
	{
		long[] dim = new long[] {10, 20, 30, 40};
		SequentializeTransform t = new SequentializeTransform( dim, 3 );
		
		long[] source = new long[ 4 ];
		long[] target = new long[ 3 ];
		long[] expectedTarget = new long[ 3 ];

		source[ 0 ] = 0;
		source[ 1 ] = 0;
		source[ 2 ] = 0;
		source[ 3 ] = 0;
		expectedTarget[ 0 ] = source[ 0 ];
		expectedTarget[ 1 ] = source[ 1 ];
		expectedTarget[ 2 ] = 0;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );

		source[ 0 ] = 0;
		source[ 1 ] = 0;
		source[ 2 ] = 0;
		source[ 3 ] = 1;
		expectedTarget[ 0 ] = source[ 0 ];
		expectedTarget[ 1 ] = source[ 1 ];
		expectedTarget[ 2 ] = 30;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );

		source[ 0 ] = 9;
		source[ 1 ] = 4;
		source[ 2 ] = 2;
		source[ 3 ] = 3;
		expectedTarget[ 0 ] = source[ 0 ];
		expectedTarget[ 1 ] = source[ 1 ];
		expectedTarget[ 2 ] = 2 + 3*30;
		t.apply( source, target );
		assertArrayEquals( expectedTarget, target );	
	}
	
	@Test
	public void test4Dto3DInverse()
	{
		long[] dim = new long[] {10, 20, 30, 40};
		SequentializeTransform t = new SequentializeTransform( dim, 3 );
		
		long[] source = new long[ 4 ];
		long[] target = new long[ 3 ];
		long[] expectedSource = new long[ 4 ];

		source[ 0 ] = 0;
		source[ 1 ] = 0;
		source[ 2 ] = 0;
		source[ 3 ] = 0;
		t.apply( source, target );
		for ( int d = 0; d < 4; ++d )
		{
			expectedSource[ d ] = source[ d ];
			source[ d ] = -1;
		}
		t.applyInverse( source, target );
		assertArrayEquals( expectedSource, source );

		source[ 0 ] = 0;
		source[ 1 ] = 0;
		source[ 2 ] = 0;
		source[ 3 ] = 1;
		t.apply( source, target );
		for ( int d = 0; d < 4; ++d )
		{
			expectedSource[ d ] = source[ d ];
			source[ d ] = -1;
		}
		t.applyInverse( source, target );
		assertArrayEquals( expectedSource, source );

		source[ 0 ] = 9;
		source[ 1 ] = 4;
		source[ 2 ] = 2;
		source[ 3 ] = 3;
		t.apply( source, target );
		for ( int d = 0; d < 4; ++d )
		{
			expectedSource[ d ] = source[ d ];
			source[ d ] = -1;
		}
		t.applyInverse( source, target );
		assertArrayEquals( expectedSource, source );
	}
	
}
