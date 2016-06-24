/**
 *
 */
package net.imglib2.transform.integer.permutation;

import java.util.ArrayList;
import java.util.Collections;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Philipp Hanslovsky (hanslovskyp@janelia.hhmi.org)
 *
 */
public class AbstractPermutationTransformTest
{

	/**
	 * @author Philipp Hanslovsky (hanslovskyp@janelia.hhmi.org) Wrapper class
	 *         for AbstractPermutatioTransform to test protected methods.
	 */
	public static class MockPermutation extends AbstractPermutationTransform
	{

		// test protected methods
		@Override
		public int apply( final int x )
		{
			return super.apply( x );
		}

		@Override
		public long applyChecked( final int x )
		{
			return super.applyChecked( x );
		}

		@Override
		public int applyInverse( final int y )
		{
			return super.applyInverse( y );
		}

		@Override
		public long applyInverseChecked( final int y )
		{
			return super.applyInverseChecked( y );
		}

		public MockPermutation( final int[] lut )
		{
			super( lut );
		}

		// ignore unimplemented methods
		@Override
		public void applyInverse( final long[] source, final long[] target )
		{

		}

		@Override
		public void applyInverse( final int[] source, final int[] target )
		{

		}

		@Override
		public void applyInverse( final Positionable source, final Localizable target )
		{

		}

		@Override
		public MockPermutation inverse()
		{
			return new MockPermutation( this.inverseLut );
		}

		@Override
		public int numSourceDimensions()
		{
			return 0;
		}

		@Override
		public int numTargetDimensions()
		{
			return 0;
		}

		@Override
		public void apply( final long[] source, final long[] target )
		{

		}

		@Override
		public void apply( final int[] source, final int[] target )
		{

		}

		@Override
		public void apply( final Localizable source, final Positionable target )
		{

		}

	}

	private final int size = 200;

	private final int[] lut = new int[ this.size ];

	private final int[] inv = new int[ this.size ];

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		final ArrayList< Integer > al = new ArrayList< Integer >();
		for ( int i = 0; i < this.size; ++i )
			al.add( i );

		Collections.shuffle( al );

		for ( int i = 0; i < this.size; ++i )
		{
			this.lut[ i ] = al.get( i );
			this.inv[ this.lut[ i ] ] = i;
		}

	}

	@Test
	public void test()
	{
		final MockPermutation transform = new MockPermutation( this.lut );
		final MockPermutation inverse = transform.inverse();
		int resInt;
		long resLong;
		for ( int i = 0; i < this.size; ++i )
		{
			resInt = transform.apply( i );
			resLong = transform.applyChecked( i );
			Assert.assertEquals( this.lut[ i ], resInt );
			Assert.assertEquals( this.lut[ i ], resLong );

			resInt = inverse.apply( transform.apply( i ) );
			resLong = transform.apply( inverse.apply( i ) );
			Assert.assertEquals( i, resInt );
			Assert.assertEquals( i, resLong );

			resInt = inverse.apply( i );
			resLong = inverse.applyChecked( i );
			Assert.assertEquals( this.inv[ i ], resInt );
			Assert.assertEquals( this.inv[ i ], resLong );
		}

		Assert.assertEquals( -Long.MAX_VALUE, transform.applyChecked( -1 ) );
		Assert.assertEquals( Long.MAX_VALUE, transform.applyChecked( this.size + 1 ) );

	}

}
