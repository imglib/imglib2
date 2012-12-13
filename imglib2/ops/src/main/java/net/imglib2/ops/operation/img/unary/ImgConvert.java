/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.img.unary;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.iterableinterval.unary.MinMax;
import net.imglib2.ops.operation.real.unary.Convert;
import net.imglib2.ops.operation.real.unary.Convert.TypeConversionTypes;
import net.imglib2.ops.operation.real.unary.Normalize;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;

/**
 * Converts complete images from one type into another
 * 
 * @author hornm, dietzc, University of Konstanz
 */
public class ImgConvert< I extends RealType< I >, O extends RealType< O > & NativeType< O >> implements UnaryOutputOperation< Img< I >, Img< O >>
{

	public enum ImgConversionTypes
	{
		DIRECT( "Copy" ), DIRECTCLIP( "Clip" ), SCALE( "Scale" ), NORMALIZESCALE( "Normalize and scale" ), NORMALIZEDIRECT( "Normalize" ), NORMALIZEDIRECTCLIP( "Normalize (clipped)" );

		private final String m_label;

		public static String[] labelsAsStringArray()
		{
			ImgConversionTypes[] types = ImgConversionTypes.values();
			String[] res = new String[ types.length ];
			for ( int i = 0; i < res.length; i++ )
			{
				res[ i ] = types[ i ].getLabel();
			}

			return res;

		}

		/**
		 * @param label
		 * @return the conversion type for the label, null, if doesn't match any
		 */
		public static ImgConversionTypes getByLabel( String label )
		{
			for ( ImgConversionTypes t : ImgConversionTypes.values() )
			{
				if ( t.getLabel().equals( label ) ) { return t; }
			}
			return null;
		}

		private ImgConversionTypes( String label )
		{
			m_label = label;
		}

		public String getLabel()
		{
			return m_label;
		}

	}

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
	public Img< O > compute( Img< I > img, Img< O > r )
	{

		double factor;
		Pair< I, I > oldMinMax;
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
			oldMinMax = new MinMax< I >( 0, img.firstElement() ).compute( img );
			factor = Normalize.normalizationFactor( oldMinMax.a.getRealDouble(), oldMinMax.b.getRealDouble(), m_inType.getMinValue(), m_inType.getMaxValue() );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );

			convertOp.setFactor( convertOp.getFactor() / factor );
			convertOp.setInMin( 0 );
			convertOp.setOutMin( 0 );
			break;
		case NORMALIZESCALE:
			oldMinMax = new MinMax< I >( 0, img.firstElement() ).compute( img );
			factor = Normalize.normalizationFactor( oldMinMax.a.getRealDouble(), oldMinMax.b.getRealDouble(), m_inType.getMinValue(), m_inType.getMaxValue() );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );
			convertOp.setFactor( convertOp.getFactor() / factor );
			convertOp.setInMin( oldMinMax.a.getRealDouble() );
			break;
		case NORMALIZEDIRECTCLIP:
			oldMinMax = new MinMax< I >( 0, img.firstElement() ).compute( img );
			factor = Normalize.normalizationFactor( oldMinMax.a.getRealDouble(), oldMinMax.b.getRealDouble(), m_inType.getMinValue(), m_inType.getMaxValue() );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALECLIP );
			convertOp.setFactor( convertOp.getFactor() / factor );
			convertOp.setInMin( oldMinMax.a.getRealDouble() );
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
	public Img< O > createEmptyOutput( Img< I > in )
	{
		try
		{
			return in.factory().imgFactory( m_outType ).create( in, m_outType );
		}
		catch ( IncompatibleTypeException e )
		{
			return new ArrayImgFactory< O >().create( in, m_outType );
		}
	}

	@Override
	public Img< O > compute( Img< I > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}
}
