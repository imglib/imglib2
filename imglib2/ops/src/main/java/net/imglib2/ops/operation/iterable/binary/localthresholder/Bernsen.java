package net.imglib2.ops.operation.iterable.binary.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class Bernsen< T extends RealType< T >, IN extends Iterator< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_contrastThreshold;

	private double m_maxHalfValue;

	public Bernsen( double contrastThreshold, double maxValue )
	{
		m_contrastThreshold = contrastThreshold;
		m_maxHalfValue = maxValue;
	}

	@Override
	public BitType compute( IN input, T px, BitType output )
	{

		double min = Double.MAX_VALUE;;
		double max = -Double.MAX_VALUE;

		while ( input.hasNext() )
		{
			double next = input.next().getRealDouble();
			min = Math.min( next, min );
			max = Math.max( next, max );
		}

		double localContrast = max - min;
		double midGray = ( max + min ) / 2;

		if ( localContrast < m_contrastThreshold )
		{
			output.set( midGray >= m_maxHalfValue );
		}
		else
		{
			output.set( px.getRealDouble() >= midGray );
		}

		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new Bernsen< T, IN >( m_contrastThreshold, m_maxHalfValue );
	}

}
