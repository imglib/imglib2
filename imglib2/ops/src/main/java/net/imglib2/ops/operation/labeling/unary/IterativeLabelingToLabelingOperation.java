package net.imglib2.ops.operation.labeling.unary;

import net.imglib2.labeling.Labeling;
import net.imglib2.ops.img.ConcatenatedBufferedUnaryOperation;
import net.imglib2.ops.operation.UnaryOperation;

public class IterativeLabelingToLabelingOperation< L extends Comparable< L >> extends ConcatenatedBufferedUnaryOperation< Labeling< L >>
{

	private final UnaryOperation< Labeling< L >, Labeling< L >> m_op;

	private final int m_numIterations;

	public IterativeLabelingToLabelingOperation( UnaryOperation< Labeling< L >, Labeling< L >> op, int numIterations )
	{
		super( getOpArray( op, numIterations ) );
		m_op = op;
		m_numIterations = numIterations;

	}

	private static < LL extends Comparable< LL >> UnaryOperation< Labeling< LL >, Labeling< LL >>[] getOpArray( UnaryOperation< Labeling< LL >, Labeling< LL >> op, int numIterations )
	{

		@SuppressWarnings( "unchecked" )
		UnaryOperation< Labeling< LL >, Labeling< LL >>[] ops = new UnaryOperation[ numIterations ];

		for ( int i = 0; i < numIterations; i++ )
		{
			ops[ i ] = op.copy();
		}

		return ops;
	}

	@Override
	protected Labeling< L > getBuffer( Labeling< L > input )
	{
		return input.< L >factory().create( input );
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new IterativeLabelingToLabelingOperation< L >( m_op.copy(), m_numIterations );
	}
}
