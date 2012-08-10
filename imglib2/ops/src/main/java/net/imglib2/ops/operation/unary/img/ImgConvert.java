package net.imglib2.ops.operation.unary.img;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.ops.UnaryOutputOperation;
import net.imglib2.ops.image.UnaryOperationAssignment;
import net.imglib2.ops.operation.binary.real.Convert;
import net.imglib2.ops.operation.binary.real.Convert.TypeConversionTypes;
import net.imglib2.ops.operation.unary.iterableinterval.NormalizeIterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Converts complete images from one type into another
 * 
 * @author hornm, dietzc, University of Konstanz
 */
public class ImgConvert< I extends RealType< I >, O extends RealType< O >> implements UnaryOutputOperation< Img< I >, Img< O >>
{

	private final O m_outType;

	private final I m_inType;

	private final ImgConversionTypes m_conversionType;

	/**
	 * Convert to the new type. Scale values with respect to the old type range.
	 * 
	 * @param outType
	 *            The new type.
	 * @param inType
	 *            The old type.
	 * @param imgFac
	 *            the image factory to produce the image
	 */
	public ImgConvert( final I inType, final O outType, ImgConversionTypes type )
	{
		m_outType = outType;
		m_conversionType = type;
		m_inType = inType;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< O > createEmptyOutput( Img< I > op )
	{
		try
		{
			long[] dims = new long[ op.numDimensions() ];
			op.dimensions( dims );
			return op.factory().imgFactory( m_outType ).create( dims, m_outType.createVariable() );
		}
		catch ( IncompatibleTypeException e )
		{
			throw new RuntimeException( e );
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Img< O > compute( Img< I > img, Img< O > r )
	{

		double[] normPar;
		Convert< I, O > convertOp = null;
		switch ( m_conversionType )
		{
		case DIRECT:
			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.DIRECT );
			break;
		case DIRECTCLIP:
			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.DIRECTCLIP );
			break;
		case NORMALIZEDIRECT:
			normPar = new NormalizeIterableInterval< I, Img< I >>().getNormalizationProperties( img, 0 );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );

			convertOp.setFactor( convertOp.getFactor() / normPar[ 0 ] );
			convertOp.setInMin( 0 );
			convertOp.setOutMin( 0 );
			break;
		case NORMALIZESCALE:
			normPar = new NormalizeIterableInterval< I, Img< I >>().getNormalizationProperties( img, 0 );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );
			convertOp.setFactor( convertOp.getFactor() / normPar[ 0 ] );
			convertOp.setInMin( normPar[ 1 ] );
			break;
		case NORMALIZEDIRECTCLIP:
			normPar = new NormalizeIterableInterval< I, Img< I >>().getNormalizationProperties( img, 0 );
			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALECLIP );
			convertOp.setFactor( convertOp.getFactor() / normPar[ 0 ] );
			convertOp.setInMin( normPar[ 1 ] );
			break;
		case SCALE:
			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );
			break;

		default:
			throw new IllegalArgumentException( "Normalization type unknown" );
		}

		UnaryOperationAssignment< I, O > map = new UnaryOperationAssignment< I, O >( convertOp );
		map.compute( Views.flatIterable( img ), Views.flatIterable( r ) );
		return r;
	}

	@Override
	public UnaryOutputOperation< Img< I >, Img< O >> copy()
	{
		return new ImgConvert< I, O >( m_inType.copy(), m_outType.copy(), m_conversionType );
	}

	@Override
	public Img< O > compute( Img< I > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}
}
