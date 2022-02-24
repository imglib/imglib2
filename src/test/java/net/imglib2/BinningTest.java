/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */

package net.imglib2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.util.Binning;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Aivar Grislis
 */
public class BinningTest
{
	private static final double EPSILON_FACTOR = 1000000;

	private static final boolean TEST_INTERNAL = false;

	public BinningTest()
	{}

	@BeforeClass
	public static void setUpClass() throws Exception
	{}

	@AfterClass
	public static void tearDownClass() throws Exception
	{}

	@Before
	public void setUp()
	{}

	@After
	public void tearDown()
	{}

	@Test
	public void testInternal()
	{
		// avoid lengthy test in build; tests internal method
		if ( TEST_INTERNAL )
		{
			System.out.println( "testInternal" );
			double value;
			int number;

			// test going from negative to zero to positive
			value = -0x0.0000000000010P-1022;
			number = 1024;
			testNextDouble( value, number );

			// test going from unnormalizable to normalizable
			value = 0x0.ffffffffffff0P-1022;
			number = 1024;
			testNextDouble( value, number );
		}
	}

	/**
	 * Test looking up some simple values.
	 */
	@Test
	public void testValues()
	{
		System.out.println( "testValues" );

		testSomeValues( 256 );
		testSomeValues( 1000 );
		testSomeValues( 32767 );
		testSomeValues( 65534 );
	}

	/**
	 * Test edge cases, near min and max.
	 */
	@Test
	public void testEdges()
	{
		System.out.println( "testEdges" );

		double d = 1.0234; // testing arbitrary values
		testSomeEdges( 256, d );
		testSomeEdges( 1000, d );
		testSomeEdges( 32767, d );
		testSomeEdges( 65534, d );

		d = 9.87654321;
		testSomeEdges( 256, d );
		testSomeEdges( 1000, d );
		testSomeEdges( 32767, d );
		testSomeEdges( 65534, d );

		d = -1.0234;
		testSomeEdges( 256, d );
		testSomeEdges( 1000, d );
		testSomeEdges( 32767, d );
		testSomeEdges( 65534, d );

		d = -9.87654321;
		testSomeEdges( 256, d );
		testSomeEdges( 1000, d );
		testSomeEdges( 32767, d );
		testSomeEdges( 65534, d );
	}

	/**
	 * Tests bins are evenly distributed.
	 */
	@Test
	public void testDistribution()
	{
		System.out.println( "testDistribution" );

		int bins = 256;
		double min = 0.0;
		double max = 1.0;
		double inc = 0.0001;
		testHistogram( min, max, inc, bins );

		bins = 1024;
		min = 0.0;
		max = 1.0;
		inc = 0.00001;
		testHistogram( min, max, inc, bins );
	}

