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

package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.real.binary.Normalize;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;

/**
 * 
 * @author dietzc, hornm, schoenenbergerf University of Konstanz
 * @param <T>
 *            The type of the {@link Img} which will be normalized. Must be
 *            {@link RealType}
 */
public class NormalizeIterableInterval< T extends RealType< T >, I extends IterableInterval< T >> implements UnaryOperation< I, I >
{

	private double m_saturation;

	private boolean m_manuallyMinMax;

	private Pair< T, T > m_minMax;

	private MinMax< T > m_minMaxOp;

	private Normalize< T > m_normalize;

	/**
	 * Normalizes an image.
	 */
	public NormalizeIterableInterval()
	{
		this( 0 );
	}

	/**
	 * @param saturation
	 *            the percentage of pixels in the lower and upper domain to be
	 *            ignored in the normalization
	 */
	public NormalizeIterableInterval( double saturation )
	{
		m_saturation = saturation;
		m_manuallyMinMax = false;
	}

	/**
	 * @param min
	 * @param max
	 */
	public NormalizeIterableInterval( T min, T max )
	{

		m_minMax = new Pair< T, T >( min, max );
		m_manuallyMinMax = true;
	}

	/**
	 * 
	 * @param saturation
	 *            the percentage of pixels in the lower and upper domain to be
	 * @param numBins
	 *            number of bins in the histogram to determine the saturation,
	 *            if T is not {@link IntegerType}, 256 bins are chosen
	 *            automatically
	 */
	public NormalizeIterableInterval( ImgFactory< T > fac, double saturation )
	{
		this( saturation );
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public I compute( I in, I res )
	{
		Pair< T, T > minmax;
		if ( !m_manuallyMinMax )
		{
			if ( m_minMaxOp == null )
			{
				m_minMaxOp = new MinMax< T >( m_saturation, in.firstElement().createVariable() );
			}
			minmax = m_minMaxOp.compute( in );
			m_normalize = new Normalize< T >( minmax.a, minmax.b );
		}
		else
		{
			minmax = m_minMax;
			if ( m_normalize == null )
				m_normalize = new Normalize< T >( minmax.a, minmax.b );
		}

		// actually compute everything
		UnaryOperationAssignment< T, T > imgNormalize = new UnaryOperationAssignment< T, T >( m_normalize );
		imgNormalize.compute( in, res );

		return res;
	}

	/**
	 * Determines the minimum and factory for scaling according to the given
	 * saturation
	 * 
	 * @param <T>
	 * @param interval
	 * @param saturation
	 *            the percentage of pixels in the lower and upper domain to be
	 *            ignored in the normalization
	 * @return with the normalization factor at position 0, minimum of the image
	 *         at position 1
	 */
	public double[] getNormalizationProperties( I interval, double saturation )
	{

		T type = interval.firstElement().createVariable();
		MinMax< T > minMax = new MinMax< T >( saturation, type );

		Pair< T, T > pair = minMax.compute( interval );
		return new double[] { 1 / ( pair.b.getRealDouble() - pair.a.getRealDouble() ) * ( type.getMaxValue() - type.getMinValue() ), pair.a.getRealDouble() };
	}

	@Override
	public UnaryOperation< I, I > copy()
	{
		return new NormalizeIterableInterval< T, I >( m_saturation );
	}

}
