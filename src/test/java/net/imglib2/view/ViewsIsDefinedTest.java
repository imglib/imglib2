package net.imglib2.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link Views#isDefined}.
 * 
 * @author Stefan Helfrich (University of Konstanz)
 */
public class ViewsIsDefinedTest
{
	private final long[] dim = new long[] { 10, 10 };

	private ArrayImg< UnsignedByteType, ByteArray > rai;

	@Before
	public void setUp()
	{
		rai = ArrayImgs.unsignedBytes( dim );
	}

	@Test
	public void testIsDefinedUnextended()
	{
		// Complete overlap
		Interval interval = Intervals.createMinMax( 2, 2, 8, 8 );
		assertTrue( Views.isDefined( rai, interval ) );

		// Partial overlap
		interval = Intervals.createMinMax( -1, -1, 5, 5 );
		assertFalse( Views.isDefined( rai, interval ) );

		// No overlap
		interval = Intervals.createMinMax( -5, -5, -1, -1 );
		assertFalse( Views.isDefined( rai, interval ) );
	}

	/**
	 * Tests {@link Views#extend}.
	 */
	@Test
	public void testIsDefinedExtended()
	{
		RandomAccessibleInterval< UnsignedByteType > extendedRai = Views.interval( Views.extendBorder( rai ), rai );

		// Complete overlap
		Interval interval = Intervals.createMinMax( 2, 2, 8, 8 );
		assertTrue( Views.isDefined( extendedRai, interval ) );

		// Partial overlap
		interval = Intervals.createMinMax( -1, -1, 5, 5 );
		assertTrue( Views.isDefined( extendedRai, interval ) );

		// No overlap
		interval = Intervals.createMinMax( -5, -5, -1, -1 );
		assertTrue( Views.isDefined( extendedRai, interval ) );
	}

	/**
	 * Tests {@link Views#stack} and {@link Views#concatenate}.
	 */
	@Test
	public void testIsDefinedStacked()
	{
		// 1D RAI
		ArrayImg< UnsignedByteType, ByteArray > rai1 = ArrayImgs.unsignedBytes( 10 );
		IntervalView< UnsignedByteType > interval1 = Views.interval( Views.extendBorder( rai1 ), rai );

		// 1D RAI
		ArrayImg< UnsignedByteType, ByteArray > rai2 = ArrayImgs.unsignedBytes( 10 );
		IntervalView< UnsignedByteType > interval2 = Views.interval( Views.extendBorder( rai2 ), rai2 );

		// 2D RAI extended only in second dimension
		RandomAccessibleInterval< UnsignedByteType > stack = Views.stack( interval1, interval2 );

		assertTrue( Views.isDefined( stack, Intervals.createMinMax( -1, 0, 1, 1 ) ) );
		assertTrue( Views.isDefined( stack, Intervals.createMinMax( 0, 0, 10, 1 ) ) );
		assertFalse( Views.isDefined( stack, Intervals.createMinMax( -1, -1, 1, 2 ) ) );
	}

	/**
	 * Tests {@link Views#translate}.
	 */
	@Test
	public void testIsDefinedTranslated()
	{
		RandomAccessibleInterval< UnsignedByteType > translatedRai = Views.translate( rai, 5, 5 );
		assertFalse( Views.isDefined( translatedRai, Intervals.createMinMax( 0, 0, 1, 1 ) ) );

		RandomAccessibleInterval< UnsignedByteType > translatedExtendedRai = Views.translate( Views.interval( Views.extendBorder( rai ), rai ), 5, 5 );
		assertFalse( Views.isDefined( translatedExtendedRai, Intervals.createMinMax( 0, 0, 1, 1 ) ) );
	}

	/**
	 * Tests {@link Views#permute}.
	 */
	@Test
	public void testIsDefinedPermuted()
	{
		RandomAccessibleInterval< UnsignedByteType > permutedRai = Views.permute( rai, 0, 1 );

		// Complete overlap
		Interval interval = Intervals.createMinMax( 2, 2, 8, 8 );
		assertTrue( Views.isDefined( permutedRai, interval ) );

		// Partial overlap
		interval = Intervals.createMinMax( -1, -1, 5, 5 );
		assertFalse( Views.isDefined( permutedRai, interval ) );

		// No overlap
		interval = Intervals.createMinMax( -5, -5, -1, -1 );
		assertFalse( Views.isDefined( permutedRai, interval ) );
	}

	/**
	 * Tests {@link Views#addDimension}.
	 */
	@Test
	public void testIsDefinedAddedDimension()
	{
		RandomAccessibleInterval< UnsignedByteType > addedDimensionRai = Views.addDimension( rai, 0, 9 );

		// Complete overlap
		Interval interval = Intervals.createMinMax( 2, 2, 2, 8, 8, 8 );
		assertTrue( Views.isDefined( addedDimensionRai, interval ) );

		// Partial overlap
		interval = Intervals.createMinMax( -1, -1, -1, 5, 5, 5 );
		assertFalse( Views.isDefined( addedDimensionRai, interval ) );

		// No overlap
		interval = Intervals.createMinMax( -5, -5, -5, -1, -1, -1 );
		assertFalse( Views.isDefined( addedDimensionRai, interval ) );
	}
}
