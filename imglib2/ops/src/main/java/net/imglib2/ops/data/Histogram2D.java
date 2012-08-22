package net.imglib2.ops.data;

import net.imglib2.type.numeric.RealType;

public final class Histogram2D
{

	private final int[] m_hist;

	private final int m_size;

	private final double m_min;

	private final double m_max;

	private final double m_scale;

	public Histogram2D( final int size, final double min, final double max )
	{
		m_size = size;
		m_hist = new int[ size * size ];
		m_min = min;
		m_max = max;
		m_scale = ( m_size - 1 ) / ( m_max - m_min );
	}

	public < T extends RealType< T >> Histogram2D( final int bins, final T type )
	{
		this( bins, type.getMinValue(), type.getMaxValue() );
	}

	public < T extends RealType< T >> Histogram2D( final T type )
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

	public final int numBins()
	{
		return m_size;
	}

	public final double min()
	{
		return m_min;
	}

	public final double max()
	{
		return m_max;
	}

	public final int get( final int x, final int y )
	{
		return m_hist[ y * m_size + x ];
	}

	public final void inc( final int x, final int y )
	{
		++m_hist[ y * m_size + x ];
	}

	public final int getByValue( final double x, final double y )
	{
		return get( valueToBin( x ), valueToBin( y ) );
	}

	public final void incByValue( final double x, final double y )
	{
		inc( valueToBin( x ), valueToBin( y ) );
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
