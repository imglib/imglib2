package net.imglib2.ops.operation.img.unary;

import net.imglib2.img.Img;
import net.imglib2.ops.img.ConcatenatedBufferedUnaryOperation;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class IterativeImgToImgOperation< TT extends RealType< TT >> extends ConcatenatedBufferedUnaryOperation< Img< TT >>
{

	private final UnaryOperation< Img< TT >, Img< TT >> m_op;

	private final int m_numIterations;

	public IterativeImgToImgOperation( UnaryOperation< Img< TT >, Img< TT >> op, int numIterations )
	{
		super( getOpArray( op, numIterations ) );
		m_op = op;
		m_numIterations = numIterations;

	}

	private static < TTT extends RealType< TTT >> UnaryOperation< Img< TTT >, Img< TTT >>[] getOpArray( UnaryOperation< Img< TTT >, Img< TTT >> op, int numIterations )
	{

		@SuppressWarnings( "unchecked" )
		UnaryOperation< Img< TTT >, Img< TTT >>[] ops = new UnaryOperation[ numIterations ];

		for ( int i = 0; i < numIterations; i++ )
		{
			ops[ i ] = op.copy();
		}

		return ops;
	}

	@Override
	protected Img< TT > getBuffer( Img< TT > input )
	{
		return input.factory().create( input, input.firstElement().createVariable() );
	}

	@Override
	public UnaryOperation< Img< TT >, Img< TT >> copy()
	{
		return new IterativeImgToImgOperation< TT >( m_op.copy(), m_numIterations );
	}
}
