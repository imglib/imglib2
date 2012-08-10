package net.imglib2.ops.operation.unary.iterableinterval;

import java.util.Iterator;

import net.imglib2.ops.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

public final class MakeHistogram< T extends RealType< T >> implements UnaryOutputOperation< Iterable< T >, OpsHistogram >
{

	int m_numBins = 0;

	public MakeHistogram()
	{
		this( -1 );
	}

	public MakeHistogram( int numBins )
	{
		m_numBins = numBins;
	}

	@Override
	public final OpsHistogram createEmptyOutput( Iterable< T > op )
	{
		return m_numBins <= 0 ? new OpsHistogram( op.iterator().next().createVariable() ) : new OpsHistogram( m_numBins, op.iterator().next().createVariable() );
	}

	@Override
	public final OpsHistogram compute( Iterable< T > op, OpsHistogram r )
	{
		final Iterator< T > it = op.iterator();
		r.clear();
		while ( it.hasNext() )
		{
			r.incByValue( it.next().getRealDouble() );
		}

		return r;
	}

	@Override
	public OpsHistogram compute( Iterable< T > op )
	{
		return compute( op, createEmptyOutput( op ) );
	}

	@Override
	public UnaryOutputOperation< Iterable< T >, OpsHistogram > copy()
	{
		return new MakeHistogram< T >( m_numBins );
	}
}
