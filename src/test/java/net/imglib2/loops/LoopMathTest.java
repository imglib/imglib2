package net.imglib2.loops;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopMath;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

import static net.imglib2.loops.LoopMath.Max;
import static net.imglib2.loops.LoopMath.Div;

public class LoopMathTest
{
	protected static boolean testLoopMath1( )
	{	
		final long[] dims = new long[]{ 100, 100, 100 };
		
		final ArrayImg< ARGBType, ? > rgb = new ArrayImgFactory< ARGBType >( new ARGBType() ).create( dims );
		
		final RandomAccessibleInterval< UnsignedByteType >
			red = Converters.argbChannel( rgb, 1 ),
			green = Converters.argbChannel( rgb, 2 ),
			blue = Converters.argbChannel( rgb, 3 );
		
		for ( final UnsignedByteType t : Views.iterable(red) ) t.set( 10 );
		for ( final UnsignedByteType t : Views.iterable(green) ) t.set( 30 );
		for ( final UnsignedByteType t : Views.iterable(blue) ) t.set( 20 );
		
		final ArrayImg< FloatType, ? > brightness = new ArrayImgFactory< FloatType >( new FloatType() ).create( dims );
		
		try {
			LoopMath.compute( brightness, new Div( new Max( red, new Max( green, blue ) ), 3.0 ) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double sum = 0;
		
		for ( final FloatType t: brightness )
			sum += t.getRealDouble();
		
		System.out.println( "Sum: " + sum );

		return 100 * 100 * 100 * 10 == sum;
	}
	
	@Test
	public void test() {
		assertTrue( testLoopMath1() );
	}
	
	static public void main(String[] args) {
		new LoopMathTest().test();
	}
}
