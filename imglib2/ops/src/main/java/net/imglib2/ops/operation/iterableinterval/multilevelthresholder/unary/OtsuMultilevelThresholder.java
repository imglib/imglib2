package net.imglib2.ops.operation.iterableinterval.multilevelthresholder.unary;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.iterableinterval.unary.MakeHistogram;
import net.imglib2.ops.operation.iterableinterval.unary.OpsHistogram;
import net.imglib2.type.numeric.RealType;

/**
 * friedrichm (University of Konstanz)
 */
public class OtsuMultilevelThresholder< T extends RealType< T >, IN extends IterableInterval< T > & RandomAccessibleInterval< T >> implements UnaryOutputOperation< IN, ThresholdValueCollection >
{

	private int m_numberOfLevels;

	private double m_maxValue;

	private double[][] m_p, m_s, m_h;

	private int m_numBins;

	public OtsuMultilevelThresholder()
	{
		this( 2, 256 );
	}

	public OtsuMultilevelThresholder( int numLevels )
	{
		this( numLevels, 256 );
	}

	public OtsuMultilevelThresholder( int numLevels, int numBins )
	{
		if ( numLevels < 2 ) { throw new IllegalArgumentException( "Number of levels must be greater than 1" ); }
		m_numberOfLevels = numLevels;
		m_numBins = numBins;
	}

	@Override
	public ThresholdValueCollection compute( IN input, ThresholdValueCollection thresholdValues ) throws RuntimeException
	{

		// Thresholds must be scaled
		T inVar = input.firstElement().createVariable();

		OpsHistogram histogram = new MakeHistogram< T >( ( int ) Math.min( m_numBins, inVar.getMinValue() - inVar.getMaxValue() ) ).compute( input );

		m_maxValue = 0.0;

		m_p = new double[ m_numBins + 1 ][ m_numBins + 1 ];
		m_s = new double[ m_numBins + 1 ][ m_numBins + 1 ];
		m_h = new double[ m_numBins + 1 ][ m_numBins + 1 ];

		calculatePLookup( histogram );
		calculateSLookup( histogram );
		calculateHLookup();

		int[] tempThresholdList = new int[ m_numberOfLevels ];
		calculateThresholdValues( thresholdValues, 1, m_numBins - m_numberOfLevels + 1, 0.0, 0, tempThresholdList );

		// Threshold values must be scaled according to the number of
		// bins.
		thresholdValues.scale( m_numBins, inVar.getMinValue(), inVar.getMaxValue() );

		return thresholdValues;
	}

	private void calculateThresholdValues( ThresholdValueCollection thresholdValues, int start, int end, double curValue, int curIndex, int[] tList )
	{

		for ( int i = start; i < end; i++ )
		{
			double h1 = m_h[ start ][ i ];
			double h2 = m_h[ i + 1 ][ end + 1 ];
			double h = curValue + h1 + h2;
			tList[ curIndex ] = i;

			if ( curIndex == m_numberOfLevels - 2 )
			{
				if ( h > m_maxValue )
				{
					for ( int j = 0; j < m_numberOfLevels - 1; j++ )
					{
						thresholdValues.set( j, tList[ j ] );
					}
					m_maxValue = h;
				}
			}
			else
			{
				calculateThresholdValues( thresholdValues, i + 1, end + 1, curValue + h1, curIndex + 1, tList );
			}
		}
	}

	private void calculatePLookup( OpsHistogram histogram )
	{
		m_p[ 1 ][ 0 ] = 0;
		for ( int v = 1; v <= m_numBins; v++ )
		{
			m_p[ 1 ][ v ] = m_p[ 1 ][ v - 1 ] + histogram.get( v - 1 );
		}
		for ( int u = 2; u <= m_numBins; u++ )
		{
			for ( int v = 1; v <= m_numBins; v++ )
			{
				m_p[ u ][ v ] = m_p[ 1 ][ v ] - m_p[ 1 ][ u - 1 ];
			}
		}
	}

	private void calculateSLookup( OpsHistogram histogram )
	{
		m_s[ 1 ][ 0 ] = 0;
		for ( int v = 1; v <= m_numBins; v++ )
		{
			m_s[ 1 ][ v ] = m_s[ 1 ][ v - 1 ] + v * histogram.get( v - 1 );
		}
		for ( int u = 2; u <= m_numBins; u++ )
		{
			for ( int v = 1; v <= m_numBins; v++ )
			{
				m_s[ u ][ v ] = m_s[ 1 ][ v ] - m_s[ 1 ][ u - 1 ];
			}
		}
	}

	private void calculateHLookup()
	{
		for ( int u = 1; u <= m_numBins; u++ )
		{
			for ( int v = 1; v <= m_numBins; v++ )
			{
				m_h[ u ][ v ] = ( m_s[ u ][ v ] * m_s[ u ][ v ] ) / m_p[ u ][ v ];
			}
		}
	}

	@Override
	public ThresholdValueCollection createEmptyOutput( IN in )
	{
		return new ThresholdValueCollection( m_numberOfLevels );
	}

	@Override
	public UnaryOutputOperation< IN, ThresholdValueCollection > copy()
	{
		return new OtsuMultilevelThresholder< T, IN >( m_numberOfLevels, m_numBins );
	}

	@Override
	public ThresholdValueCollection compute( IN in )
	{
		return compute( in, createEmptyOutput( in ) );
	}

}
