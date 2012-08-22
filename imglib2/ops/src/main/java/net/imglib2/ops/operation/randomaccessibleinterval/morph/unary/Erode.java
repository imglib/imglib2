package net.imglib2.ops.operation.randomaccessibleinterval.morph.unary;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.logic.BitType;

public final class Erode< I extends RandomAccessibleInterval< BitType > & IterableInterval< BitType >> implements UnaryOperation< I, I >
{

	private final int m_neighbourhoodCount;

	private final ConnectedType m_type;

	private final BinaryOps< I > m_binOps;

	/**
	 * @param type
	 * @param neighbourhoodCount
	 * @param iterations
	 *            number of iterations, at least 1
	 */
	public Erode( ConnectedType type, final int neighbourhoodCount )
	{
		m_neighbourhoodCount = neighbourhoodCount;
		m_type = type;
		m_binOps = new BinaryOps< I >();
	}

	@Override
	public I compute( I op, I r )
	{
		m_binOps.erode( m_type, r, op, m_neighbourhoodCount );
		return r;

	}

	@Override
	public UnaryOperation< I, I > copy()
	{
		return new Erode< I >( m_type, m_neighbourhoodCount );
	}
}
