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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.regiongrowing;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * First attempt to voronoi-like local region growing. NOT FINISHED YET!
 * 
 * @author Martin Horn (University of Konstanz)
 */
public class VoronoiLikeLocalRegionGrowing< L extends Comparable< L >, T extends Type< T > & Comparable< T >> extends VoronoiLikeRegionGrowing< L, T >
{

	private RandomAccessibleInterval< T > m_srcImg;

	private RandomAccess< T > m_srcImgRA;

	/**
	 * @param m_srcImg
	 * @param threshold
	 *            stops growing if the pixel value falls below that value
	 * @param fillHoles
	 *            fills the wholes in a post-processing step within segments of
	 *            the same label
	 */
	public VoronoiLikeLocalRegionGrowing( RandomAccessibleInterval< T > srcImg, boolean fillHoles )
	{
		super( srcImg, Views.iterable( srcImg ).firstElement().createVariable(), fillHoles );
		m_srcImg = srcImg;
		m_srcImgRA = m_srcImg.randomAccess();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initRegionGrowing( RandomAccessibleInterval< LabelingType< L > > in )
	{
		if(!(in instanceof Labeling)){
			throw new IllegalArgumentException("Since now only Labelings are supported");
		}
		
		Labeling<L> srcLab = (Labeling<L>) in;
		
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
	public UnaryOperation< RandomAccessibleInterval< LabelingType< L > >, Labeling< L >> copy()
	{
		return new VoronoiLikeLocalRegionGrowing< L, T >( m_srcImg, m_fillHoles );

	}
}
