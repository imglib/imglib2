package net.imglib2.ops.operation.imgplus.unary;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RealInterval;
import net.imglib2.img.ImgPlus;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

/**
 * Crops an image.
 * 
 * @author dietzc, hornm, University of Konstanz
 */
public class ImgPlusCrop< T extends Type< T >> implements UnaryOperation< ImgPlus< T >, ImgPlus< T >>
{

	/**
	 * The interval to be cropped
	 */
	private final Interval m_interval;

	/**
	 * Crops the intervaled defined by origin and extend
	 * 
	 * @param origin
	 *            origin of the interval
	 * 
	 * @param extend
	 *            extend of the interval
	 */
	public ImgPlusCrop( long[] origin, long[] extend )
	{
		long[] max = new long[ extend.length ];
		for ( int i = 0; i < max.length; i++ )
		{
			max[ i ] = origin[ i ] + extend[ i ] - 1;
		}
		m_interval = new FinalInterval( origin, max );
	}

	/**
	 * Crops the intervaled defined by origin and extend
	 * 
	 * @param origin
	 *            origin of the interval
	 * 
	 * @param extend
	 *            extend of the interval
	 */
	public ImgPlusCrop( int[] origin, int[] extend )
	{
		long[] max = new long[ extend.length ];
		long[] loffset = new long[ extend.length ];
		for ( int i = 0; i < max.length; i++ )
		{
			max[ i ] = origin[ i ] + extend[ i ] - 1;
			loffset[ i ] = origin[ i ];
		}
		m_interval = new FinalInterval( loffset, max );
	}

	/**
	 * Crops an interval
	 * 
	 * @param interval
	 */
	public ImgPlusCrop( Interval interval )
	{
		m_interval = interval;
	}

	/**
	 * 
	 * @param interval
	 * @param imgFac
	 */
	public ImgPlusCrop( RealInterval interval )
	{
		long[] min = new long[ interval.numDimensions() ];
		long[] max = new long[ interval.numDimensions() ];
		for ( int i = 0; i < max.length; i++ )
		{
			min[ i ] = ( long ) Math.floor( interval.realMin( i ) );
			max[ i ] = ( long ) Math.ceil( interval.realMax( i ) );
		}
		m_interval = new FinalInterval( min, max );
	}

	@Override
	public ImgPlus< T > compute( ImgPlus< T > op, ImgPlus< T > r )
	{
		Cursor< T > rc = r.localizingCursor();
		RandomAccess< T > opc = op.randomAccess();
		while ( rc.hasNext() )
		{
			rc.next();
			for ( int d = 0; d < m_interval.numDimensions(); d++ )
			{
				opc.setPosition( rc.getLongPosition( d ) + m_interval.min( d ), d );
			}
			rc.get().set( opc.get() );
		}

		return r;
	}

	@Override
	public UnaryOperation< ImgPlus< T >, ImgPlus< T >> copy()
	{
		return new ImgPlusCrop< T >( m_interval );
	}
}
