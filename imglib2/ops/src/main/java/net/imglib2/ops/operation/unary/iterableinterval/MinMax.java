package net.imglib2.ops.operation.unary.iterableinterval;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.ops.UnaryOutputOperation;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;

/**
 * 
 * @author dietzc
 * 
 * @param <T>
 */
public final class MinMax< T extends RealType< T >> implements UnaryOutputOperation< IterableInterval< T >, Pair< T, T >>
{

	private double m_saturation;

	private MakeHistogram< T > m_histOp;

	public MinMax( double saturation, T type )
	{
		m_saturation = saturation;

		if ( saturation != 0 )
		{

			int bins;
			if ( !( type instanceof IntegerType ) )
			{
				bins = Short.MAX_VALUE * 2;
			}
			else
			{
				bins = ( int ) ( type.getMaxValue() - type.getMinValue() + 1 );
			}

			m_histOp = new MakeHistogram< T >( bins );
		}
	}

	public MinMax()
	{
		this( 0, null );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair< T, T > createEmptyOutput( IterableInterval< T > op )
	{
		final T t = op.iterator().next();
		return new Pair< T, T >( t.createVariable(), t.createVariable() );
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public Pair< T, T > compute( IterableInterval< T > op, Pair< T, T > r )
	{

		if ( m_saturation == 0 )
		{
			final Iterator< T > it = op.iterator();
			r.a.setReal( r.a.getMaxValue() );
			r.b.setReal( r.b.getMinValue() );
			while ( it.hasNext() )
			{
				T i = it.next();
				if ( r.a.compareTo( i ) > 0 )
					r.a.set( i );
				if ( r.b.compareTo( i ) < 0 )
					r.b.set( i );
			}
		}
		else
		{
			calcMinMaxWithSaturation( op, r, m_histOp.compute( op, m_histOp.createEmptyOutput( op ) ) );
		}

		return r;
	}

	private void calcMinMaxWithSaturation( IterableInterval< T > interval, Pair< T, T > r, OpsHistogram hist )
	{
		int histMin = 0, histMax;
		int threshold = ( int ) ( interval.size() * m_saturation / 200.0 );

		// find min
		int pCount = 0;
		for ( int i = 0; i < hist.numBins(); i++ )
		{
			pCount += hist.get( i );
			if ( pCount > threshold )
			{
				histMin = i;
				break;
			}
		}

		// find max
		pCount = 0;
		histMax = hist.numBins() - 1;
		for ( int i = hist.numBins() - 1; i >= 0; i-- )
		{
			pCount += hist.get( i );
			if ( pCount > threshold )
			{
				histMax = i;
				break;
			}
		}
		r.a.setReal( ( histMin * ( ( r.a.getMaxValue() - r.a.getMinValue() ) / hist.numBins() ) ) + r.a.getMinValue() );
		r.b.setReal( ( histMax * ( ( r.a.getMaxValue() - r.a.getMinValue() ) / hist.numBins() ) ) + r.a.getMinValue() );
	}

	@Override
	public UnaryOutputOperation< IterableInterval< T >, Pair< T, T >> copy()
	{
		return new MinMax< T >();
	}

	@Override
	public Pair< T, T > compute( IterableInterval< T > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}
}
