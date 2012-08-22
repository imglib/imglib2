package net.imglib2.ops.operation.randomaccessibleinterval.unary.morph;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.logic.BitType;

public final class Dilate< I extends RandomAccessibleInterval< BitType > & IterableInterval< BitType >> implements UnaryOperation< I, I >
{

	private final int m_neighbourhoodCount;

	private final ConnectedType m_type;

	private final BinaryOps< I > m_binOps;

	public Dilate( ConnectedType type, final int neighbourhoodCount )
	{
		m_neighbourhoodCount = neighbourhoodCount;
		m_type = type;
		m_binOps = new BinaryOps< I >();
	}

	@Override
	public I compute( I op, I r )
	{
		m_binOps.dilate( m_type, r, op, m_neighbourhoodCount );
		return r;

	}

	@Override
	public UnaryOperation< I, I > copy()
	{
		return new Dilate< I >( m_type, m_neighbourhoodCount );
	}
}
