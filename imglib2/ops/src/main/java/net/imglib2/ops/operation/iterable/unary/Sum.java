package net.imglib2.ops.operation.iterable.unary;

import java.util.Iterator;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Sum< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterator< T >, V >
{

	@Override
	public V compute( Iterator< T > op, V r )
	{
		double sum = 0;
		while ( op.hasNext() )
		{
			sum += op.next().getRealDouble();
		}
		r.setReal( sum );

		return r;
	}

	@Override
	public UnaryOperation< Iterator< T >, V > copy()
	{
		return new Sum< T, V >();
	}

}
