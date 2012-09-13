package net.imglib2.ops.operation.iterable.binary.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.iterable.unary.MedianOp;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class MedianLocalThreshold< T extends RealType< T >, IN extends Iterator< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_c;

	private MedianOp< T, DoubleType > m_median;

	private DoubleType m_tmp;

	public MedianLocalThreshold( double c )
	{
		m_c = c;
		m_median = new MedianOp< T, DoubleType >();
		m_tmp = new DoubleType();
	}

	@Override
	public BitType compute( IN input, T px, BitType output )
	{
		output.set( px.getRealDouble() > m_median.compute( input, m_tmp ).getRealDouble() - m_c );
		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new MedianLocalThreshold< T, IN >( m_c );
	}

}
