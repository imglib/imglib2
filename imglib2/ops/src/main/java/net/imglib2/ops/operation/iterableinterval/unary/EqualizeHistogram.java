package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author hornm, dietzc University of Konstanz
 */
public class EqualizeHistogram< T extends RealType< T >, I extends IterableInterval< T >> implements UnaryOperation< I, I >
{

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public I compute( I in, I r )
	{

		MakeHistogram< T > histogramOp = new MakeHistogram< T >();
		int[] histo = new MakeHistogram< T >().compute( r, histogramOp.createEmptyOutput( in ) ).hist();

		T val = r.firstElement().createVariable();

		int min = ( int ) val.getMaxValue();
		// calc cumulated histogram
		for ( int i = 1; i < histo.length; i++ )
		{
			histo[ i ] = histo[ i ] + histo[ i - 1 ];
			if ( histo[ i ] != 0 )
			{
				min = Math.min( min, histo[ i ] );
			}
		}

		// global possible extrema
		double gmax = val.getMaxValue();
		double gmin = val.getMinValue();

		Cursor< T > c = r.cursor();
		long numPix = r.size();

		while ( c.hasNext() )
		{
			c.fwd();
			val = c.get();
			int p = histo[ ( int ) val.getRealFloat() - ( int ) gmin ];
			double t = ( p - min );
			t /= numPix - min;
			t *= gmax;
			p = ( int ) Math.round( t );
			c.get().setReal( p );
		}
		return r;

	}

	@Override
	public UnaryOperation< I, I > copy()
	{
		return new EqualizeHistogram< T, I >();
	}
}
