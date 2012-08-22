package net.imglib2.ops.operation.iterableinterval.multilevelthresholder.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

/**
 * friedrichm (University of Konstanz)
 */
public class MultilevelThresholderOp< T extends RealType< T >, IN extends IterableInterval< T >, OUT extends IterableInterval< T >> implements UnaryOperation< IN, OUT >
{

	private final UnaryOutputOperation< IN, ThresholdValueCollection > m_op;

	public MultilevelThresholderOp( UnaryOutputOperation< IN, ThresholdValueCollection > op )
	{
		m_op = op;
	}

	@Override
	public UnaryOperation< IN, OUT > copy()
	{
		return new MultilevelThresholderOp< T, IN, OUT >( m_op.copy() );
	}

	@Override
	public OUT compute( IN input, OUT out )
	{

		if ( !input.iterationOrder().equals( out.iterationOrder() ) ) { throw new IllegalArgumentException( "IterationOrders not the same in StandardMultilevelThresholder" ); }

		Cursor< T > outputCursor = out.cursor();
		Cursor< T > inputCursor = input.cursor();

		ThresholdValueCollection thresholdValues = m_op.compute( input );

		double[] sortedValues = thresholdValues.getSortedVector();

		while ( inputCursor.hasNext() )
		{
			outputCursor.fwd();

			double value = inputCursor.next().getRealDouble();

			int idx = 0;
			for ( int d = 0; d < sortedValues.length; d++ )
			{
				if ( value > sortedValues[ d ] )
				{
					idx++;
				}
				else
				{
					break;
				}
			}
			outputCursor.get().setReal( idx );
		}
		return out;
	}

}
