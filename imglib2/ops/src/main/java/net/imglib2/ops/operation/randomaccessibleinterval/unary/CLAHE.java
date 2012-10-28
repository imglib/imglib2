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
package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * CLAHE - contrast limited adaptive histogram equalization.
 * 
 * *** CONVERT THE INPUT IMAGE TO TYPE UNSIGNEDBYTE ***
 * 
 * 
 * @author Lukas Kircher, University Of Konstanz. Seminar Biological Image
 *         Processing, summer term 2011.
 * 
 */
public class CLAHE< T extends RealType< T >, K extends RandomAccessibleInterval< T > & IterableInterval< T >> implements UnaryOperation< K, K >
{

	// ALGORITHM SETUP
	/** Number of contextual regions in X direction. */
	private int m_ctxNrX = 8;

	/** Number of contextual regions in Y direction. */
	private int m_ctxNrY = 8;

	/** Number of histogram bins. */
	private final int histNrBins = 256;

	/** Relative histogram clip limit. */
	// try 1.01f - won't work: check upperBin in clipHistogram
	private final float clipLimit = 3.0f;

	/**
	 * If enabled, CLAHE is applied (cumulative histogram slope is limited). If
	 * disabled only adaptive histogram equalization is applied.
	 */
	private boolean m_enableClipping = true;

	// IMAGE PROPERTIES
	/** Image random access. */
	private RandomAccess< T > m_srcRa;

	private RandomAccess< T > m_resRa;

	/** Image width. */
	private int imgWidth;

	/** Image height. */
	private int imgHeight;

	/** Minimum gray value of input image (8bit image expected) */
	private int m_imgGrayMin = 0;

	/** Maximum gray value of input image (8bit image expected) */
	private int m_imgGrayMax = 255;

	/** Number of distinct gray values of the input image. */
	private int m_imgNrGrayVals;

	/** Number of pixels per contextual region in X direction. */
	private int ctxXSize;

	/** Number of pixels per contextual region in Y direction. */
	private int ctxYSize;

	/** Number of pixels per contextual region. */
	private int ctxNrPixels;

	/** Absolute histogram clip limit. */
	private int absClipLimit;

	/* scale factor for the image values */
	private double m_scale;

	/** Histograms for all contextual regions. */
	private int[] histograms;