	/**
	 * Tests edge values array.
	 */
	@Test
	public void testEdgeValuesArray()
	{
		System.out.println( "testEdgeValuesArray" );
		int bins;
		double min;
		double max;
		double[] edgeValues;
		double[] expectedValues;

		bins = 256;
		min = 0.0;
		max = 1.0;
		edgeValues = Binning.edgeValuesPerBin( bins, min, max );
		expectedValues = new double[] {
				0.0, 0.00390625, 0.0078125, 0.01171875,
				0.015625, 0.01953125, 0.0234375, 0.02734375,
				0.03125, 0.03515625, 0.0390625, 0.04296875,
				0.046875, 0.05078125, 0.0546875, 0.05859375,
				0.0625, 0.06640625, 0.0703125, 0.07421875,
				0.078125, 0.08203125, 0.0859375, 0.08984375,
				0.09375, 0.09765625, 0.1015625, 0.10546875,
				0.109375, 0.11328125, 0.1171875, 0.12109375,
				0.125, 0.12890625, 0.1328125, 0.13671875,
				0.140625, 0.14453125, 0.1484375, 0.15234375,
				0.15625, 0.16015625, 0.1640625, 0.16796875,
				0.171875, 0.17578125, 0.1796875, 0.18359375,
				0.1875, 0.19140625, 0.1953125, 0.19921875,
				0.203125, 0.20703125, 0.2109375, 0.21484375,
				0.21875, 0.22265625, 0.2265625, 0.23046875,
				0.234375, 0.23828125, 0.2421875, 0.24609375,
				0.25, 0.25390625, 0.2578125, 0.26171875,
				0.265625, 0.26953125, 0.2734375, 0.27734375,
				0.28125, 0.28515625, 0.2890625, 0.29296875,
				0.296875, 0.30078125, 0.3046875, 0.30859375,
				0.3125, 0.31640625, 0.3203125, 0.32421875,
				0.328125, 0.33203125, 0.3359375, 0.33984375,
				0.34375, 0.34765625, 0.3515625, 0.35546875,
				0.359375, 0.36328125, 0.3671875, 0.37109375,
				0.375, 0.37890625, 0.3828125, 0.38671875,
				0.390625, 0.39453125, 0.3984375, 0.40234375,
				0.40625, 0.41015625, 0.4140625, 0.41796875,
				0.421875, 0.42578125, 0.4296875, 0.43359375,
				0.4375, 0.44140625, 0.4453125, 0.44921875,
				0.453125, 0.45703125, 0.4609375, 0.46484375,
				0.46875, 0.47265625, 0.4765625, 0.48046875,
				0.484375, 0.48828125, 0.4921875, 0.49609375,
				0.5, 0.50390625, 0.5078125, 0.51171875,
				0.515625, 0.51953125, 0.5234375, 0.52734375,
				0.53125, 0.53515625, 0.5390625, 0.54296875,
				0.546875, 0.55078125, 0.5546875, 0.55859375,
				0.5625, 0.56640625, 0.5703125, 0.57421875,
				0.578125, 0.58203125, 0.5859375, 0.58984375,
				0.59375, 0.59765625, 0.6015625, 0.60546875,
				0.609375, 0.61328125, 0.6171875, 0.62109375,
				0.625, 0.62890625, 0.6328125, 0.63671875,
				0.640625, 0.64453125, 0.6484375, 0.65234375,
				0.65625, 0.66015625, 0.6640625, 0.66796875,
				0.671875, 0.67578125, 0.6796875, 0.68359375,
				0.6875, 0.69140625, 0.6953125, 0.69921875,
				0.703125, 0.70703125, 0.7109375, 0.71484375,
				0.71875, 0.72265625, 0.7265625, 0.73046875,
				0.734375, 0.73828125, 0.7421875, 0.74609375,
				0.75, 0.75390625, 0.7578125, 0.76171875,
				0.765625, 0.76953125, 0.7734375, 0.77734375,
				0.78125, 0.78515625, 0.7890625, 0.79296875,
				0.796875, 0.80078125, 0.8046875, 0.80859375,
				0.8125, 0.81640625, 0.8203125, 0.82421875,
				0.828125, 0.83203125, 0.8359375, 0.83984375,
				0.84375, 0.84765625, 0.8515625, 0.85546875,
				0.859375, 0.86328125, 0.8671875, 0.87109375,
				0.875, 0.87890625, 0.8828125, 0.88671875,
				0.890625, 0.89453125, 0.8984375, 0.90234375,
				0.90625, 0.91015625, 0.9140625, 0.91796875,
				0.921875, 0.92578125, 0.9296875, 0.93359375,
				0.9375, 0.94140625, 0.9453125, 0.94921875,
				0.953125, 0.95703125, 0.9609375, 0.96484375,
				0.96875, 0.97265625, 0.9765625, 0.98046875,
				0.984375, 0.98828125, 0.9921875, 0.99609375 };
		assertEquals( edgeValues.length, expectedValues.length );
		assertEquals( edgeValues.length, bins );
		for ( int i = 0; i < bins; ++i )
		{
			assertEquals( edgeValues[ i ], expectedValues[ i ], edgeValues[ i ] / EPSILON_FACTOR );
		}

		// make sure edge value is the first double that maps to that bin
		// (this won't pass if bins/min/max are too oddball)
		bins = 256;
		min = 0.0;
		max = 1.0;
		edgeValues = Binning.edgeValuesPerBin( bins, min, max );
		for ( int i = 0; i < bins; ++i )
		{
			final int edgeBin = Binning.exclusiveValueToBin( bins, min, max, edgeValues[ i ] );
			assertEquals( i, edgeBin );

			// previous double should map to previous bin
			final double prevValue = DoubleUtil.nextDouble( edgeValues[ i ], false );
			final int prevBin = Binning.exclusiveValueToBin( bins, min, max, prevValue );
			assertEquals( i - 1, prevBin );
		}
	}

