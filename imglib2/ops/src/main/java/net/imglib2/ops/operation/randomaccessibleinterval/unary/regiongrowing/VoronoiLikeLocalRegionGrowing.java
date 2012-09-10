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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.regiongrowing;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.labeling.Labeling;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;

/**
 * First attempt to voronoi-like local region growing. NOT FINISHED YET!
 * 
 * @author hornm, University of Konstanz
 */
public class VoronoiLikeLocalRegionGrowing< L extends Comparable< L >, T extends Type< T > & Comparable< T >> extends VoronoiLikeRegionGrowing< L, T >
{

	private Img< T > m_srcImg;

	private RandomAccess< T > m_srcImgRA;

	/**
	 * @param m_srcImg
	 * @param threshold
	 *            stops growing if the pixel value falls below that value
	 * @param fillHoles
	 *            fills the wholes in a post-processing step within segments of
	 *            the same label
	 */
	public VoronoiLikeLocalRegionGrowing( Img< T > srcImg, boolean fillHoles )
	{
		super( srcImg, srcImg.firstElement().createVariable(), fillHoles );
		m_srcImg = srcImg;
		m_srcImgRA = m_srcImg.randomAccess();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initRegionGrowing( Labeling< L > srcLab )
	{
		super.initRegionGrowing( srcLab );

		// determine the local threshold for each region by means of the
		// seeding
		// regions
		Collection< L > labels = srcLab.getLabels();
		// Map<L, T> thresholdMap = new HashMap<L, T>();
		for ( L label : labels )
		{
			Cursor< T > cur = srcLab.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( m_srcImg ).localizingCursor();
			while ( cur.hasNext() )
			{
				cur.fwd();
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, L label )
	{
		m_srcImgRA.setPosition( nextPos );
		return false;
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new VoronoiLikeLocalRegionGrowing< L, T >( m_srcImg, m_fillHoles );

	}
}
