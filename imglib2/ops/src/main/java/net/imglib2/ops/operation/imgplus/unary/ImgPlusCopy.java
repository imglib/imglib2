package net.imglib2.ops.operation.imgplus.unary;

import net.imglib2.img.ImgPlus;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.img.unary.ImgCopyOperation;
import net.imglib2.type.Type;

/**
 * 
 * @author hornm, dietzc University of Konstanz
 */
public class ImgPlusCopy< T extends Type< T >> implements UnaryOperation< ImgPlus< T >, ImgPlus< T >>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImgPlus< T > compute( ImgPlus< T > op, ImgPlus< T > r )
	{
		r.setName( op.getName() );
		r.setSource( op.getSource() );
		for ( int d = 0; d < op.numDimensions(); d++ )
		{

			r.setAxis( op.axis( d ), d );
			r.setCalibration( op.calibration( d ), d );

		}
		new ImgCopyOperation< T >().compute( op, r );
		return r;
	}

	@Override
	public UnaryOperation< ImgPlus< T >, ImgPlus< T >> copy()
	{
		return new ImgPlusCopy< T >();
	}
}