	/**
	 * Tests center values array.
	 */
	@Test
	public void testCenterValuesArray()
	{
		System.out.println( "testCenterValuesArray" );
		int bins;
		double min;
		double max;
		double[] expectedValues;

		bins = 256;
		min = 0.0;
		max = 1.0;
		final double[] centerValues = Binning.centerValuesPerBin( bins, min, max );
		expectedValues = new double[] {
				0.001953125, 0.005859375, 0.009765625, 0.013671875,
				0.017578125, 0.021484375, 0.025390625, 0.029296875,
				0.033203125, 0.037109375, 0.041015625, 0.044921875,
				0.048828125, 0.052734375, 0.056640625, 0.060546875,
				0.064453125, 0.068359375, 0.072265625, 0.076171875,
				0.080078125, 0.083984375, 0.087890625, 0.091796875,
				0.095703125, 0.099609375, 0.103515625, 0.107421875,
				0.111328125, 0.115234375, 0.119140625, 0.123046875,
				0.126953125, 0.130859375, 0.134765625, 0.138671875,
				0.142578125, 0.146484375, 0.150390625, 0.154296875,
				0.158203125, 0.162109375, 0.166015625, 0.169921875,
				0.173828125, 0.177734375, 0.181640625, 0.185546875,
				0.189453125, 0.193359375, 0.197265625, 0.201171875,
				0.205078125, 0.208984375, 0.212890625, 0.216796875,
				0.220703125, 0.224609375, 0.228515625, 0.232421875,
				0.236328125, 0.240234375, 0.244140625, 0.248046875,
				0.251953125, 0.255859375, 0.259765625, 0.263671875,
				0.267578125, 0.271484375, 0.275390625, 0.279296875,
				0.283203125, 0.287109375, 0.291015625, 0.294921875,
				0.298828125, 0.302734375, 0.306640625, 0.310546875,
				0.314453125, 0.318359375, 0.322265625, 0.326171875,
				0.330078125, 0.333984375, 0.337890625, 0.341796875,
				0.345703125, 0.349609375, 0.353515625, 0.357421875,
				0.361328125, 0.365234375, 0.369140625, 0.373046875,
				0.376953125, 0.380859375, 0.384765625, 0.388671875,
				0.392578125, 0.396484375, 0.400390625, 0.404296875,
				0.408203125, 0.412109375, 0.416015625, 0.419921875,
				0.423828125, 0.427734375, 0.431640625, 0.435546875,
				0.439453125, 0.443359375, 0.447265625, 0.451171875,
				0.455078125, 0.458984375, 0.462890625, 0.466796875,
				0.470703125, 0.474609375, 0.478515625, 0.482421875,
				0.486328125, 0.490234375, 0.494140625, 0.498046875,
				0.501953125, 0.505859375, 0.509765625, 0.513671875,
				0.517578125, 0.521484375, 0.525390625, 0.529296875,
				0.533203125, 0.537109375, 0.541015625, 0.544921875,
				0.548828125, 0.552734375, 0.556640625, 0.560546875,
				0.564453125, 0.568359375, 0.572265625, 0.576171875,
				0.580078125, 0.583984375, 0.587890625, 0.591796875,
				0.595703125, 0.599609375, 0.603515625, 0.607421875,
				0.611328125, 0.615234375, 0.619140625, 0.623046875,
				0.626953125, 0.630859375, 0.634765625, 0.638671875,
				0.642578125, 0.646484375, 0.650390625, 0.654296875,
				0.658203125, 0.662109375, 0.666015625, 0.669921875,
				0.673828125, 0.677734375, 0.681640625, 0.685546875,
				0.689453125, 0.693359375, 0.697265625, 0.701171875,
				0.705078125, 0.708984375, 0.712890625, 0.716796875,
				0.720703125, 0.724609375, 0.728515625, 0.732421875,
				0.736328125, 0.740234375, 0.744140625, 0.748046875,
				0.751953125, 0.755859375, 0.759765625, 0.763671875,
				0.767578125, 0.771484375, 0.775390625, 0.779296875,
				0.783203125, 0.787109375, 0.791015625, 0.794921875,
				0.798828125, 0.802734375, 0.806640625, 0.810546875,
				0.814453125, 0.818359375, 0.822265625, 0.826171875,
				0.830078125, 0.833984375, 0.837890625, 0.841796875,
				0.845703125, 0.849609375, 0.853515625, 0.857421875,
				0.861328125, 0.865234375, 0.869140625, 0.873046875,
				0.876953125, 0.880859375, 0.884765625, 0.888671875,
				0.892578125, 0.896484375, 0.900390625, 0.904296875,
				0.908203125, 0.912109375, 0.916015625, 0.919921875,
				0.923828125, 0.927734375, 0.931640625, 0.935546875,
				0.939453125, 0.943359375, 0.947265625, 0.951171875,
				0.955078125, 0.958984375, 0.962890625, 0.966796875,
				0.970703125, 0.974609375, 0.978515625, 0.982421875,
				0.986328125, 0.990234375, 0.994140625, 0.998046875
		};
		assertEquals( centerValues.length, expectedValues.length );
		assertEquals( centerValues.length, bins );
		for ( int i = 0; i < bins; ++i )
		{
			assertEquals( centerValues[ i ], expectedValues[ i ], centerValues[ i ] / EPSILON_FACTOR );
		}

		// make sure center value maps to correct bin
		for ( int i = 0; i < bins; ++i )
		{
			final int centerBin = Binning.exclusiveValueToBin( bins, min, max, centerValues[ i ] );
			assertEquals( i, centerBin );
		}
	}

