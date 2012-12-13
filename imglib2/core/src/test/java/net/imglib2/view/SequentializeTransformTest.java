/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.view;

import static org.junit.Assert.assertArrayEquals;
import net.imglib2.transform.integer.SequentializeTransform;

import org.junit.Test;

/**
 * TODO
 *
 */
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
