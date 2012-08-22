package net.imglib2.ops.operation.iterableinterval.unary.multilevelthresholder;

import java.util.Arrays;

/**
 * friedrichm (University of Konstanz)
 */
public class ThresholdValueCollection
{

	public ThresholdValueCollection( int numberOfLevels )
	{
		m_thresholdValues = new double[ numberOfLevels - 1 ];
		m_numberOfLevels = numberOfLevels;
	}

	public double get( int i )
	{
		return m_thresholdValues[ i ];
	}

	public void set( int i, double value )
	{
		m_thresholdValues[ i ] = value;
	}

	public int getNumberOfLevels()
	{
		return m_numberOfLevels;
	}

	public double[] getSortedVector()
	{
		double[] sortedCopy = m_thresholdValues.clone();
		Arrays.sort( sortedCopy );
		return sortedCopy;
	}

	public void scale( int numBins, double min, double max )
	{
		double factor = ( Math.abs( max - min ) / numBins );
		for ( int i = 0; i < m_thresholdValues.length; i++ )
		{
			m_thresholdValues[ i ] = m_thresholdValues[ i ] * factor;
		}
	}

	private double[] m_thresholdValues;

	private int m_numberOfLevels;
}
