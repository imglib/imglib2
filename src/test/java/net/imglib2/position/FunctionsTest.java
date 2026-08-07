package net.imglib2.position;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class FunctionsTest
{

	private static final String checker1 = "01010";
	private static final String checker2 = "01010101010101010101010101010101010";
	private static final String checker3 = "01010101010101010101101010101010101010100101010101010101010110101010101010101010010101010101010101011010101010101010101001010101010101010101";

	private static final String checker12 = "00110";
	private static final String checker22 = "00001000010000100001111101111011110";
	private static final String checker32 = "00110001101100111001001100011011001110011100111001001100011011001110010011000110001100011011001110010011000110110011100111001110010011000110";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{}

	@Test
	public void testChecker()
	{
		final IntType on = new IntType( 1 );
		final IntType off = new IntType( 0 );

		int i = 0;
		for ( final IntType t : Views.interval( Functions.checker( 1, on, off ), Intervals.createMinSize( 0, 5 ) ) )
		{
			assertEquals( Integer.parseInt( checker1.substring( i, ++i ) ), t.get() );
		}

		final int width = 5;
		i = 0;
		for ( final IntType t : Views.interval( Functions.checker( 2, on, off ), Intervals.createMinSize( 0, 0, width, 7 ) ) ) {
			assertEquals( Integer.parseInt( checker2.substring( i, ++i ) ), t.get() );
		}

		final int height = 4;
		i = 0;
		for ( final IntType t : Views.interval( Functions.checker( 3, on, off ), Intervals.createMinSize( 0, 0, 0, width, height, 7 ) ) ) {
			assertEquals( Integer.parseInt( checker3.substring( i, ++i ) ), t.get() );
		}
	}

	@Test
	public void testChecker2()
	{
		final IntType on = new IntType( 1 );
		final IntType off = new IntType( 0 );

		int i = 0;
		for ( final IntType t : Views.interval( Functions.checker( 1, on, off, 1 ), Intervals.createMinSize( 0, 5 ) ) )
		{
//			System.out.print( t.get() );
			assertEquals( Integer.parseInt( checker12.substring( i, ++i ) ), t.get() );
		}

		System.out.println(  );

		final int width = 5;
		i = 0;
		for ( final IntType t : Views.interval( Functions.checker( 2, on, off, 2 ), Intervals.createMinSize( 0, 0, width, 7 ) ) )
		{
//			if ( i % width == 0 )
//				System.out.println(  );
//
//			System.out.print( t.get() );
//			++i;
			assertEquals( Integer.parseInt( checker22.substring( i, ++i ) ), t.get() );
		}

		System.out.println(  );

		final int height = 4;
		i = 0;
		for ( final IntType t : Views.interval( Functions.checker( 3, on, off, 1 ), Intervals.createMinSize( 0, 0, 0, width, height, 7 ) ) )
		{
//			System.out.print( t.get() );
			assertEquals( Integer.parseInt( checker32.substring( i, ++i ) ), t.get() );
		}
	}

}
