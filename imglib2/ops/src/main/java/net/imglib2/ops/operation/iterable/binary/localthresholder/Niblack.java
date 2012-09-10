package net.imglib2.ops.operation.iterable.binary.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class Niblack< T extends RealType< T >, IN extends Iterator< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_c;

	private double m_k;

	public Niblack( double k, double c )
	{
		m_c = c;
		m_k = k;
	}

	@Override
	public BitType compute( IN input, T px, BitType output )
	{

		double sum = 0;
		double sumSqr = 0;
		int n = 0;

		while ( input.hasNext() )
		{
			double val = input.next().getRealDouble();
			n++;
			sum += val;
			sumSqr += val * val;
		}

		double mean = sum / n;
		double variance = ( sumSqr - ( sum * mean ) ) / ( n - 1 );

		output.set( px.getRealDouble() > mean + m_k * Math.sqrt( variance ) - m_c );

		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new Niblack< T, IN >( m_k, m_c );
	}

}
