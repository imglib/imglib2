package net.imglib2.ops.operation.real.binary;

import net.imglib2.converter.Converter;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public final class Convert< I extends RealType< I >, O extends RealType< O >> implements UnaryOperation< I, O >, Converter< I, O >
{

	public enum TypeConversionTypes
	{
		DIRECTCLIP, SCALE, DIRECT, SCALECLIP;
	}

	private final TypeConversionTypes m_mode;

	private double m_inMin;

	private final double m_outMax;

	private double m_outMin;

	private double m_factor;

	private final I m_inType;

	private final O m_outType;

	private final UnaryOperation< I, O > m_op;

	/**
	 * Convert to the new type.
	 * 
	 * @param inType
	 *            The old type.
	 * @param outType
	 *            The new type.
	 */
	public Convert( final I inType, final O outType, TypeConversionTypes mode )
	{
		m_outType = outType;
		m_mode = mode;
		m_op = initOp();

		m_inType = inType;
		m_inMin = inType.getMinValue();
		m_outMax = m_outType.getMaxValue();
		m_outMin = m_outType.getMinValue();

		if ( mode == TypeConversionTypes.SCALE || mode == TypeConversionTypes.DIRECTCLIP )
		{
			m_factor = ( inType.getMaxValue() - m_inMin ) / ( outType.getMaxValue() - m_outMin );
		}
		else
		{
			m_factor = 1.0;
		}
	}

	public void setOutMin( double outMin )
	{
		m_outMin = outMin;
	}

	public void setInMin( double inMin )
	{
		m_inMin = inMin;
	}

	public double getFactor()
	{
		return m_factor;
	}

	public void setFactor( double newFactor )
	{
		m_factor = newFactor;
	}

	private UnaryOperation< I, O > initOp()
	{
		switch ( m_mode )
		{
		case DIRECTCLIP:
			return new UnaryOperation< I, O >()
			{

				private double v;

				@Override
				public O compute( I op, O r )
				{
					v = op.getRealDouble();
					if ( v > m_outMax )
					{
						r.setReal( m_outMax );
					}
					else if ( v < m_outMin )
					{
						r.setReal( m_outMin );
					}
					else
					{
						r.setReal( v );
					}

					return r;
				}

				@Override
				public UnaryOperation< I, O > copy()
				{
					return this;
				}
			};
		case DIRECT:
			return new UnaryOperation< I, O >()
			{

				@Override
				public O compute( I op, O r )
				{
					r.setReal( op.getRealDouble() );
					return r;
				}

				@Override
				public UnaryOperation< I, O > copy()
				{
					return this;
				}
			};
		case SCALE:
			return new UnaryOperation< I, O >()
			{

				@Override
				public O compute( I op, O r )
				{
					r.setReal( ( op.getRealDouble() - m_inMin ) / m_factor + m_outMin );
					return r;
				}

				@Override
				public UnaryOperation< I, O > copy()
				{
					return this;
				}
			};
		case SCALECLIP:
			return new UnaryOperation< I, O >()
			{

				private double v;

				@Override
				public O compute( I op, O r )
				{
					v = ( op.getRealDouble() - m_inMin ) / m_factor + m_outMin;
					if ( v > m_outMax )
					{
						r.setReal( m_outMax );
					}
					else if ( v < m_outMin )
					{
						r.setReal( m_outMin );
					}
					else
					{
						r.setReal( v );
					}
					return r;
				}

				@Override
				public UnaryOperation< I, O > copy()
				{
					return this;
				}
			};
		}
		return null;
	}

	@Override
	public final O compute( final I op, final O r )
	{
		return m_op.compute( op, r );
	}

	@Override
	public UnaryOperation< I, O > copy()
	{
		return new Convert< I, O >( m_inType, m_outType, m_mode );
	}

	@Override
	public void convert( I input, O output )
	{
		m_op.compute( input, output );
	}
}
