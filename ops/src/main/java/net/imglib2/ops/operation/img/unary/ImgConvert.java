/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
 * #L%
 */

package net.imglib2.ops.operation.img.unary;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.iterableinterval.unary.MinMax;
import net.imglib2.ops.operation.real.unary.Convert;
import net.imglib2.ops.operation.real.unary.Convert.TypeConversionTypes;
import net.imglib2.ops.operation.real.unary.Normalize;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

/**
 * Converts complete images from one type into another
 * TODO: Can now convert RandomAccessibleIntervals from one type into another.
 * 
 * @author hornm, dietzc, University of Konstanz
 */
public class ImgConvert< I extends RealType< I >, O extends RealType< O > & NativeType< O >> implements UnaryOutputOperation< RandomAccessibleInterval< I >, RandomAccessibleInterval< O >>
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
	
	private ImgFactory<O> m_outFactory; //TODO: Make final, when deprecated API is removed!

	/**
	 * Convert to the new type. Scale values with respect to the old type range.
	 * 
	 * @param outType
	 *            The new type.
	 * @param inType
	 *            The old type.
	 * @param type
	 * 			  The {@link ImgConversionTypes}, type of conversion.
	 * @param imgFac
	 *            the image factory to produce the image
	 */
	public ImgConvert( final I inType, final O outType, ImgConversionTypes type, ImgFactory<O> imgFac)
	{
		m_outType = outType;
		m_conversionType = type;
		m_inType = inType;
		m_outFactory = imgFac;
	}
	
	/**
	 * For Compatability with previous API, this creates a default ImgFactory.
	 * 
	 * @param inType
	 * @param outType
	 * @param type
	 * @deprecated Use the other constructor and specify a ImgFactory yourself.
	 */
	@Deprecated
	public ImgConvert( final I inType, final O outType, ImgConversionTypes type)
	{
		m_outType = outType;
		m_conversionType = type;
		m_inType = inType;
		m_outFactory = new ListImgFactory<O>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RandomAccessibleInterval< O > compute( RandomAccessibleInterval< I > img, RandomAccessibleInterval< O > r )
	{
		Iterable<I> iterImg = Views.iterable( img );
		double factor;
		ValuePair< I, I > oldMinMax;
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
			oldMinMax = new MinMax< I >().compute( iterImg );
			factor = Normalize.normalizationFactor( oldMinMax.a.getRealDouble(), oldMinMax.b.getRealDouble(), m_inType.getMinValue(), m_inType.getMaxValue() );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );

			convertOp.setFactor( convertOp.getFactor() / factor );
			convertOp.setInMin( 0 );
			convertOp.setOutMin( 0 );
			break;
		case NORMALIZESCALE:
			oldMinMax = new MinMax< I >().compute( iterImg );
			factor = Normalize.normalizationFactor( oldMinMax.a.getRealDouble(), oldMinMax.b.getRealDouble(), m_inType.getMinValue(), m_inType.getMaxValue() );

			convertOp = new Convert< I, O >( m_inType, m_outType, TypeConversionTypes.SCALE );
			convertOp.setFactor( convertOp.getFactor() / factor );
			convertOp.setInMin( oldMinMax.a.getRealDouble() );
			break;
		case NORMALIZEDIRECTCLIP:
			oldMinMax = new MinMax< I >().compute( iterImg );
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
	
	/**
	 * @deprecated This is for compatability with old API only.
	 */
	@Deprecated
	public RandomAccessibleInterval< O > compute( Img< I > img, Img< O > r )
	{
		m_outFactory = r.factory();
		return compute(img, r);
	}

	@Override
	public UnaryOutputOperation< RandomAccessibleInterval< I >, RandomAccessibleInterval< O >> copy()
	{
		return new ImgConvert< I, O >( m_inType.copy(), m_outType.copy(), m_conversionType, m_outFactory );
	}

	@Override
	public Img< O > createEmptyOutput( RandomAccessibleInterval< I > in )
	{
			return m_outFactory.create( in, m_outType );
	}

	@Override
	public RandomAccessibleInterval< O > compute( RandomAccessibleInterval< I > in )
	{
		return compute( in, createEmptyOutput( in ) );
	}
}
