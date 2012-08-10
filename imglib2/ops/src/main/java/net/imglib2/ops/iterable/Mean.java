package net.imglib2.ops.iterable;

import java.util.Iterator;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Mean< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterator< T >, V >
{

	@Override
	public V compute( Iterator< T > op, V r )
	{
		double sum = 0;
		double ctr = 0;
		while ( op.hasNext() )
		{
			sum += op.next().getRealDouble();
			ctr++;
		}
		r.setReal( sum / ctr );

		return r;
	}

	@Override
	public UnaryOperation< Iterator< T >, V > copy()
	{
		return new Mean< T, V >();
	}

}
