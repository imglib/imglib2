package net.imglib2.ops.iterable.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class MidGrey< T extends RealType< T >, IN extends Iterator< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_c;

	public MidGrey( double c )
	{
		m_c = c;
	}

	@Override
	public BitType compute( IN input, T px, BitType output )
	{

		double min = Double.MAX_VALUE;;
		double max = -Double.MAX_VALUE;

		while ( input.hasNext() )
		{
			double next = input.next().getRealDouble();
			min = Math.min( next, min );
			max = Math.max( next, max );
		}

		output.set( px.getRealDouble() > ( ( max + min ) / 2 ) - m_c );

		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new MidGrey< T, IN >( m_c );
	}

}
