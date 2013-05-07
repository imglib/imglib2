package net.imglib2.ops.expression.examples;

import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.ops.expression.ops.IntervalReduce;
import net.imglib2.ops.expression.ops.Ops;
import net.imglib2.type.numeric.real.FloatType;

public class ReduceExample
{
	/**
	 * Compute the minimum, maximum, and sum of the FloatType values in an {@link IterableInterval}.
	 *
	 * {@link IntervalReduce} lifts a (pixelwise in this case) binary operation <em>T x T -> T</em> to a unary operation <em>IterableInterval&lt;T&gt; -> T</em>
	 *
	 *  @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">reduce on wikipedia</a>
	 */
	public static void main( final String[] args )
	{
		final float[] data = new float[] { 12, 7, 4, 100, 9, 30, 34, 97.2f, 13.4f };
		final Img< FloatType > imgA = ArrayImgs.floats( data, data.length );

		final IntervalReduce< FloatType > reduceMin = Ops.reduce( Ops.<FloatType>min() );
		reduceMin.input1().setConst( imgA );
		reduceMin.output().setConst( new FloatType() );
		System.out.println( "minimum is " + reduceMin.get().get() );

		System.out.println( "maximum is " + Ops.compute( Ops.reduce( Ops.<FloatType>max() ), new FloatType(), imgA ) );

		System.out.println( "sum is " + Ops.compute( Ops.reduce( Ops.<FloatType>add() ), new FloatType(), imgA ) );
	}
}
