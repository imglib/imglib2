package net.imglib2.ops.operation.iterable.localthresholder.binary;

import java.util.Iterator;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class MeanLocalThreshold< T extends RealType< T >, IN extends Iterator< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_c;

	public MeanLocalThreshold( double c )
	{
		m_c = c;
	}

	@Override
	public BitType compute( IN input, T px, BitType output )
	{
		int numElements = 0;
		double mean = 0;

		while ( input.hasNext() )
		{
			mean += input.next().getRealDouble();
			numElements++;
		}

		mean /= numElements;

		output.set( px.getRealDouble() > mean - m_c );
		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new MeanLocalThreshold< T, IN >( m_c );
	}

}
