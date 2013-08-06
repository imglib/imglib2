package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.histogram.Histogram1d;
import net.imglib2.histogram.Real1dBinMapper;
import net.imglib2.ops.img.UnaryObjectFactory;
import net.imglib2.ops.operation.Operations;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;

/**
 * 
 * @author dietzc, zinsmaierm, hornm (University of Konstanz)
 * 
 * @param <T>
 */
public final class MakeHistogram< T extends RealType< T >> implements UnaryOutputOperation< Iterable< T >, Histogram1d< T > >
{

	int m_numBins = 0;

	private final boolean m_calculateMinMax;

	public MakeHistogram()
	{
		this( -1, false );
	}

	public MakeHistogram( int numBins )
	{
		this( numBins, false );
	}

	/**
	 * @param calculateMinMax
	 *            Calculates the real min and max values of the image and uses
	 *            them for creation of the histogram
	 */
	public MakeHistogram( boolean calculateMinMax )
	{
		this( -1, calculateMinMax );
	}

	/**
	 * 
	 * @param numBins
	 * @param calculateMinMax
	 *            Calculates the real min and max values of the image and uses
	 *            them for creation of the histogram
	 */
	public MakeHistogram( int numBins, boolean calculateMinMax )
	{
		m_numBins = numBins;
		m_calculateMinMax = calculateMinMax;
	}

	@Override
	public final UnaryObjectFactory< Iterable< T >, Histogram1d< T > > bufferFactory()
	{
		return new UnaryObjectFactory< Iterable< T >, Histogram1d< T > >()
		{
			@Override
			public Histogram1d< T > instantiate( Iterable< T > op )
			{
				T type = op.iterator().next().createVariable();

				double min = type.getMinValue();
				double max = type.getMaxValue();

				if ( m_calculateMinMax )
				{
					ValuePair< T, T > minMaxPair = Operations.compute( new MinMax< T >(), op );
					min = minMaxPair.getA().getRealDouble();
					max = minMaxPair.getB().getRealDouble();
				}

				if ( m_numBins <= 0 )
				{
					return new Histogram1d< T >( new Real1dBinMapper< T >( min, max, 256, false ) );
				}
				else
				{
					return new Histogram1d< T >( new Real1dBinMapper< T >( min, max, m_numBins, false ) );
				}
			}
		};
	}

	@Override
	public final Histogram1d< T > compute( Iterable< T > op, Histogram1d< T > r )
	{
		r.resetCounters();
		r.addData( op );

		return r;
	}

	@Override
	public UnaryOutputOperation< Iterable< T >, Histogram1d< T > > copy()
	{
		return new MakeHistogram< T >( m_numBins );
	}
}
