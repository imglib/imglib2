package net.imglib2.ops.operation.unary.iterableinterval;

import net.imglib2.type.numeric.RealType;

public final class OpsHistogram
{

	private final int[] m_hist;

	private final int m_bins;

	private final double m_min;

	private final double m_max;

	private final double m_scale;

	public OpsHistogram( final int[] hist, final double min, final double max )
	{
		m_bins = hist.length;
		m_hist = hist;
		m_min = min;
		m_max = max;
		m_scale = ( m_bins - 1 ) / ( m_max - m_min );
	}

	public OpsHistogram( final int bins, final double min, final double max )
	{
		this( new int[ bins ], min, max );
	}

	public < T extends RealType< T >> OpsHistogram( final int bins, final T type )
	{
		this( new int[ bins ], type.getMinValue(), type.getMaxValue() );
	}

	public < T extends RealType< T >> OpsHistogram( final T type )
	{
		this( 256, type );
	}

	public final void clear()
	{
		for ( int i = 0; i < m_hist.length; i++ )
		{
			m_hist[ i ] = 0;
		}
	}

	public final int[] hist()
	{
		return m_hist;
	}

	public final int numBins()
	{
		return m_bins;
	}

	public final double min()
	{
		return m_min;
	}

	public final double max()
	{
		return m_max;
	}

	public final int get( final int i )
	{
		return m_hist[ i ];
	}

	public final void inc( final int i )
	{
		++m_hist[ i ];
	}

	public final int getByValue( final double v )
	{
		return get( valueToBin( v ) );
	}

	public final void incByValue( final double v )
	{
		inc( valueToBin( v ) );
	}

	public final int valueToBin( final double v )
	{
		return ( int ) ( ( v - m_min ) * m_scale );
	}

	public final double binToValue( final int i )
	{
		return ( i / m_scale ) + m_min;
	}

}
