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

package net.imglib2.algorithm.componenttree.mser;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.type.Type;

/**
 * A maximally stable extremal region (MSER) of the image thresholded at
 * {@link #value()}. The set of pixels can be accessed by iterating (
 * {@link #iterator()}) the component.
 * 
 * This is a node in a {@link MserTree}. The child and parent nodes can be
 * accessed by {@link #getChildren()} and {@link #getParent()}.
 * 
 * @param <T>
 *            value type of the input image.
 * 
 * @author Tobias Pietzsch
 */
public final class Mser< T extends Type< T > > implements Iterable< Localizable >
{
	/**
	 * child nodes in the {@link MserTree}.
	 */
	final ArrayList< Mser< T > > children;

	/**
	 * parent node in the {@link MserTree}.
	 */
	Mser< T > parent;

	/**
	 * Threshold value of the connected component.
	 */
	private final T value;

	/**
	 * Pixels in the component.
	 */
	private final PixelList pixelList;

	/**
	 * MSER score : |R_i - R_{i-\Delta}| / |R_i|.
	 */
	private final double score;

	/**
	 * Mean of the pixel positions in the region.
	 */
	private final double[] mean;

	/**
	 * Covariance of the pixel positions in the region.
	 */
	private final double[] cov;

	Mser( final MserEvaluationNode< T > node )
	{
		children = new ArrayList< Mser< T > >();
		parent = null;

		value = node.value;
		score = node.score;
		pixelList = node.pixelList;
		mean = node.mean;
		cov = node.cov;
	}

	/**
	 * Get the image threshold that created the extremal region.
	 * 
	 * @return the image threshold that created the extremal region.
	 */
	public T value()
	{
		return value;
	}

	/**
	 * Get the number of pixels in the extremal region.
	 * 
	 * @return number of pixels in the extremal region.
	 */
	public long size()
	{
		return pixelList.size();
	}

	/**
	 * The MSER score is computed as |R_i - R_{i-\Delta}| / |R_i|.
	 * 
	 * @return the MSER score.
	 */
	public double score()
	{
		return score;
	}

	/**
	 * Mean of the pixel positions in the region. This is a position vector (x,
	 * y, z, ...)
	 * 
	 * @return mean vector.
	 */
	public double[] mean()
	{
		return mean;
	}

	/**
	 * Covariance of the pixel positions in the region. This is a vector of the
	 * independent elements of the covariance matrix (xx, xy, xz, ..., yy, yz,
	 * ..., zz, ...)
	 * 
	 * @return vector of covariance elements.
	 */
	public double[] cov()
	{
		return cov;
	}

	/**
	 * Get an iterator over the pixel locations ({@link Localizable}) in this
	 * connected component.
	 * 
	 * @return iterator over locations.
	 */
	@Override
	public Iterator< Localizable > iterator()
	{
		return pixelList.iterator();
	}

	/**
	 * Get the children of this node in the {@link MserTree}.
	 * 
	 * @return the children of this node in the {@link MserTree}.
	 */
	public ArrayList< Mser< T > > getChildren()
	{
		return children;
	}

	/**
	 * Get the parent of this node in the {@link MserTree}.
	 * 
	 * @return the parent of this node in the {@link MserTree}.
	 */
	public Mser< T > getParent()
	{
		return parent;
	}
}
