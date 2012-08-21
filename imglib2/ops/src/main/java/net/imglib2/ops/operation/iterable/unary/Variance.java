package net.imglib2.ops.operation.iterable.unary;

import java.util.Iterator;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author siedentopl, dietzc
 * 
 * @param <T>
 * @param <V>
 */
public class Variance< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterator< T >, V >
{

	@Override
	public V compute( Iterator< T > input, V output )
	{
		double sum = 0;
		double sumSqr = 0;
		int n = 0;

		while ( input.hasNext() )
		{
			double px = input.next().getRealDouble();
			n++;
			sum += px;
			sumSqr += px * px;
		}

		output.setReal( ( sumSqr - ( sum * sum / n ) ) / ( n - 1 ) );
		return output;
	}

	@Override
	public UnaryOperation< Iterator< T >, V > copy()
	{
		return new Variance< T, V >();
	}

}
