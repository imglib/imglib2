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

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.type.Type;

/**
 * Implementation of {@link Component} that stores a list of associated pixels
 * in a {@link PixelList}, maintains running sums over pixel positions to
 * compute mean and covariance. It keeps track of which components were merged
 * into this since it was last emitted (this is used to establish region size
 * history).
 * 
 * @param <T>
 *            value type of the input image.
 * 
 * @author Tobias Pietzsch
 */
final class MserComponentIntermediate< T extends Type< T > > implements Component< T >
{
	/**
	 * Threshold value of the connected component.
	 */
	private final T value;

	/**
	 * Pixels in the component.
	 */
	final PixelList pixelList;

	/**
	 * number of dimensions in the image.
	 */
	final int n;

	/**
	 * sum of pixel positions (x, y, z, ...).
	 */
	final double[] sumPos;

	/**
	 * sum of independent elements of outer product of position (xx, xy, xz,
	 * ..., yy, yz, ..., zz, ...).
	 */
	final double[] sumSquPos;

	private final long[] tmp;

	/**
	 * A list of {@link MserComponentIntermediate} merged into this one since it
	 * was last emitted. (For building up MSER evaluation structure.)
	 */
	ArrayList< MserComponentIntermediate< T > > children;

	/**
	 * The {@link MserEvaluationNode} assigned to this MserComponent when it was
	 * last emitted. (For building up MSER evaluation structure.)
	 */
	MserEvaluationNode< T > evaluationNode;

	/**
	 * Create new empty component.
	 * 
	 * @param value
	 *            (initial) threshold value {@see #getValue()}.
	 * @param generator
	 *            the {@link MserComponentGenerator#linkedList} is used to store
	 *            the {@link #pixelList}.
	 */
	MserComponentIntermediate( final T value, final MserComponentGenerator< T > generator )
	{
		pixelList = new PixelList( generator.linkedList.randomAccess(), generator.dimensions );
		n = generator.dimensions.length;
		sumPos = new double[ n ];
		sumSquPos = new double[ ( n * ( n + 1 ) ) / 2 ];
		this.value = value.copy();
		this.children = new ArrayList< MserComponentIntermediate< T > >();
		this.evaluationNode = null;
		tmp = new long[ n ];
	}

	@Override
	public void addPosition( final Localizable position )
	{
		pixelList.addPosition( position );
		position.localize( tmp );
		int k = 0;
		for ( int i = 0; i < n; ++i )
		{
			sumPos[ i ] += tmp[ i ];
			for ( int j = i; j < n; ++j )
				sumSquPos[ k++ ] += tmp[ i ] * tmp[ j ];
		}
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}

	@Override
	public void merge( final Component< T > component )
	{
		final MserComponentIntermediate< T > c = ( MserComponentIntermediate< T > ) component;
		pixelList.merge( c.pixelList );
		for ( int i = 0; i < sumPos.length; ++i )
			sumPos[ i ] += c.sumPos[ i ];
		for ( int i = 0; i < sumSquPos.length; ++i )
			sumSquPos[ i ] += c.sumSquPos[ i ];
		children.add( c );
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : ";
		boolean first = true;
		for ( final Localizable l : pixelList )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				s += ", ";
			}
			s += l.toString();
		}
		return s + "}";
	}

	/**
	 * Get the number of pixels in the component.
	 * 
	 * @return number of pixels in the component.
	 */
	long size()
	{
		return pixelList.size();
	}

	/**
	 * Get the {@link MserEvaluationNode} assigned to this
	 * {@link MserComponentIntermediate} when it was last emitted.
	 * 
	 * @return {@link MserEvaluationNode} last emitted from the component.
	 */
	MserEvaluationNode< T > getEvaluationNode()
	{
		return evaluationNode;
	}

	/**
	 * Set the {@link MserEvaluationNode} created from this
	 * {@link MserComponentIntermediate} when it is emitted.
	 */
	void setEvaluationNode( final MserEvaluationNode< T > node )
	{
		evaluationNode = node;
	}
}