	/**
	 * Helper function checks values map reasonably.
	 * 
	 * @param bins
	 */
	private void testSomeValues( final int bins )
	{
		double min;
		double max;
		double value;
		int bin;

		// check that value min maps to first bin
		min = 0.0;
		max = 1.0;
		value = min;
		bin = Binning.valueToBin( bins, min, max, value );
		assertEquals( 0, bin );
		bin = Binning.exclusiveValueToBin( bins, min, max, value );
		assertEquals( 0, bin );

		// check that value max maps to last bin
		min = 0.0;
		max = 1.0;
		value = max;
		bin = Binning.valueToBin( bins, min, max, value );
		assertEquals( bins - 1, bin );
		bin = Binning.exclusiveValueToBin( bins, min, max, value );
		assertEquals( bins - 1, bin );

		// check that value (max - min) / 2 maps to middle bin
		min = 0.0;
		max = 1.0;
		value = ( max - min ) / 2;
		bin = Binning.valueToBin( bins, min, max, value );
		assertEquals( bins / 2, bin );
		bin = Binning.exclusiveValueToBin( bins, min, max, value );
		assertEquals( bins / 2, bin );

		// check that if max == min == value get middle bin
		min = 0.0;
		max = min;
		value = min;
		bin = Binning.valueToBin( bins, min, max, value );
		assertEquals( bins / 2, bin );
		bin = Binning.exclusiveValueToBin( bins, min, max, value );
		assertEquals( bins / 2, bin );
	}

	/**
	 * Tests that values less than min or more than max map outside the range of
	 * bins.
	 * 
	 * @param bins
	 * @param d1
	 */
	private void testSomeEdges( final int bins, double d1 )
	{
		double min;
		double max;
		double value;
		int bin;

		// get two adjacent doubles
		double d2 = DoubleUtil.nextDouble( d1, true );
		if ( d2 < d1 )
		{
			// allow for negative numbers
			final double tmp = d1;
			d1 = d2;
			d2 = tmp;
		}

		// value is just before min
		value = d1;
		min = d2;
		max = d2 + 1.0;
		bin = Binning.valueToBin( bins, min, max, value );
		// inclusive, get first bin
		assertEquals( 0, bin );
		bin = Binning.exclusiveValueToBin( bins, min, max, value );
		// exclusive, get bin - 1
		assertEquals( -1, bin );

		// value is just after max
		max = d1;
		min = d1 - 1.0;
		value = d2;
		bin = Binning.valueToBin( bins, min, max, value );
		// inclusive, get last bin
		assertEquals( bins - 1, bin );
		bin = Binning.exclusiveValueToBin( bins, min, max, value );
		// exclusive, get last bin + 1
		assertEquals( bins, bin );
	}

	/**
	 * Tests the uniformity of bin distribution in a histogram situation.
	 * 
	 * @param min
	 * @param max
	 * @param inc
	 * @param bins
	 */
	private void testHistogram( final double min, final double max, final double inc, final int bins )
	{
		final long[] histogram = new long[ bins ];

		// fill up the histogram with uniform values
		for ( double value = min; value <= max; value += inc )
		{
			final int bin = Binning.valueToBin( bins, min, max, value );
			++histogram[ bin ];
		}

		// make a list of histogram count values
		final List< Long > values = new ArrayList< Long >();
		Arrays.sort( histogram );
		long previousCount = -1;
		for ( final long count : histogram )
		{
			if ( count != previousCount )
			{
				values.add( count );
				previousCount = count;
			}
		}

		// should get only two values
		assertEquals( 2, values.size() );

		// values should be off by one
		final long v1 = values.get( 0 );
		final long v2 = values.get( 1 );
		assertEquals( v1 + 1, v2 );
	}

	/**
	 * Tests generating next doubles.
	 * 
	 * @param value
	 * @param number
	 */
	private void testNextDouble( final double value, final int number )
	{
		final double[] values = new double[ number ];

		// generate 'number' adjacent double values
		int i = 0;
		values[ i ] = value;
		do
		{
			final double prev = values[ i++ ];
			final double next = DoubleUtil.nextDouble( prev, true );
			values[ i ] = next;
		}
		while ( i < number - 1 );

		// go down the list backwards
		do
		{
			final double next = values[ i-- ];
			final double prev = DoubleUtil.nextDouble( next, false );
			assertEquals( values[ i ], prev, prev / EPSILON_FACTOR );
		}
		while ( i > 0 );
	}

}
