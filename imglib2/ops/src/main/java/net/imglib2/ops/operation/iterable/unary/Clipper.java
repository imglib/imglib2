package net.imglib2.ops.operation.iterable.unary;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Clipper< T extends RealType< T >> implements UnaryOperation< Iterable< T >, T >
{

	private final UnaryOperation< Iterable< T >, DoubleType > m_op;

	private final DoubleType m_type;

	public Clipper( UnaryOperation< Iterable< T >, DoubleType > op )
	{
		m_op = op;
		m_type = new DoubleType();
	}

	@Override
	public final T compute( Iterable< T > input, T output )
	{

		double val = m_op.compute( input, m_type ).getRealDouble();

		if ( val < output.getMinValue() )
			output.setReal( output.getMinValue() );
		else if ( val > output.getMaxValue() )
			output.setReal( output.getMaxValue() );
		else
			output.setReal( val );

		return output;
	}

	@Override
	public final UnaryOperation< Iterable< T >, T > copy()
	{
		return new Clipper< T >( m_op.copy() );
	}

}