	/**
	 * @param ctxNrX
	 *            Number of contextual regions in X direction.
	 * @param ctxNrY
	 *            Number of contextual regions in Y direction.
	 * @param enableClipping
	 *            If enabled, CLAHE is applied (cumulative histogram slope is
	 *            limited). If disabled only adaptive histogram equalization is
	 *            applied.
	 */
	public CLAHE( int ctxNrX, int ctxNrY, boolean enableClipping )
	{
		m_ctxNrX = ctxNrX;
		m_ctxNrY = ctxNrY;
		m_enableClipping = enableClipping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public K compute( K in, K r )
	{

		T type = r.firstElement().createVariable();
		m_imgGrayMin = ( int ) Math.round( type.getMinValue() );
		m_imgGrayMax = ( int ) Math.round( type.getMaxValue() );
		m_imgNrGrayVals = m_imgGrayMax - m_imgGrayMin + 1;

		m_scale = ( histNrBins - 1 ) / ( m_imgGrayMax - m_imgGrayMin );
		if ( r.numDimensions() > 2 ) { throw new IllegalArgumentException( "CLAHE only supports 2-dim images." ); }

		type.setReal( m_imgGrayMin );
		// m_ra = Views.extendValue(r, type).randomAccess();
		m_srcRa = in.randomAccess();
		m_resRa = r.randomAccess();

		imgWidth = ( int ) r.dimension( 0 );
		imgHeight = ( int ) r.dimension( 1 );
		ctxXSize = imgWidth / m_ctxNrX;
		ctxYSize = imgHeight / m_ctxNrY;
		ctxNrPixels = ctxXSize * ctxYSize;
		absClipLimit = ( int ) ( clipLimit * ( ( ctxXSize * ctxYSize ) / histNrBins ) );

		// value checks
		if ( m_ctxNrX < 2 || m_ctxNrY < 2 )
			throw new IllegalArgumentException( "A minimum of two context regions " + "per dimension is required." );
		if ( m_imgNrGrayVals < histNrBins )
			throw new IllegalArgumentException( "Number of input gray values must be greater than " + "number of histogram bins." );
		if ( ctxXSize * ctxYSize / histNrBins < 1 )
			throw new IllegalArgumentException( "Number of histogram bins is larger than number of pixels " + "in a contextual region." );
		if ( absClipLimit * histNrBins < ctxNrPixels )
			throw new IllegalArgumentException( "Relative clip limit too low." );

		histograms = createHistograms( r );

		// runners
		int xi = 0;
		int yi = 0;
		// current histogram position
		int histArrayOffset = 0;
		for ( yi = 0; yi < m_ctxNrY; yi++ )
		{
			// set img offset to the first contextual region of the
			// current line
			for ( xi = 0; xi < m_ctxNrX; xi++ )
			{
				if ( m_enableClipping )
					clipHistogram( histArrayOffset );
				createCumulativeHistogram( histArrayOffset );
				histArrayOffset += histNrBins;
			}
		}
		interpolate();

		return r;

	}

	/**
	 * Updates the image and applies a bi-linear interpolation scheme to create
	 * smooth transitions between context areas.
	 * 
	 * @throws InvalidCLAHEoperation
	 */
	private void interpolate()
	{
		int yUp = 0;
		int yBo = 0;
		int xLeft = 0;
		int xRight = 0;
		int subXSize = 0;
		int subYSize = 0;
		int imgPos = 0;

		for ( int iy = 0; iy <= m_ctxNrY; iy++ )
		{
			// top row
			if ( iy == 0 )
			{
				subYSize = ctxYSize >> 1;
				yUp = 0;
				yBo = 0;
			}
			else
			{
				// bottom row
				if ( iy == m_ctxNrY )
				{
					subYSize = ctxYSize >> 1;
					yUp = m_ctxNrY - 1;
					yBo = yUp;
				}
				else
				{
					// default
					subYSize = ctxYSize;
					yUp = iy - 1;
					yBo = yUp + 1;
				}
			}

			for ( int ix = 0; ix <= m_ctxNrX; ix++ )
			{
				// left column
				if ( ix == 0 )
				{
					subXSize = ctxXSize >> 1;
					xLeft = 0;
					xRight = 0;
				}
				else
				{
					// right column
					if ( ix == m_ctxNrX )
					{
						subXSize = ctxXSize >> 1;
						xLeft = m_ctxNrX - 1;
						xRight = xLeft;
					}
					else
					{
						// default
						subXSize = ctxXSize;
						xLeft = ix - 1;
						xRight = xLeft + 1;
					}
				}
				// start of subimages for interpolation
				int leftUpper = histNrBins * ( yUp * m_ctxNrX + xLeft );
				int rightUpper = histNrBins * ( yUp * m_ctxNrX + xRight );
				int leftBottom = histNrBins * ( yBo * m_ctxNrX + xLeft );
				int rightBottom = histNrBins * ( yBo * m_ctxNrX + xRight );
				interpolate4regions( leftUpper, rightUpper, leftBottom, rightBottom, subXSize, subYSize, imgPos );
				// forward image pointer
				imgPos += subXSize;
			}

			// forward image pointer to next line
			imgPos += ( subYSize - 1 ) * imgWidth;
		}
	}

	private void interpolate4regions( final int leftUpper, final int rightUpper, final int leftBottom, final int rightBottom, final int subXSize, final int subYSize, int p )
	{
		int incr = imgWidth - subXSize;
		int num = subXSize * subYSize;

		int invCoefY = subYSize;
		for ( int coefY = 0; coefY < subYSize; coefY++ )
		{
			int invCoefX = subXSize;
			m_srcRa.setPosition( p, 0 );

			for ( int coefX = 0; coefX < subXSize; coefX++ )
			{
				m_srcRa.setPosition( p, 0 );
				// testing
				// if (c.hasNext()) {
				// c.fwd(X);
				// c.bck(X);
				// }
				int gray = valueToBin( m_srcRa.get().getRealFloat() );

				int newValue = ( ( invCoefY * ( invCoefX * histograms[ leftUpper + gray ] + coefX * histograms[ rightUpper + gray ] ) + coefY * ( invCoefX * histograms[ leftBottom + gray ] + coefX * histograms[ rightBottom + gray ] ) ) / num );

				m_resRa.setPosition( m_srcRa );
				m_resRa.get().setReal( newValue );
				p++;
				invCoefX--;
			}

			// fwd image pointer
			invCoefY--;
			p += incr;
		}
	}

	/**
	 * Creates the histograms for all contextual regions. The image is scanned
	 * line by line as this (hopefully) speeds up the underlying image cursor.
	 * 
	 * @throws InvalidCLAHEoperation
	 */
	private int[] createHistograms( K img )
	{
		RandomAccess< T > ra = Views.extendPeriodic( img ).randomAccess();
		int[] histograms = new int[ m_ctxNrX * m_ctxNrY * histNrBins ];
		for ( int ctxY = 0; ctxY < m_ctxNrY; ctxY++ )
		{
			for ( int y = 0; y < ctxYSize; y++ )
			{
				for ( int ctxX = 0; ctxX < m_ctxNrX; ctxX++ )
				{
					for ( int x = 0; x < ctxXSize; x++ )
					{
						ra.fwd( 0 );
						final int bin = valueToBin( ra.get().getRealDouble() );
						final int offSet = ctxY * m_ctxNrX * histNrBins + ctxX * histNrBins;
						final int index = offSet + bin;
						histograms[ index ]++;
					}
				}
				ra.fwd( 1 );
			}

		}

		return histograms;

	}

	/**
	 * Creates the cumulative histogram from the image histogram.
	 * 
	 * @param histArrayOffset
	 *            index of first bin
	 */
	private void createCumulativeHistogram( final int histArrayOffset )
	{
		final float fScale = ( ( float ) m_imgGrayMax - m_imgGrayMin ) / ctxNrPixels;
		int sum = 0;

		for ( int i = histArrayOffset; i < histArrayOffset + histNrBins; i++ )
		{
			sum += histograms[ i ];
			histograms[ i ] = ( int ) ( m_imgGrayMin + sum * fScale );
			if ( histograms[ i ] > m_imgGrayMax )
				histograms[ i ] = m_imgGrayMax;
		}
	}

	/**
	 * Clips and redistributes the bin pixels of the cumulative histogram to
	 * limit cumulative histogram slope, hence the local contrast.
	 * 
	 * @param histArrayOffset
	 *            index of first bin
	 */
	private void clipHistogram( final int histArrayOffset )
	{
		// calculate number of excess pixels
		int totalExcessPxs = 0;
		for ( int i = histArrayOffset; i < histArrayOffset + histNrBins; i++ )
		{
			int binExcessPxs = histograms[ i ] - absClipLimit;
			if ( binExcessPxs > 0 )
				totalExcessPxs += binExcessPxs;
		}

		// clip histogram and recalculate excess pixels in each bin
		int avgBinIncrement = totalExcessPxs / histNrBins;
		// upper bin capacity
		int upperBinCapacity = absClipLimit - avgBinIncrement;
		for ( int i = histArrayOffset; i < histArrayOffset + histNrBins; i++ )
		{
			if ( histograms[ i ] > absClipLimit )
				histograms[ i ] = absClipLimit;
			else
			{
				if ( histograms[ i ] > upperBinCapacity )
				{
					totalExcessPxs -= histograms[ i ] - upperBinCapacity;
					histograms[ i ] = absClipLimit;
				}
				else
				{
					totalExcessPxs -= avgBinIncrement;
					histograms[ i ] += avgBinIncrement;
				}
			}
		}

		// redistribute remaining excess pixels
		final int histLimit = histArrayOffset + histNrBins;
		while ( totalExcessPxs > 0 )
		{
			int currHistPos = histArrayOffset;
			while ( totalExcessPxs > 0 && currHistPos < histLimit )
			{
				int stepSize = histNrBins / totalExcessPxs;
				if ( stepSize < 1 )
					stepSize = 1;
				for ( int currBin = currHistPos; currBin < histLimit && totalExcessPxs > 0; currBin += stepSize )
				{
					if ( histograms[ currBin ] < absClipLimit )
					{
						histograms[ currBin ]++;
						totalExcessPxs--;
					}
				}
				currHistPos++;
			}
		}
	}

	private final int valueToBin( final double v )
	{
		return ( int ) ( ( v - m_imgGrayMin ) * m_scale );
	}

	@Override
	public UnaryOperation< K, K > copy()
	{
		return new CLAHE< T, K >( m_ctxNrX, m_ctxNrY, m_enableClipping );
	}

}
