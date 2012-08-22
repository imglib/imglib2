package net.imglib2.ops.img;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.Type;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public final class BinaryOperationAssignment< I extends Type< I >, V extends Type< V >, O extends Type< O >> implements BinaryOperation< IterableInterval< I >, IterableInterval< V >, IterableInterval< O >>
{
	/* Operation to be wrapped */
	private final BinaryOperation< I, V, O > m_op;

	public BinaryOperationAssignment( final net.imglib2.ops.operation.BinaryOperation< I, V, O > op )
	{
		m_op = op;
	}

	@Override
	public IterableInterval< O > compute( IterableInterval< I > input1, IterableInterval< V > input2, IterableInterval< O > output )
	{

		if ( !input1.iterationOrder().equals( input2.iterationOrder() ) || !input1.iterationOrder().equals( output.iterationOrder() ) ) { throw new IllegalArgumentException( "Intervals are not compatible" ); }

		Cursor< I > c1 = input1.cursor();
		Cursor< V > c2 = input2.cursor();
		Cursor< O > resC = output.cursor();

		while ( c1.hasNext() )
		{
			c1.fwd();
			c2.fwd();
			resC.fwd();
			m_op.compute( c1.get(), c2.get(), resC.get() );
		}
		return output;
	}

	@Override
	public BinaryOperation< IterableInterval< I >, IterableInterval< V >, IterableInterval< O >> copy()
	{
		return new BinaryOperationAssignment< I, V, O >( m_op.copy() );
	}

}
